package com.hoopers.basketball.course;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, Long> {
	boolean existsByCourseIdAndUserId(Long courseId, Long userId);
	boolean existsByCourseId(Long courseId);
	boolean existsByCourseIdAndCheckedInFalse(Long courseId);
	long countByCourseId(Long courseId);
	List<CourseRegistration> findAllByCourseIdOrderByCreatedAtAsc(Long courseId);
	void deleteAllByCourseId(Long courseId);
	List<CourseRegistration> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
