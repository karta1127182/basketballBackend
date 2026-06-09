package com.hoopers.basketball.profile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class ProfileDtos {

	private ProfileDtos() {
	}

	public record PlayerStatsResponse(
			Integer gamesPlayed,
			Double points,
			Double rebounds,
			Double assists,
			Double fieldGoalPercentage,
			Double threePointPercentage,
			Double freeThrowPercentage,
			Double steals,
			Double blocks,
			Integer heightCm,
			Integer weightKg,
			String position) {
	}

	public record RecentScheduleResponse(
			Long id,
			LocalDate date,
			LocalTime time,
			String venue,
			String homeTeam,
			String awayTeam,
			String status,
			String score) {
	}

	public record PlayerDashboardResponse(
			String teamName,
			PlayerStatsResponse stats,
			List<RecentScheduleResponse> recentSchedules) {
	}
}
