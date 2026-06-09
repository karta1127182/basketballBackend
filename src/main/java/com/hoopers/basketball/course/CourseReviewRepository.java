package com.hoopers.basketball.course;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
	boolean existsByRegistrationId(Long registrationId);
	List<CourseReview> findAllByCourseIdOrderByCreatedAtDesc(Long courseId);
	List<CourseReview> findAllByCoachUserIdOrderByCreatedAtDesc(Long coachUserId);
	List<CourseReview> findAllByUserIdOrderByCreatedAtDesc(Long userId);
	long countByCoachUserId(Long coachUserId);
}
