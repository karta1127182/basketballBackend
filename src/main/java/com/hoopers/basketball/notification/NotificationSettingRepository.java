package com.hoopers.basketball.notification;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
	Optional<NotificationSetting> findByUserId(Long userId);
}
