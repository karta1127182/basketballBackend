package com.hoopers.basketball.league;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hoopers.basketball.league.LeagueDtos.MemberRequest;
import com.hoopers.basketball.league.LeagueDtos.MemberResponse;
import com.hoopers.basketball.league.LeagueDtos.PlayerGameStatsRequest;
import com.hoopers.basketball.league.LeagueDtos.PlayerGameStatsResponse;
import com.hoopers.basketball.league.LeagueDtos.ScheduleRequest;
import com.hoopers.basketball.league.LeagueDtos.ScheduleResponse;
import com.hoopers.basketball.league.LeagueDtos.TeamRequest;
import com.hoopers.basketball.league.LeagueDtos.TeamResponse;
import com.hoopers.basketball.notification.NotificationService;

@Service
public class LeagueService {

	private final TeamRepository teamRepository;
	private final LeagueScheduleRepository scheduleRepository;
	private final com.hoopers.basketball.auth.AppUserRepository userRepository;
	private final NotificationService notificationService;

	public LeagueService(TeamRepository teamRepository, LeagueScheduleRepository scheduleRepository, com.hoopers.basketball.auth.AppUserRepository userRepository, NotificationService notificationService) {
		this.teamRepository = teamRepository;
		this.scheduleRepository = scheduleRepository;
		this.userRepository = userRepository;
		this.notificationService = notificationService;
	}

	@Transactional(readOnly = true)
	public List<TeamResponse> findTeams() {
		return teamRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Transactional
	public TeamResponse createTeam(TeamRequest request) {
		if (teamRepository.findByName(request.name().trim()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "球隊名稱已存在");
		}
		return toResponse(teamRepository.save(new Team(request.name().trim())));
	}

	@Transactional
	public TeamResponse updateTeam(Long id, TeamRequest request) {
		Team team = findTeam(id);
		team.rename(request.name().trim());
		return toResponse(team);
	}

	@Transactional
	public TeamResponse addMember(Long id, MemberRequest request) {
		Team team = findTeam(id);
		Long userId = findMemberId(request);
		team.addMember(request.name().trim(), request.birthday(), userId);
		return toResponse(team);
	}

	@Transactional
	public TeamResponse removeMember(Long id, MemberRequest request) {
		Team team = findTeam(id);
		team.removeMember(request.name().trim(), request.birthday());
		return toResponse(team);
	}

	@Transactional
	public void deleteTeam(Long id) {
		Team team = findTeam(id);
		boolean usedBySchedule = scheduleRepository.findAll().stream()
				.anyMatch(game -> game.getHomeTeam().getId().equals(id) || game.getAwayTeam().getId().equals(id));
		if (usedBySchedule) throw new ResponseStatusException(HttpStatus.CONFLICT, "此球隊已被賽程使用，不能刪除");
		teamRepository.delete(team);
	}

	@Transactional(readOnly = true)
	public List<ScheduleResponse> findSchedules() {
		return scheduleRepository.findAllByOrderByGameDateAscGameTimeAsc().stream().map(this::toResponse).toList();
	}

	@Transactional
	public ScheduleResponse createSchedule(ScheduleRequest request) {
		LeagueSchedule schedule = new LeagueSchedule(
				request.date(), request.time(), request.venue().trim(),
				findTeam(request.homeTeamId()), findTeam(request.awayTeamId()),
				LeagueSchedule.Status.UPCOMING, null);
		schedule.update(
				request.date(), request.time(), request.venue().trim(),
				findTeam(request.homeTeamId()), findTeam(request.awayTeamId()),
				LeagueSchedule.Status.UPCOMING, null, trimToNull(request.liveYoutubeUrl()), trimToNull(request.replayYoutubeUrl()));
		schedule.replacePlayerStats(toPlayerStats(schedule, request.playerStats()));
		LeagueSchedule saved = scheduleRepository.save(schedule);
		notificationService.notifyAllUsers("GAME_REMINDER", "新增比賽賽程", matchTitle(saved) + " 已新增，時間：" + saved.getGameDate() + " " + saved.getGameTime() + "。", "SCHEDULE", saved.getId());
		return toResponse(saved);
	}

	@Transactional
	public ScheduleResponse updateSchedule(Long id, ScheduleRequest request) {
		LeagueSchedule schedule = scheduleRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到賽程"));
		LeagueSchedule.Status previousStatus = schedule.getStatus();
		schedule.update(
				request.date(), request.time(), request.venue().trim(),
				findTeam(request.homeTeamId()), findTeam(request.awayTeamId()),
				request.status(), request.score(), trimToNull(request.liveYoutubeUrl()), trimToNull(request.replayYoutubeUrl()));
		schedule.replacePlayerStats(toPlayerStats(schedule, request.playerStats()));
		if (schedule.getStatus() == LeagueSchedule.Status.LIVE && previousStatus != LeagueSchedule.Status.LIVE) {
			notificationService.notifyAllUsers("LIVE_STARTED", "直播開始", matchTitle(schedule) + " 直播已開始。", "SCHEDULE", schedule.getId());
		} else {
			notificationService.notifyAllUsers("SCHEDULE_CHANGED", "賽程異動", matchTitle(schedule) + " 賽程資訊已更新。", "SCHEDULE", schedule.getId());
		}
		return toResponse(schedule);
	}

	public void deleteSchedule(Long id) {
		if (!scheduleRepository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到賽程");
		scheduleRepository.deleteById(id);
	}

	private Team findTeam(Long id) {
		return teamRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到球隊"));
	}

	private TeamResponse toResponse(Team team) {
		List<MemberResponse> members = team.getMembers().stream()
				.map(member -> new MemberResponse(member.getId(), member.getUserId(), member.getName(), member.getBirthday(), member.getUserId() != null))
				.toList();
		return new TeamResponse(team.getId(), team.getName(), members);
	}

	private Long findMemberId(MemberRequest request) {
		return userRepository.findByNameAndBirthday(request.name().trim(), request.birthday())
				.map(com.hoopers.basketball.auth.AppUser::getId)
				.orElse(null);
	}

	private ScheduleResponse toResponse(LeagueSchedule schedule) {
		return new ScheduleResponse(
				schedule.getId(), schedule.getGameDate(), schedule.getGameTime(), schedule.getVenue(),
				schedule.getHomeTeam().getId(), schedule.getAwayTeam().getId(),
				schedule.getStatus(), schedule.getScore(), schedule.getLiveYoutubeUrl(), schedule.getReplayYoutubeUrl(),
				schedule.getPlayerStats().stream().map(stats -> new PlayerGameStatsResponse(
						stats.getTeamMemberId(), stats.getUserId(), stats.getPlayerName(),
						stats.getPoints(), stats.getTwoPointMade(), stats.getTwoPointAttempts(), stats.getTwoPointPercentage(),
						stats.getThreePointMade(), stats.getThreePointAttempts(), stats.getThreePointPercentage(),
						stats.getFreeThrowMade(), stats.getFreeThrowAttempts(), stats.getFreeThrowPercentage(),
						stats.getRebounds(), stats.getAssists(), stats.getSteals(), stats.getBlocks(),
						stats.getTurnovers(), stats.getFouls(), stats.getEfficiency(), stats.getMinutesPlayed()))
						.toList());
	}

	private List<SchedulePlayerStats> toPlayerStats(LeagueSchedule schedule, List<PlayerGameStatsRequest> requests) {
		if (requests == null) return List.of();
		List<TeamMember> members = schedule.getHomeTeam().getMembers().stream()
				.collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
		members.addAll(schedule.getAwayTeam().getMembers());
		return requests.stream().map(request -> {
			TeamMember member = members.stream()
					.filter(item -> item.getId().equals(request.teamMemberId()))
					.findFirst()
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "球員不屬於此場賽程的任一球隊"));
			return new SchedulePlayerStats(
					schedule, member.getId(), member.getUserId(), member.getName(),
					points(request), nonNegative(request.twoPointMade()), nonNegative(request.twoPointAttempts()),
					percentage(request.twoPointMade(), request.twoPointAttempts()), nonNegative(request.threePointMade()), nonNegative(request.threePointAttempts()),
					percentage(request.threePointMade(), request.threePointAttempts()), nonNegative(request.freeThrowMade()), nonNegative(request.freeThrowAttempts()),
					percentage(request.freeThrowMade(), request.freeThrowAttempts()), nonNegative(request.rebounds()), nonNegative(request.assists()),
					nonNegative(request.steals()), nonNegative(request.blocks()), nonNegative(request.turnovers()),
					nonNegative(request.fouls()), efficiency(request),
					request.minutesPlayed() == null ? "" : request.minutesPlayed().trim());
		}).toList();
	}

