package com.hoopers.basketball.league;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "league_schedules")
public class LeagueSchedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDate gameDate;

	@Column(nullable = false)
	private LocalTime gameTime;

	@Column(nullable = false, length = 120)
	private String venue;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "home_team_id")
	private Team homeTeam;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "away_team_id")
	private Team awayTeam;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Status status;

	@Column(length = 30)
	private String score;

	@Column(length = 500)
	private String liveYoutubeUrl;

	@Column(length = 500)
	private String replayYoutubeUrl;

	@OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SchedulePlayerStats> playerStats = new ArrayList<>();

	protected LeagueSchedule() {
	}

	public LeagueSchedule(LocalDate gameDate, LocalTime gameTime, String venue, Team homeTeam, Team awayTeam, Status status, String score) {
		update(gameDate, gameTime, venue, homeTeam, awayTeam, status, score, null, null);
	}

	public void update(LocalDate gameDate, LocalTime gameTime, String venue, Team homeTeam, Team awayTeam, Status status, String score) {
		update(gameDate, gameTime, venue, homeTeam, awayTeam, status, score, liveYoutubeUrl, replayYoutubeUrl);
	}

	public void update(LocalDate gameDate, LocalTime gameTime, String venue, Team homeTeam, Team awayTeam, Status status, String score, String liveYoutubeUrl, String replayYoutubeUrl) {
		this.gameDate = gameDate;
		this.gameTime = gameTime;
		this.venue = venue;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.status = status;
		this.score = score;
		this.liveYoutubeUrl = liveYoutubeUrl;
		this.replayYoutubeUrl = replayYoutubeUrl;
	}

	public Long getId() { return id; }
	public LocalDate getGameDate() { return gameDate; }
	public LocalTime getGameTime() { return gameTime; }
	public String getVenue() { return venue; }
	public Team getHomeTeam() { return homeTeam; }
	public Team getAwayTeam() { return awayTeam; }
	public Status getStatus() { return status; }
	public String getScore() { return score; }
	public String getLiveYoutubeUrl() { return liveYoutubeUrl; }
	public String getReplayYoutubeUrl() { return replayYoutubeUrl; }
	public List<SchedulePlayerStats> getPlayerStats() { return playerStats; }

	public void replacePlayerStats(List<SchedulePlayerStats> nextStats) {
		playerStats.clear();
		playerStats.addAll(nextStats);
	}

	public enum Status {
		UPCOMING,
		LIVE,
		FINAL
	}
}
