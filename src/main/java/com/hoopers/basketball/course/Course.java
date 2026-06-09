package com.hoopers.basketball.course;

import java.time.Instant;

import com.hoopers.basketball.auth.AppUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String title;

	@Column(nullable = false)
	private Long coachUserId;

	@Column(nullable = false, length = 80)
	private String coachName;

	@Column(nullable = false, length = 80)
	private String timeText;

	@Column(nullable = false, length = 120, columnDefinition = "varchar(120) default ''")
	private String location = "";

	@Column(nullable = false, length = 40)
	private String level;

	@Column(nullable = false)
	private int capacity;

	@Column(nullable = false, columnDefinition = "integer default 0")
	private int price;

	@Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'OPEN'")
	private String status = "OPEN";

	@Column(nullable = false, length = 500)
	private String description;

	@Column(nullable = false)
	private Instant createdAt;

	protected Course() {
	}

	public Course(String title, AppUser coach, String timeText, String level, int capacity, String description) {
		this(title, coach, timeText, "", level, capacity, 0, "OPEN", description);
	}

	public Course(String title, AppUser coach, String timeText, String level, int capacity, int price, String status, String description) {
		this(title, coach, timeText, "", level, capacity, price, status, description);
	}

	public Course(String title, AppUser coach, String timeText, String location, String level, int capacity, int price, String status, String description) {
		this.title = title;
		this.coachUserId = coach.getId();
		this.coachName = coach.getName();
		this.timeText = timeText;
		this.location = location == null ? "" : location;
		this.level = level;
		this.capacity = capacity;
		this.price = price;
		this.status = status == null ? "OPEN" : status;
		this.description = description;
		this.createdAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Long getCoachUserId() {
		return coachUserId;
	}

	public String getCoachName() {
		return coachName;
	}

	public String getTimeText() {
		return timeText;
	}

	public String getLocation() {
		return location;
	}

	public String getLevel() {
		return level;
	}

	public int getCapacity() {
		return capacity;
	}

	public int getPrice() {
		return price;
	}

	public String getStatus() {
		return status;
	}

	public String getDescription() {
		return description;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
