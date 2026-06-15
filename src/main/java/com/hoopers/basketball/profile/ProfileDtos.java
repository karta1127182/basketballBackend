package com.hoopers.basketball.profile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

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
			String position,
			String photoUrl) {
	}

	public record PlayerProfileRequest(
			@Min(value = 0, message = "身高不可小於 0")
			@Max(value = 260, message = "身高不可大於 260")
			Integer heightCm,
			@Min(value = 0, message = "體重不可小於 0")
			@Max(value = 250, message = "體重不可大於 250")
			Integer weightKg,
			@Size(max = 20, message = "位置最多 20 字") String position,
			@Size(max = 2000, message = "照片網址最多 2000 字") String photoUrl) {
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

	public record CourseSeriesResponse(
			Long registrationId,
			Long courseId,
			String title,
			String coachName,
			String timeText,
			String location,
			String level,
			String paymentStatus,
			boolean checkedIn,
			int capacity,
			long registrationCount,
			int price) {
	}

	public record PlayerDashboardResponse(
			String profileType,
			String teamName,
			PlayerStatsResponse stats,
			List<RecentScheduleResponse> recentSchedules,
			List<CourseSeriesResponse> courseSeries) {
	}
}
