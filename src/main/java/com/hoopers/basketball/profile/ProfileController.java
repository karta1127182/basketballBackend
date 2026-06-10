package com.hoopers.basketball.profile;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoopers.basketball.auth.AuthService;
import com.hoopers.basketball.auth.AppUser;
import com.hoopers.basketball.league.LeagueSchedule;
import com.hoopers.basketball.league.LeagueScheduleRepository;
import com.hoopers.basketball.league.SchedulePlayerStats;
import com.hoopers.basketball.league.TeamMember;
import com.hoopers.basketball.league.TeamMemberRepository;
import com.hoopers.basketball.profile.ProfileDtos.PlayerDashboardResponse;
import com.hoopers.basketball.profile.ProfileDtos.RecentScheduleResponse;
import com.hoopers.basketball.profile.ProfileDtos.PlayerStatsResponse;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

	private final AuthService authService;
	private final PlayerStatsRepository statsRepository;
	private final TeamMemberRepository memberRepository;
	private final LeagueScheduleRepository scheduleRepository;

	public ProfileController(
			AuthService authService,
			PlayerStatsRepository statsRepository,
			TeamMemberRepository memberRepository,
			LeagueScheduleRepository scheduleRepository) {
		this.authService = authService;
		this.statsRepository = statsRepository;
		this.memberRepository = memberRepository;
		this.scheduleRepository = scheduleRepository;
	}

	@GetMapping("/stats")
	@Transactional(readOnly = true)
	public ResponseEntity<PlayerStatsResponse> stats(@RequestHeader("X-Auth-Token") String token) {
		AppUser user = authService.requireUser(token);
		return ResponseEntity.ok(playerStats(user));
	}

	@GetMapping("/dashboard")
	@Transactional(readOnly = true)
	public ResponseEntity<PlayerDashboardResponse> dashboard(@RequestHeader("X-Auth-Token") String token) {
		AppUser user = authService.requireUser(token);
		TeamMember member = findMember(user);
		String teamName = member == null ? null : member.getTeam().getName();
		Long teamId = member == null ? null : member.getTeam().getId();
		List<RecentScheduleResponse> schedules = teamId == null ? List.of() : scheduleRepository.findAll().stream()
				.filter(schedule -> schedule.getHomeTeam().getId().equals(teamId) || schedule.getAwayTeam().getId().equals(teamId))
				.sorted(Comparator.comparing(LeagueSchedule::getGameDate).thenComparing(LeagueSchedule::getGameTime))
				.filter(schedule -> !schedule.getGameDate().isBefore(LocalDate.now()))
				.limit(3)
				.map(schedule -> new RecentScheduleResponse(
						schedule.getId(), schedule.getGameDate(), schedule.getGameTime(), schedule.getVenue(),
						schedule.getHomeTeam().getName(), schedule.getAwayTeam().getName(),
						schedule.getStatus().name(), schedule.getScore()))
				.toList();
		return ResponseEntity.ok(new PlayerDashboardResponse(teamName, playerStats(user), schedules));
	}

	private PlayerStatsResponse playerStats(AppUser user) {
		Long userId = user.getId();
		PlayerStats bio = statsRepository.findByUserId(userId).orElse(null);
		TeamMember member = findMember(user);
		Long memberId = member == null ? null : member.getId();
		List<SchedulePlayerStats> games = scheduleRepository.findAll().stream()
				.filter(schedule -> schedule.getStatus() == LeagueSchedule.Status.FINAL)
				.flatMap(schedule -> schedule.getPlayerStats().stream())
				.filter(stats -> userId.equals(stats.getUserId()) || (memberId != null && memberId.equals(stats.getTeamMemberId())))
				.toList();
		int gamesPlayed = games.size();
		int twoPointMade = games.stream().mapToInt(stats -> value(stats.getTwoPointMade())).sum();
		int twoPointAttempts = games.stream().mapToInt(stats -> value(stats.getTwoPointAttempts())).sum();
		int threePointMade = games.stream().mapToInt(stats -> value(stats.getThreePointMade())).sum();
		int threePointAttempts = games.stream().mapToInt(stats -> value(stats.getThreePointAttempts())).sum();
		int freeThrowMade = games.stream().mapToInt(stats -> value(stats.getFreeThrowMade())).sum();
		int freeThrowAttempts = games.stream().mapToInt(stats -> value(stats.getFreeThrowAttempts())).sum();
		return new PlayerStatsResponse(
				gamesPlayed,
				average(games.stream().mapToInt(stats -> value(stats.getPoints())).sum(), gamesPlayed),
				average(games.stream().mapToInt(stats -> value(stats.getRebounds())).sum(), gamesPlayed),
				average(games.stream().mapToInt(stats -> value(stats.getAssists())).sum(), gamesPlayed),
				percentage(twoPointMade + threePointMade, twoPointAttempts + threePointAttempts),
				percentage(threePointMade, threePointAttempts),
				percentage(freeThrowMade, freeThrowAttempts),
				average(games.stream().mapToInt(stats -> value(stats.getSteals())).sum(), gamesPlayed),
				average(games.stream().mapToInt(stats -> value(stats.getBlocks())).sum(), gamesPlayed),
				bio == null ? 0 : value(bio.getHeightCm()),
				bio == null ? 0 : value(bio.getWeightKg()),
				bio == null || bio.getPosition() == null ? "未設定" : bio.getPosition());
	}

	private TeamMember findMember(AppUser user) {
		return memberRepository.findFirstByUserIdOrderByIdAsc(user.getId())
				.orElseGet(() -> memberRepository.findAllByNameAndBirthday(user.getName(), user.getBirthday()).stream()
						.findFirst()
						.orElse(null));
	}

	private int value(Integer value) {
		return value == null ? 0 : value;
	}

	private double average(int total, int gamesPlayed) {
		if (gamesPlayed == 0) return 0.0;
		return Math.round(total * 10.0 / gamesPlayed) / 10.0;
	}

	private double percentage(int made, int attempts) {
		if (attempts == 0) return 0.0;
		return Math.round(made * 1000.0 / attempts) / 10.0;
	}
}
