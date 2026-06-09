package com.hoopers.basketball.notification;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoopers.basketball.auth.AuthService;
import com.hoopers.basketball.notification.NotificationDtos.NotificationFeedResponse;
import com.hoopers.basketball.notification.NotificationDtos.NotificationResponse;
import com.hoopers.basketball.notification.NotificationDtos.NotificationSettingRequest;
import com.hoopers.basketball.notification.NotificationDtos.NotificationSettingResponse;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	private final NotificationService notificationService;
	private final AuthService authService;

	public NotificationController(NotificationService notificationService, AuthService authService) {
		this.notificationService = notificationService;
		this.authService = authService;
	}

	@GetMapping
	public NotificationFeedResponse notifications(@RequestHeader("X-Auth-Token") String token) {
		return notificationService.findFeed(authService.requireUser(token));
	}

	@PostMapping("/{id}/read")
	public NotificationResponse markRead(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
		return notificationService.markRead(id, authService.requireUser(token));
	}

	@GetMapping("/settings")
	public NotificationSettingResponse settings(@RequestHeader("X-Auth-Token") String token) {
		return notificationService.findSettings(authService.requireUser(token));
	}

	@PutMapping("/settings")
	public NotificationSettingResponse updateSettings(
			@RequestHeader("X-Auth-Token") String token,
			@RequestBody NotificationSettingRequest request) {
		return notificationService.updateSettings(authService.requireUser(token), request);
	}
}
