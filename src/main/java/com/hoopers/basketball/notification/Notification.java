package com.hoopers.basketball.notification;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false, length = 40)
	private String type;

	@Column(nullable = false, length = 120)
	private String title;

	@Column(nullable = false, length = 500)
	private String body;

	@Column(length = 40)
	private String targetType;

	private Long targetId;

	@Column(nullable = false)
	private boolean readFlag;

	@Column(nullable = false)
	private Instant createdAt;

	protected Notification() {
	}

	public Notification(Long userId, String type, String title, String body, String targetType, Long targetId) {
		this.userId = userId;
		this.type = type;
		this.title = title;
		this.body = body;
		this.targetType = targetType;
		this.targetId = targetId;
		this.readFlag = false;
		this.createdAt = Instant.now();
	}

	public Long getId() { return id; }

	public Long getUserId() { return userId; }

	public String getType() { return type; }

	public String getTitle() { return title; }

	public String getBody() { return body; }

	public String getTargetType() { return targetType; }

	public Long getTargetId() { return targetId; }

	public boolean isReadFlag() { return readFlag; }

	public Instant getCreatedAt() { return createdAt; }

	public void markRead() {
		this.readFlag = true;
	}
}
