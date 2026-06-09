package com.hoopers.basketball.league;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoopers.basketball.auth.AuthService;
import com.hoopers.basketball.league.LeagueDtos.MemberRequest;
import com.hoopers.basketball.league.LeagueDtos.ScheduleRequest;
import com.hoopers.basketball.league.LeagueDtos.ScheduleResponse;
import com.hoopers.basketball.league.LeagueDtos.TeamRequest;
import com.hoopers.basketball.league.LeagueDtos.TeamResponse;

@RestController
@RequestMapping("/api/league")
public class LeagueController {

	private final LeagueService leagueService;
	private final AuthService authService;

	public LeagueController(LeagueService leagueService, AuthService authService) {
		this.leagueService = leagueService;
		this.authService = authService;
	}

	@GetMapping("/teams")
	public List<TeamResponse> teams() {
		return leagueService.findTeams();
	}

	@PostMapping("/teams")
	public ResponseEntity<TeamResponse> createTeam(@RequestHeader("X-Auth-Token") String token, @Valid @RequestBody TeamRequest request) {
		authService.requireAdmin(token);
		TeamResponse team = leagueService.createTeam(request);
		return ResponseEntity.created(URI.create("/api/league/teams/" + team.id())).body(team);
	}

	@PutMapping("/teams/{id}")
	public TeamResponse updateTeam(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id, @Valid @RequestBody TeamRequest request) {
		authService.requireAdmin(token);
		return leagueService.updateTeam(id, request);
	}

	@PostMapping("/teams/{id}/members")
	public TeamResponse addMember(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id, @Valid @RequestBody MemberRequest request) {
		authService.requireAdmin(token);
		return leagueService.addMember(id, request);
	}

	@DeleteMapping("/teams/{id}/members")
	public TeamResponse removeMember(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id, @Valid @RequestBody MemberRequest request) {
		authService.requireAdmin(token);
		return leagueService.removeMember(id, request);
	}

	@DeleteMapping("/teams/{id}")
	public ResponseEntity<Void> deleteTeam(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
		authService.requireAdmin(token);
		leagueService.deleteTeam(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/schedules")
	public List<ScheduleResponse> schedules() {
		return leagueService.findSchedules();
	}

	@PostMapping("/schedules")
	public ResponseEntity<ScheduleResponse> createSchedule(@RequestHeader("X-Auth-Token") String token, @Valid @RequestBody ScheduleRequest request) {
		authService.requireAdmin(token);
		ScheduleResponse schedule = leagueService.createSchedule(request);
		return ResponseEntity.created(URI.create("/api/league/schedules/" + schedule.id())).body(schedule);
	}

	@PutMapping("/schedules/{id}")
	public ScheduleResponse updateSchedule(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id, @Valid @RequestBody ScheduleRequest request) {
		authService.requireAdmin(token);
		return leagueService.updateSchedule(id, request);
	}

	@DeleteMapping("/schedules/{id}")
	public ResponseEntity<Void> deleteSchedule(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
		authService.requireAdmin(token);
		leagueService.deleteSchedule(id);
		return ResponseEntity.noContent().build();
	}
}
