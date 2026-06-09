package com.hoopers.basketball.auth;

import java.time.LocalDate;
import java.time.Instant;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class AdminAccountDtos {

	private AdminAccountDtos() {
	}

	public record AccountResponse(
			Long id,
			String name,
			String phone,
			LocalDate birthday,
			AppUser.Role role,
			Set<AppUser.Role> roles,
			Instant createdAt) {
	}

	public record UpdateAccountRequest(
			@NotBlank(message = "請輸入姓名")
			@Size(max = 80, message = "姓名最多 80 個字") String name,
			@NotBlank(message = "請輸入手機號碼")
			@Pattern(regexp = "^[0-9+ -]{8,20}$", message = "手機號碼格式不正確") String phone,
			LocalDate birthday,
			AppUser.Role role,
			Set<AppUser.Role> roles,
			@Size(min = 8, max = 72, message = "密碼長度需為 8 到 72 個字") String password) {
	}
}
