package com.hoopers.basketball.coach;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.Size;

public final class CoachDtos {

	private CoachDtos() {
	}

	public record CoachProfileRequest(
			@Size(max = 500, message = "教練介紹最多 500 字") String bio,
			@Size(max = 300, message = "專長最多 300 字") String specialties,
			@Size(max = 2000, message = "照片網址最多 2000 字") String photoUrl) {
	}

	public record CoachProfileResponse(
			Long userId,
			String name,
			String bio,
			String specialties,
			String photoUrl,
			long courseCount,
			long registrationCount,
			long paidCount,
			int paidRevenue,
			double averageRating,
			long reviewCount,
			List<CoachRatingBreakdown> ratingBreakdown,
			List<CoachReviewSummary> reviews,
			List<CoachCourseSummary> courses,
			Instant updatedAt) {
	}

	public record CoachRatingBreakdown(int rating, long count) {
	}

	public record CoachReviewSummary(
			Long id,
			String courseTitle,
			String reviewerName,
			int rating,
			String comment,
			Instant createdAt) {
	}

	public record CoachCourseSummary(
			Long id,
			String title,
			String timeText,
			String location,
			int price,
			long registrationCount) {
	}
}
