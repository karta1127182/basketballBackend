package com.hoopers.basketball.auth;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "user_roles", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "role" }))
public class UserRole {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private AppUser.Role role;

	@Column(nullable = false)
	private Instant createdAt;

	protected UserRole() {
	}

	public UserRole(Long userId, AppUser.Role role) {
		this.userId = userId;
		this.role = role;
		this.createdAt = Instant.now();
	}

	public AppUser.Role getRole() {
		return role;
	}
}
