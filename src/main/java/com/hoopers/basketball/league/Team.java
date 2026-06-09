package com.hoopers.basketball.league;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "teams")
public class Team {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 100)
	private String name;

	@OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TeamMember> members = new ArrayList<>();

	protected Team() {
	}

	public Team(String name) {
		this.name = name;
	}

	public Long getId() { return id; }
	public String getName() { return name; }
	public List<TeamMember> getMembers() { return members; }
	public void rename(String name) { this.name = name; }

	public void addMember(String name, java.time.LocalDate birthday, Long userId) {
		TeamMember existing = members.stream().filter(member -> member.matches(name, birthday)).findFirst().orElse(null);
		if (existing == null) {
			members.add(new TeamMember(this, name, birthday, userId));
		} else if (userId != null) {
			existing.linkUser(userId);
		}
	}

	public void removeMember(String name, java.time.LocalDate birthday) {
		members.removeIf(member -> member.matches(name, birthday));
	}
}
