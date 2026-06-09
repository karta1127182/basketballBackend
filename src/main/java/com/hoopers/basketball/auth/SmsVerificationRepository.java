package com.hoopers.basketball.auth;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsVerificationRepository extends JpaRepository<SmsVerification, UUID> {
}