	private Integer nonNegative(Integer value) {
		if (value == null) return 0;
		if (value < 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "球員數據不能為負數");
		return value;
	}

	private Double nonNegative(Double value) {
		if (value == null) return 0.0;
		if (value < 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "球員數據不能為負數");
		return value;
	}

	private Double percentage(Integer madeValue, Integer attemptsValue) {
		int made = nonNegative(madeValue);
		int attempts = nonNegative(attemptsValue);
		if (made > attempts) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "命中數不能大於出手數");
		if (attempts == 0) return 0.0;
		return Math.round(made * 1000.0 / attempts) / 10.0;
	}

	private Integer points(PlayerGameStatsRequest request) {
		return nonNegative(request.twoPointMade()) * 2
				+ nonNegative(request.threePointMade()) * 3
				+ nonNegative(request.freeThrowMade());
	}

	private Double efficiency(PlayerGameStatsRequest request) {
		return (double) (points(request)
				+ nonNegative(request.rebounds())
				+ nonNegative(request.assists())
				+ nonNegative(request.steals())
				+ nonNegative(request.blocks())
				- missedShots(request.twoPointMade(), request.twoPointAttempts())
				- missedShots(request.threePointMade(), request.threePointAttempts())
				- missedShots(request.freeThrowMade(), request.freeThrowAttempts())
				- nonNegative(request.turnovers()));
	}

	private Integer missedShots(Integer madeValue, Integer attemptsValue) {
		int made = nonNegative(madeValue);
		int attempts = nonNegative(attemptsValue);
		if (made > attempts) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "命中數不能大於出手數");
		return attempts - made;
	}

	private String trimToNull(String value) {
		if (value == null || value.isBlank()) return null;
		return value.trim();
	}

	private String matchTitle(LeagueSchedule schedule) {
		return schedule.getHomeTeam().getName() + " VS " + schedule.getAwayTeam().getName();
	}

}
