package com.hoopers.basketball.course;

import java.time.Instant;

import com.hoopers.basketball.auth.AppUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "course_registrations", uniqueConstraints = @UniqueConstraint(columnNames = { "course_id", "user_id" }))
public class CourseRegistration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(nullable = false, length = 80)
	private String name;

	@Column(nullable = false, length = 30)
	private String phone;

	@Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'REGISTERED'")
	private String status;

	@Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'UNPAID'")
	private String paymentStatus;

	@Column(nullable = false, columnDefinition = "boolean default false")
	private boolean checkedIn;

	@Column(nullable = false)
	private Instant createdAt;

	protected CourseRegistration() {
	}

	public CourseRegistration(Course course, AppUser user) {
		this.course = course;
		this.userId = user.getId();
		this.name = user.getName();
		this.phone = user.getPhone();
		this.status = "REGISTERED";
		this.paymentStatus = "UNPAID";
		this.checkedIn = false;
		this.createdAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public Course getCourse() {
		return course;
	}

	public Long getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public String getStatus() {
		return status;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public boolean isCheckedIn() {
		return checkedIn;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void markPaid() {
		this.paymentStatus = "PAID";
	}

	public void markRefunded() {
		this.paymentStatus = "REFUNDED";
	}

	public void checkIn() {
		this.checkedIn = true;
	}
}
