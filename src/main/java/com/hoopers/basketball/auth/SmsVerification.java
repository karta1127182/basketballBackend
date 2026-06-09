package com.hoopers.basketball.auth;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sms_verifications")
public class SmsVerification {

	@Id
	private UUID id;

	private String phone;
	private String code;
	private Instant expiresAt;
	private boolean used;

	protected SmsVerification() {
	}

	public SmsVerification(UUID id, String phone, String code, Instant expiresAt) {
		this.id = id;
		this.phone = phone;
		this.code = code;
		this.expiresAt = expiresAt;
	}

	public UUID getId() {
		return id;
	}

	public boolean matches(String phone, String code) {
		return !used && expiresAt.isAfter(Instant.now()) && this.phone.equals(phone) && this.code.equals(code);
	}

	public void markUsed() {
		used = true;
	}
}
