package com.hoopers.basketball.stats;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoopers.basketball.stats.StatsDtos.LeagueStatsResponse;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

	private final StatsService statsService;

	public StatsController(StatsService statsService) {
		this.statsService = statsService;
	}

	@GetMapping("/league")
	public LeagueStatsResponse leagueStats() {
		return statsService.leagueStats();
	}
}
