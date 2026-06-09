package com.hoopers.basketball.notification;

import java.time.Instant;
import java.util.List;

public final class NotificationDtos {

	private NotificationDtos() {
	}

	public record NotificationResponse(
			Long id,
			String type,
			String title,
			String body,
			String targetType,
			Long targetId,
			boolean read,
			Instant createdAt) {
	}

	public record NotificationFeedResponse(long unreadCount, List<NotificationResponse> notifications) {
	}

	public record NotificationSettingResponse(
			boolean courseRemindersEnabled,
			boolean gameRemindersEnabled,
			boolean liveNotificationsEnabled) {
	}

	public record NotificationSettingRequest(
			boolean courseRemindersEnabled,
			boolean gameRemindersEnabled,
			boolean liveNotificationsEnabled) {
	}
}
