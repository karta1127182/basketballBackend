package com.hoopers.basketball.profile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "player_stats")
public class PlayerStats {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private Long userId;

	private Integer gamesPlayed;
	private Double points;
	private Double rebounds;
	private Double assists;
	private Double fieldGoalPercentage;
	private Double threePointPercentage;
	private Double freeThrowPercentage;
	private Double steals;
	private Double blocks;
	private Integer heightCm;
	private Integer weightKg;

	@Column(length = 20)
	private String position;

	protected PlayerStats() {
	}

	public PlayerStats(
			Long userId,
			Integer gamesPlayed,
			Double points,
			Double rebounds,
			Double assists,
			Double fieldGoalPercentage,
			Double threePointPercentage,
			Double freeThrowPercentage,
			Double steals,
			Double blocks,
			Integer heightCm,
			Integer weightKg,
			String position) {
		this.userId = userId;
		this.gamesPlayed = gamesPlayed;
		this.points = points;
		this.rebounds = rebounds;
		this.assists = assists;
		this.fieldGoalPercentage = fieldGoalPercentage;
		this.threePointPercentage = threePointPercentage;
		this.freeThrowPercentage = freeThrowPercentage;
		this.steals = steals;
		this.blocks = blocks;
		this.heightCm = heightCm;
		this.weightKg = weightKg;
		this.position = position;
	}

	public Long getUserId() { return userId; }
	public Integer getGamesPlayed() { return gamesPlayed; }
	public Double getPoints() { return points; }
	public Double getRebounds() { return rebounds; }
	public Double getAssists() { return assists; }
	public Double getFieldGoalPercentage() { return fieldGoalPercentage; }
	public Double getThreePointPercentage() { return threePointPercentage; }
	public Double getFreeThrowPercentage() { return freeThrowPercentage; }
	public Double getSteals() { return steals; }
	public Double getBlocks() { return blocks; }
	public Integer getHeightCm() { return heightCm; }
	public Integer getWeightKg() { return weightKg; }
	public String getPosition() { return position; }
}
