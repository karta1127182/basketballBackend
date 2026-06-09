package com.hoopers.basketball.league;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class LeagueDataInitializer implements CommandLineRunner {

	private final TeamRepository teamRepository;
	private final LeagueScheduleRepository scheduleRepository;

	public LeagueDataInitializer(TeamRepository teamRepository, LeagueScheduleRepository scheduleRepository) {
		this.teamRepository = teamRepository;
		this.scheduleRepository = scheduleRepository;
	}

	@Override
	public void run(String... args) {
		Map<String, Team> teams = new LinkedHashMap<>();
		for (String name : new String[] {
				"Hoopers apex", "甜心老爹", "Hoopers x", "TCRJ",
				"矮人礦坑", "古亭妹GTM", "半吊子", "404 Not Founders" }) {
			teams.put(name, teamRepository.findByName(name).orElseGet(() -> teamRepository.save(new Team(name))));
		}
		if (scheduleRepository.count() == 0) {
			scheduleRepository.save(new LeagueSchedule(LocalDate.of(2026, 6, 7), LocalTime.of(14, 0), "台北體育館 A 場", teams.get("Hoopers apex"), teams.get("甜心老爹"), LeagueSchedule.Status.UPCOMING, null));
			scheduleRepository.save(new LeagueSchedule(LocalDate.of(2026, 6, 7), LocalTime.of(15, 30), "台北體育館 A 場", teams.get("Hoopers x"), teams.get("TCRJ"), LeagueSchedule.Status.UPCOMING, null));
			scheduleRepository.save(new LeagueSchedule(LocalDate.of(2026, 6, 14), LocalTime.of(14, 0), "古亭國中體育館", teams.get("矮人礦坑"), teams.get("古亭妹GTM"), LeagueSchedule.Status.UPCOMING, null));
			scheduleRepository.save(new LeagueSchedule(LocalDate.of(2026, 6, 14), LocalTime.of(15, 30), "古亭國中體育館", teams.get("半吊子"), teams.get("404 Not Founders"), LeagueSchedule.Status.UPCOMING, null));
		}
	}
}
