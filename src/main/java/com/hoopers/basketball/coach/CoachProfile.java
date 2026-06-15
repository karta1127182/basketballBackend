package com.hoopers.basketball.coach;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "coach_profiles")
public class CoachProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private Long userId;

	@Column(nullable = false, length = 500)
	private String bio;

	@Column(nullable = false, length = 300)
	private String specialties;

	@Column(length = 2000)
	private String photoUrl;

	@Column(nullable = false)
	private Instant updatedAt;

	protected CoachProfile() {
	}

	public CoachProfile(Long userId) {
		this.userId = userId;
		this.bio = "";
		this.specialties = "";
		this.photoUrl = "";
		this.updatedAt = Instant.now();
	}

	public Long getUserId() { return userId; }

	public String getBio() { return bio; }

	public String getSpecialties() { return specialties; }

	public String getPhotoUrl() { return photoUrl; }

	public Instant getUpdatedAt() { return updatedAt; }

	public void update(String bio, String specialties, String photoUrl) {
		this.bio = bio == null ? "" : bio.trim();
		this.specialties = specialties == null ? "" : specialties.trim();
		this.photoUrl = photoUrl == null ? "" : photoUrl.trim();
		this.updatedAt = Instant.now();
	}
}
