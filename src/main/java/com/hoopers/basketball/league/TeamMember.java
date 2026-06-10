package com.hoopers.basketball.league;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "team_roster_members")
public class TeamMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "team_id")
	private Team team;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false)
	private LocalDate birthday;

	private Long userId;

	protected TeamMember() {
	}

	public TeamMember(Team team, String name, LocalDate birthday, Long userId) {
		this.team = team;
		this.name = name;
		this.birthday = birthday;
		this.userId = userId;
	}

	public Long getId() { return id; }
	public Team getTeam() { return team; }
	public String getName() { return name; }
	public LocalDate getBirthday() { return birthday; }
	public Long getUserId() { return userId; }
	public void linkUser(Long userId) { this.userId = userId; }
	public boolean matches(String otherName, LocalDate otherBirthday) { return name.equals(otherName.trim()) && birthday.equals(otherBirthday); }
}
