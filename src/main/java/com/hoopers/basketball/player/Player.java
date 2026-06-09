package com.hoopers.basketball.player;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "players")
public class Player {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String team;
	private String position;
	private Integer jerseyNumber;

	protected Player() {
	}

	public Player(String name, String team, String position, Integer jerseyNumber) {
		this.name = name;
		this.team = team;
		this.position = position;
		this.jerseyNumber = jerseyNumber;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getTeam() {
		return team;
	}

	public String getPosition() {
		return position;
	}

	public Integer getJerseyNumber() {
		return jerseyNumber;
	}

	public void update(PlayerRequest request) {
		name = request.name();
		team = request.team();
		position = request.position();
		jerseyNumber = request.jerseyNumber();
	}
}
