package com.hoopers.basketball.stats;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hoopers.basketball.league.LeagueSchedule;
import com.hoopers.basketball.league.LeagueScheduleRepository;
import com.hoopers.basketball.league.SchedulePlayerStats;
import com.hoopers.basketball.league.Team;
import com.hoopers.basketball.league.TeamMember;
import com.hoopers.basketball.league.TeamRepository;
import com.hoopers.basketball.stats.StatsDtos.LeagueStatsResponse;
import com.hoopers.basketball.stats.StatsDtos.PlayerGameLogResponse;
import com.hoopers.basketball.stats.StatsDtos.PlayerRankResponse;
import com.hoopers.basketball.stats.StatsDtos.TeamStatsResponse;

@Service
public class StatsService {

	private final TeamRepository teamRepository;
	private final LeagueScheduleRepository scheduleRepository;

	public StatsService(TeamRepository teamRepository, LeagueScheduleRepository scheduleRepository) {
		this.teamRepository = teamRepository;
		this.scheduleRepository = scheduleRepository;
	}

	@Transactional(readOnly = true)
	public LeagueStatsResponse leagueStats() {
		List<Team> teams = teamRepository.findAll();
		List<LeagueSchedule> finalGames = scheduleRepository.findAll().stream()
				.filter(schedule -> schedule.getStatus() == LeagueSchedule.Status.FINAL)
				.toList();
		Map<Long, Team> teamByMemberId = new LinkedHashMap<>();
		teams.forEach(team -> team.getMembers().forEach(member -> teamByMemberId.put(member.getId(), team)));

		List<PlayerRankResponse> players = playerStats(finalGames, teamByMemberId);
		List<TeamStatsResponse> teamStats = teams.stream()
				.map(team -> teamStats(team, finalGames, players))
				.sorted(Comparator.comparingInt(TeamStatsResponse::wins).reversed().thenComparing(TeamStatsResponse::teamName))
				.toList();
		return new LeagueStatsResponse(teamStats, players.stream().limit(30).toList());
	}

	private TeamStatsResponse teamStats(Team team, List<LeagueSchedule> finalGames, List<PlayerRankResponse> allPlayers) {
		List<LeagueSchedule> games = finalGames.stream()
				.filter(schedule -> schedule.getHomeTeam().getId().equals(team.getId()) || schedule.getAwayTeam().getId().equals(team.getId()))
				.toList();
		int wins = 0;
		int losses = 0;
		for (LeagueSchedule game : games) {
			int[] score = parseScore(game.getScore());
			if (score == null) continue;
			boolean home = game.getHomeTeam().getId().equals(team.getId());
			int own = home ? score[0] : score[1];
			int other = home ? score[1] : score[0];
			if (own > other) wins++;
			if (own < other) losses++;
		}
		List<SchedulePlayerStats> teamPlayerStats = games.stream()
				.flatMap(game -> game.getPlayerStats().stream())
				.filter(stats -> team.getMembers().stream().anyMatch(member -> member.getId().equals(stats.getTeamMemberId())))
				.toList();
		List<PlayerRankResponse> roster = allPlayers.stream()
				.filter(player -> team.getId().equals(player.teamId()))
				.sorted(Comparator.comparingDouble(PlayerRankResponse::points).reversed())
				.toList();
		return new TeamStatsResponse(
				team.getId(),
				team.getName(),
				wins,
				losses,
				games.size(),
				average(teamPlayerStats.stream().mapToInt(stats -> value(stats.getPoints())).sum(), games.size()),
				average(teamPlayerStats.stream().mapToInt(stats -> value(stats.getRebounds())).sum(), games.size()),
				average(teamPlayerStats.stream().mapToInt(stats -> value(stats.getAssists())).sum(), games.size()),
				roster);
	}

	private List<PlayerRankResponse> playerStats(List<LeagueSchedule> finalGames, Map<Long, Team> teamByMemberId) {
		Map<Long, List<PlayerStatWithGame>> grouped = new LinkedHashMap<>();
		for (LeagueSchedule game : finalGames) {
			for (SchedulePlayerStats stat : game.getPlayerStats()) {
				grouped.computeIfAbsent(stat.getTeamMemberId(), ignored -> new ArrayList<>()).add(new PlayerStatWithGame(stat, game));
			}
		}
		return grouped.entrySet().stream()
				.map(entry -> playerRank(entry.getKey(), entry.getValue(), teamByMemberId.get(entry.getKey())))
				.sorted(Comparator.comparingDouble(PlayerRankResponse::points).reversed().thenComparing(PlayerRankResponse::playerName))
				.toList();
	}

	private PlayerRankResponse playerRank(Long teamMemberId, List<PlayerStatWithGame> stats, Team team) {
		int gamesPlayed = stats.size();
		SchedulePlayerStats first = stats.get(0).stats();
		List<PlayerGameLogResponse> logs = stats.stream()
				.sorted(Comparator.comparing(item -> item.game().getGameDate(), Comparator.reverseOrder()))
				.map(item -> new PlayerGameLogResponse(
						item.game().getId(),
						item.game().getGameDate(),
						opponentName(item.game(), team),
						item.stats().getPoints(),
						item.stats().getRebounds(),
						item.stats().getAssists(),
						item.stats().getSteals(),
						item.stats().getBlocks(),
						item.stats().getEfficiency()))
				.toList();
		return new PlayerRankResponse(
				teamMemberId,
				first.getUserId(),
				first.getPlayerName(),
				team == null ? null : team.getId(),
				team == null ? "未分隊" : team.getName(),
				gamesPlayed,
				average(stats.stream().mapToInt(item -> value(item.stats().getPoints())).sum(), gamesPlayed),
				average(stats.stream().mapToInt(item -> value(item.stats().getRebounds())).sum(), gamesPlayed),
				average(stats.stream().mapToInt(item -> value(item.stats().getAssists())).sum(), gamesPlayed),
				average(stats.stream().mapToInt(item -> value(item.stats().getSteals())).sum(), gamesPlayed),
				average(stats.stream().mapToInt(item -> value(item.stats().getBlocks())).sum(), gamesPlayed),
				average(stats.stream().mapToDouble(item -> value(item.stats().getEfficiency())).sum(), gamesPlayed),
				logs);
	}

	private String opponentName(LeagueSchedule game, Team team) {
		if (team == null) return "";
		return game.getHomeTeam().getId().equals(team.getId()) ? game.getAwayTeam().getName() : game.getHomeTeam().getName();
	}

	private int[] parseScore(String score) {
		if (score == null) return null;
		String[] parts = score.replace("：", "-").replace(":", "-").split("-");
		if (parts.length != 2) return null;
		try {
			return new int[] { Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()) };
		} catch (NumberFormatException ignored) {
			return null;
		}
	}

	private int value(Integer value) {
		return value == null ? 0 : value;
	}

	private double value(Double value) {
		return value == null ? 0 : value;
	}

	private double average(double total, int gamesPlayed) {
		if (gamesPlayed == 0) return 0;
		return Math.round(total * 10.0 / gamesPlayed) / 10.0;
	}

	private record PlayerStatWithGame(SchedulePlayerStats stats, LeagueSchedule game) {
	}
}
