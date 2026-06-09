package com.hoopers.basketball.auth;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_users")
public class AppUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 80)
	private String name;

	@Column(nullable = false, unique = true, length = 30)
	private String phone;

	@Column(nullable = false, length = 300)
	private String passwordHash;

	@Column(nullable = false)
	private Instant createdAt;

	private LocalDate birthday;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'MEMBER'")
	private Role role = Role.MEMBER;

	protected AppUser() {
	}

	public AppUser(String name, String phone, String passwordHash, LocalDate birthday) {
		this(name, phone, passwordHash, birthday, Role.MEMBER);
	}

	public AppUser(String name, String phone, String passwordHash, LocalDate birthday, Role role) {
		this.name = name;
		this.phone = phone;
		this.passwordHash = passwordHash;
		this.birthday = birthday;
		this.role = role == null ? Role.MEMBER : role;
		this.createdAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public Role getRole() {
		return role;
	}

	public LocalDate getBirthday() {
		return birthday;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void configureAsAdmin(String name, String passwordHash) {
		this.name = name;
		this.passwordHash = passwordHash;
		this.role = Role.ADMIN;
	}

	public void updateAccount(String name, String phone, LocalDate birthday, Role role, String passwordHash) {
		this.name = name;
		this.phone = phone;
		this.birthday = birthday;
		this.role = role;
		if (passwordHash != null) {
			this.passwordHash = passwordHash;
		}
	}

	public enum Role {
		ADMIN,
		COACH,
		MEMBER
	}
}
