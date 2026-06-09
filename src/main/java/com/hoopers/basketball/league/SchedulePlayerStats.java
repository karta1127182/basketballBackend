package com.hoopers.basketball.league;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "schedule_player_stats")
public class SchedulePlayerStats {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "schedule_id")
	private LeagueSchedule schedule;

	private Long teamMemberId;
	private Long userId;
	private String playerName;
	private Integer points;
	private Integer twoPointMade;
	private Integer twoPointAttempts;
	private Double twoPointPercentage;
	private Integer threePointMade;
	private Integer threePointAttempts;
	private Double threePointPercentage;
	private Integer freeThrowMade;
	private Integer freeThrowAttempts;
	private Double freeThrowPercentage;
	private Integer rebounds;
	private Integer assists;
	private Integer steals;
	private Integer blocks;
	private Integer turnovers;
	private Integer fouls;
	private Double efficiency;
	private String minutesPlayed;

	protected SchedulePlayerStats() {
	}

	public SchedulePlayerStats(
			LeagueSchedule schedule,
			Long teamMemberId,
			Long userId,
			String playerName,
			Integer points,
			Integer twoPointMade,
			Integer twoPointAttempts,
			Double twoPointPercentage,
			Integer threePointMade,
			Integer threePointAttempts,
			Double threePointPercentage,
			Integer freeThrowMade,
			Integer freeThrowAttempts,
			Double freeThrowPercentage,
			Integer rebounds,
			Integer assists,
			Integer steals,
			Integer blocks,
			Integer turnovers,
			Integer fouls,
			Double efficiency,
			String minutesPlayed) {
		this.schedule = schedule;
		this.teamMemberId = teamMemberId;
		this.userId = userId;
		this.playerName = playerName;
		this.points = points;
		this.twoPointMade = twoPointMade;
		this.twoPointAttempts = twoPointAttempts;
		this.twoPointPercentage = twoPointPercentage;
		this.threePointMade = threePointMade;
		this.threePointAttempts = threePointAttempts;
		this.threePointPercentage = threePointPercentage;
		this.freeThrowMade = freeThrowMade;
		this.freeThrowAttempts = freeThrowAttempts;
		this.freeThrowPercentage = freeThrowPercentage;
		this.rebounds = rebounds;
		this.assists = assists;
		this.steals = steals;
		this.blocks = blocks;
		this.turnovers = turnovers;
		this.fouls = fouls;
		this.efficiency = efficiency;
		this.minutesPlayed = minutesPlayed;
	}

	public Long getTeamMemberId() { return teamMemberId; }
	public Long getUserId() { return userId; }
	public String getPlayerName() { return playerName; }
	public Integer getPoints() { return points; }
	public Integer getTwoPointMade() { return twoPointMade; }
	public Integer getTwoPointAttempts() { return twoPointAttempts; }
	public Double getTwoPointPercentage() { return twoPointPercentage; }
	public Integer getThreePointMade() { return threePointMade; }
	public Integer getThreePointAttempts() { return threePointAttempts; }
	public Double getThreePointPercentage() { return threePointPercentage; }
	public Integer getFreeThrowMade() { return freeThrowMade; }
	public Integer getFreeThrowAttempts() { return freeThrowAttempts; }
	public Double getFreeThrowPercentage() { return freeThrowPercentage; }
	public Integer getRebounds() { return rebounds; }
	public Integer getAssists() { return assists; }
	public Integer getSteals() { return steals; }
	public Integer getBlocks() { return blocks; }
	public Integer getTurnovers() { return turnovers; }
	public Integer getFouls() { return fouls; }
	public Double getEfficiency() { return efficiency; }
	public String getMinutesPlayed() { return minutesPlayed; }
}
