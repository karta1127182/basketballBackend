package com.hoopers.basketball.course;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "course_reviews", uniqueConstraints = @UniqueConstraint(columnNames = { "registration_id" }))
public class CourseReview {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long courseId;

	@Column(nullable = false)
	private Long coachUserId;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private Long registrationId;

	@Column(nullable = false)
	private int rating;

	@Column(nullable = false, length = 500)
	private String comment;

	@Column(nullable = false)
	private Instant createdAt;

	protected CourseReview() {
	}

	public CourseReview(CourseRegistration registration, int rating, String comment) {
		this.courseId = registration.getCourse().getId();
		this.coachUserId = registration.getCourse().getCoachUserId();
		this.userId = registration.getUserId();
		this.registrationId = registration.getId();
		this.rating = rating;
		this.comment = comment;
		this.createdAt = Instant.now();
	}

	public Long getId() { return id; }

	public Long getCourseId() { return courseId; }

	public Long getCoachUserId() { return coachUserId; }

	public Long getUserId() { return userId; }

	public Long getRegistrationId() { return registrationId; }

	public int getRating() { return rating; }

	public String getComment() { return comment; }

	public Instant getCreatedAt() { return createdAt; }
}
