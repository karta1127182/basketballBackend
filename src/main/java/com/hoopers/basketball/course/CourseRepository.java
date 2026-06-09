package com.hoopers.basketball.course;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
	List<Course> findAllByOrderByCreatedAtDesc();
	List<Course> findTop3ByOrderByCreatedAtDesc();
}
