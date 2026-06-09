package com.hoopers.basketball.auth;

import java.util.UUID;
import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

	private AuthDtos() {
	}

	public record CaptchaResponse(UUID captchaId, String question) {
	}

	public record SendSmsRequest(
			@NotBlank(message = "請輸入手機號碼")
			@Pattern(regexp = "^[0-9+ -]{8,20}$", message = "手機號碼格式不正確") String phone,
			UUID captchaId,
			@NotBlank(message = "請輸入圖形驗證答案") String captchaAnswer) {
	}

	public record SendSmsResponse(UUID verificationId, int expiresInSeconds, String developmentCode) {
	}

	public record RegisterRequest(
			@NotBlank(message = "請輸入姓名")
			@Size(max = 80, message = "姓名最多 80 個字") String name,
			@NotNull(message = "請填寫生日") LocalDate birthday,
			@NotBlank(message = "請輸入手機號碼")
			@Pattern(regexp = "^[0-9+ -]{8,20}$", message = "手機號碼格式不正確") String phone,
			@NotBlank(message = "請輸入密碼")
			@Size(min = 8, max = 72, message = "密碼長度需為 8 到 72 個字") String password,
			AppUser.Role role,
			UUID verificationId,
			@NotBlank(message = "請輸入簡訊驗證碼") String smsCode) {
	}

	public record LoginRequest(
			@NotBlank(message = "請輸入手機號碼") String phone,
			@NotBlank(message = "請輸入密碼") String password) {
	}

	public record AuthResponse(Long userId, String name, String phone, String token, AppUser.Role role, Set<AppUser.Role> roles) {
	}
}
