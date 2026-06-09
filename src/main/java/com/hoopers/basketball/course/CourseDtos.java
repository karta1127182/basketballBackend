package com.hoopers.basketball.course;

import java.time.Instant;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class CourseDtos {

	private CourseDtos() {
	}

	public record CourseRequest(
			@NotBlank(message = "請輸入課程名稱") @Size(max = 120, message = "課程名稱最多 120 個字") String title,
			@NotBlank(message = "請輸入課程時間") @Size(max = 80, message = "課程時間最多 80 個字") String timeText,
			@NotBlank(message = "請輸入課程地點") @Size(max = 120, message = "課程地點最多 120 個字") String location,
			@NotBlank(message = "請輸入課程程度") @Size(max = 40, message = "課程程度最多 40 個字") String level,
			@Min(value = 1, message = "名額至少 1 人") @Max(value = 200, message = "名額最多 200 人") int capacity,
			@Min(value = 0, message = "價格不能小於 0") @Max(value = 200000, message = "價格最多 200000") int price,
			@Size(max = 20) String status,
			@NotBlank(message = "請輸入課程說明") @Size(max = 500, message = "課程說明最多 500 個字") String description) {
	}

	public record CourseResponse(
			Long id,
			String title,
			Long coachUserId,
			String coachName,
			String timeText,
			String location,
			String level,
			int capacity,
			int price,
			String status,
			long registrationCount,
			String description) {
	}

	public record RegistrationResponse(
			Long id,
			Long courseId,
			String courseTitle,
			Long userId,
			String name,
			String phone,
			String status,
			String paymentStatus,
			boolean checkedIn,
			Long orderId,
			Integer amount,
			Instant createdAt) {
	}

	public record CourseOrderResponse(
			Long orderId,
			Long registrationId,
			Long courseId,
			String courseTitle,
			String studentName,
			String studentPhone,
			int amount,
			String status,
			String paymentProvider,
			String receiptNo,
			Instant paidAt,
			Instant createdAt) {
	}

	public record CourseRevenueResponse(
			long courseCount,
			long registrationCount,
			long paidCount,
			long unpaidCount,
			long refundedCount,
			int paidRevenue,
			int expectedRevenue) {
	}

	public record CourseReviewRequest(
			@Min(value = 1, message = "評分至少 1 分") @Max(value = 5, message = "評分最多 5 分") int rating,
			@Size(max = 500, message = "留言最多 500 個字") String comment) {
	}

	public record CourseReviewResponse(
			Long id,
			Long courseId,
			String courseTitle,
			Long coachUserId,
			Long userId,
			String reviewerName,
			int rating,
			String comment,
			Instant createdAt) {
	}
}
