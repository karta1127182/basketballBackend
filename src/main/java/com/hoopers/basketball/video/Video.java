package com.hoopers.basketball.video;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "videos")
public class Video {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 160)
	private String title;

	@Column(nullable = false, length = 500)
	private String youtubeUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private Type type;

	@Column(length = 160)
	private String sourceName;

	private Long scheduleId;

	private Long teamId;

	private Long playerUserId;

	@Column(nullable = false)
	private Instant createdAt;

	protected Video() {
	}

	public Video(String title, String youtubeUrl, Type type, String sourceName) {
		update(title, youtubeUrl, type, sourceName, null, null, null);
		this.createdAt = Instant.now();
	}

	public Video(String title, String youtubeUrl, Type type, String sourceName, Long scheduleId, Long teamId, Long playerUserId) {
		update(title, youtubeUrl, type, sourceName, scheduleId, teamId, playerUserId);
		this.createdAt = Instant.now();
	}

	public void update(String title, String youtubeUrl, Type type, String sourceName, Long scheduleId, Long teamId, Long playerUserId) {
		this.title = title;
		this.youtubeUrl = youtubeUrl;
		this.type = type;
		this.sourceName = sourceName;
		this.scheduleId = scheduleId;
		this.teamId = teamId;
		this.playerUserId = playerUserId;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getYoutubeUrl() {
		return youtubeUrl;
	}

	public Type getType() {
		return type;
	}

	public String getSourceName() {
		return sourceName;
	}

	public Long getScheduleId() {
		return scheduleId;
	}

	public Long getTeamId() {
		return teamId;
	}

	public Long getPlayerUserId() {
		return playerUserId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public enum Type {
		LIVE,
		REPLAY,
		HIGHLIGHT,
		TRAINING
	}
}
