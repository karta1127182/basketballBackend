package com.hoopers.basketball.auth;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoopers.basketball.auth.AuthDtos.AuthResponse;
import com.hoopers.basketball.auth.AuthDtos.CaptchaResponse;
import com.hoopers.basketball.auth.AuthDtos.LoginRequest;
import com.hoopers.basketball.auth.AuthDtos.RegisterRequest;
import com.hoopers.basketball.auth.AuthDtos.SendSmsRequest;
import com.hoopers.basketball.auth.AuthDtos.SendSmsResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@GetMapping("/captcha")
	public CaptchaResponse captcha() {
		return authService.createCaptcha();
	}

	@PostMapping("/sms/send")
	public SendSmsResponse sendSms(@Valid @RequestBody SendSmsRequest request) {
		return authService.sendSms(request);
	}

	@PostMapping("/register")
	public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
		return authService.register(request);
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@GetMapping("/me")
	public AuthResponse me(@RequestHeader("X-Auth-Token") String token) {
		AppUser user = authService.requireUser(token);
		return authService.responseFor(user, token);
	}

	@PostMapping("/logout")
	public void logout(@RequestHeader("X-Auth-Token") String token) {
		authService.logout(token);
	}
}
