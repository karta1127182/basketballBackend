package com.hoopers.basketball.stats;

import java.time.LocalDate;
import java.util.List;

public final class StatsDtos {

	private StatsDtos() {
	}

	public record LeagueStatsResponse(List<TeamStatsResponse> teams, List<PlayerRankResponse> players) {
	}

	public record TeamStatsResponse(
			Long teamId,
			String teamName,
			int wins,
			int losses,
			int gamesPlayed,
			double pointsPerGame,
			double reboundsPerGame,
			double assistsPerGame,
			List<PlayerRankResponse> roster) {
	}

	public record PlayerRankResponse(
			Long teamMemberId,
			Long userId,
			String playerName,
			Long teamId,
			String teamName,
			int gamesPlayed,
			double points,
			double rebounds,
			double assists,
			double steals,
			double blocks,
			double efficiency,
			List<PlayerGameLogResponse> gameLogs) {
	}

	public record PlayerGameLogResponse(
			Long scheduleId,
			LocalDate date,
			String opponent,
			Integer points,
			Integer rebounds,
			Integer assists,
			Integer steals,
			Integer blocks,
			Double efficiency) {
	}
}
