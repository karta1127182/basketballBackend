package com.hoopers.basketball.notification;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hoopers.basketball.auth.AppUser;
import com.hoopers.basketball.auth.AppUserRepository;
import com.hoopers.basketball.notification.NotificationDtos.NotificationFeedResponse;
import com.hoopers.basketball.notification.NotificationDtos.NotificationResponse;
import com.hoopers.basketball.notification.NotificationDtos.NotificationSettingRequest;
import com.hoopers.basketball.notification.NotificationDtos.NotificationSettingResponse;

@Service
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final NotificationSettingRepository settingRepository;
	private final AppUserRepository userRepository;

	public NotificationService(NotificationRepository notificationRepository, NotificationSettingRepository settingRepository, AppUserRepository userRepository) {
		this.notificationRepository = notificationRepository;
		this.settingRepository = settingRepository;
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public NotificationFeedResponse findFeed(AppUser user) {
		List<NotificationResponse> notifications = notificationRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId()).stream()
				.map(this::toResponse)
				.toList();
		return new NotificationFeedResponse(notificationRepository.countByUserIdAndReadFlagFalse(user.getId()), notifications);
	}

	@Transactional
	public NotificationResponse markRead(Long id, AppUser user) {
		Notification notification = notificationRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到通知"));
		if (!notification.getUserId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只能讀取自己的通知");
		}
		notification.markRead();
		return toResponse(notification);
	}

	@Transactional(readOnly = true)
	public NotificationSettingResponse findSettings(AppUser user) {
		return toSettingResponse(settingFor(user.getId()));
	}

	@Transactional
	public NotificationSettingResponse updateSettings(AppUser user, NotificationSettingRequest request) {
		NotificationSetting setting = settingFor(user.getId());
		setting.update(request.courseRemindersEnabled(), request.gameRemindersEnabled(), request.liveNotificationsEnabled());
		return toSettingResponse(setting);
	}

	@Transactional
	public void notifyUser(Long userId, String type, String title, String body, String targetType, Long targetId) {
		if (!isEnabled(userId, type)) {
			return;
		}
		notificationRepository.save(new Notification(userId, type, title, body, targetType, targetId));
	}

	@Transactional
	public void notifyAllUsers(String type, String title, String body, String targetType, Long targetId) {
		userRepository.findAll().forEach(user -> notifyUser(user.getId(), type, title, body, targetType, targetId));
	}

	private NotificationResponse toResponse(Notification notification) {
		return new NotificationResponse(
				notification.getId(),
				notification.getType(),
				notification.getTitle(),
				notification.getBody(),
				notification.getTargetType(),
				notification.getTargetId(),
				notification.isReadFlag(),
				notification.getCreatedAt());
	}

	private NotificationSetting settingFor(Long userId) {
		return settingRepository.findByUserId(userId)
				.orElseGet(() -> settingRepository.save(new NotificationSetting(userId)));
	}

	private NotificationSettingResponse toSettingResponse(NotificationSetting setting) {
		return new NotificationSettingResponse(
				setting.isCourseRemindersEnabled(),
				setting.isGameRemindersEnabled(),
				setting.isLiveNotificationsEnabled());
	}

	private boolean isEnabled(Long userId, String type) {
		NotificationSetting setting = settingFor(userId);
		return switch (type) {
			case "COURSE_REMINDER" -> setting.isCourseRemindersEnabled();
			case "GAME_REMINDER", "SCHEDULE_CHANGED" -> setting.isGameRemindersEnabled();
			case "LIVE_STARTED" -> setting.isLiveNotificationsEnabled();
			default -> true;
		};
	}
}
