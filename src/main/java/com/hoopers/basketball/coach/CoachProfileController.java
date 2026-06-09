package com.hoopers.basketball.coach;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoopers.basketball.auth.AuthService;
import com.hoopers.basketball.coach.CoachDtos.CoachProfileRequest;
import com.hoopers.basketball.coach.CoachDtos.CoachProfileResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/coaches")
public class CoachProfileController {

	private final CoachProfileService profileService;
	private final AuthService authService;

	public CoachProfileController(CoachProfileService profileService, AuthService authService) {
		this.profileService = profileService;
		this.authService = authService;
	}

	@GetMapping
	public List<CoachProfileResponse> coaches() {
		return profileService.findCoaches();
	}

	@GetMapping("/{coachUserId}")
	public CoachProfileResponse coach(@PathVariable Long coachUserId) {
		return profileService.profileFor(coachUserId);
	}

	@GetMapping("/me")
	public CoachProfileResponse myProfile(@RequestHeader("X-Auth-Token") String token) {
		return profileService.profileFor(authService.requireCoachOrAdmin(token).getId());
	}

	@PutMapping("/me")
	public CoachProfileResponse updateMyProfile(
			@RequestHeader("X-Auth-Token") String token,
			@Valid @RequestBody CoachProfileRequest request) {
		return profileService.updateMyProfile(authService.requireCoachOrAdmin(token), request);
	}
}
