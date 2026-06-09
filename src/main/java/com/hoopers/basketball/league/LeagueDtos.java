package com.hoopers.basketball.league;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class LeagueDtos {

	private LeagueDtos() {
	}

	public record TeamRequest(@NotBlank @Size(max = 100) String name) {
	}

	public record MemberRequest(
			@NotBlank @Size(max = 100) String name,
			@NotNull LocalDate birthday) {
	}

	public record MemberResponse(Long id, Long userId, String name, LocalDate birthday, boolean linked) {
	}

	public record TeamResponse(Long id, String name, List<MemberResponse> members) {
	}

	public record ScheduleRequest(
			@NotNull LocalDate date,
			@NotNull LocalTime time,
			@NotBlank @Size(max = 120) String venue,
			@NotNull Long homeTeamId,
			@NotNull Long awayTeamId,
			@NotNull LeagueSchedule.Status status,
			@Size(max = 30) String score,
			@Size(max = 500) String liveYoutubeUrl,
			@Size(max = 500) String replayYoutubeUrl,
			List<PlayerGameStatsRequest> playerStats) {
	}

	public record PlayerGameStatsRequest(
			@NotNull Long teamMemberId,
			Integer twoPointMade,
			Integer twoPointAttempts,
			Integer threePointMade,
			Integer threePointAttempts,
			Integer freeThrowMade,
			Integer freeThrowAttempts,
			Integer rebounds,
			Integer assists,
			Integer steals,
			Integer blocks,
			Integer turnovers,
			Integer fouls,
			@Size(max = 10) String minutesPlayed) {
	}

	public record PlayerGameStatsResponse(
			Long teamMemberId,
			Long userId,
			String playerName,
			Integer points,
			Integer twoPointMade,
			Integer twoPointAttempts,
			Double twoPointPercentage,
			Integer threePointMade,
			Integer threePointAttempts,
			Double threePointPercentage,
			Integer freeThrowMade,
			Integer freeThrowAttempts,
			Double freeThrowPercentage,
			Integer rebounds,
			Integer assists,
			Integer steals,
			Integer blocks,
			Integer turnovers,
			Integer fouls,
			Double efficiency,
			String minutesPlayed) {
	}

	public record ScheduleResponse(
			Long id,
			LocalDate date,
			LocalTime time,
			String venue,
			Long homeTeamId,
			Long awayTeamId,
			LeagueSchedule.Status status,
			String score,
			String liveYoutubeUrl,
			String replayYoutubeUrl,
			List<PlayerGameStatsResponse> playerStats) {
	}
}
