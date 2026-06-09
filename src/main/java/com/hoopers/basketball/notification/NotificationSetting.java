package com.hoopers.basketball.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification_settings")
public class NotificationSetting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private Long userId;

	@Column(nullable = false)
	private boolean courseRemindersEnabled = true;

	@Column(nullable = false)
	private boolean gameRemindersEnabled = true;

	@Column(nullable = false)
	private boolean liveNotificationsEnabled = true;

	protected NotificationSetting() {
	}

	public NotificationSetting(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() { return userId; }

	public boolean isCourseRemindersEnabled() { return courseRemindersEnabled; }

	public boolean isGameRemindersEnabled() { return gameRemindersEnabled; }

	public boolean isLiveNotificationsEnabled() { return liveNotificationsEnabled; }

	public void update(boolean courseRemindersEnabled, boolean gameRemindersEnabled, boolean liveNotificationsEnabled) {
		this.courseRemindersEnabled = courseRemindersEnabled;
		this.gameRemindersEnabled = gameRemindersEnabled;
		this.liveNotificationsEnabled = liveNotificationsEnabled;
	}
}
