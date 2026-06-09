package com.hoopers.basketball.auth;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoopers.basketball.auth.AdminAccountDtos.AccountResponse;
import com.hoopers.basketball.auth.AdminAccountDtos.UpdateAccountRequest;
import com.hoopers.basketball.auth.AppUser;

@RestController
@RequestMapping("/api/admin/accounts")
public class AdminAccountController {

	private final AdminAccountService accountService;
	private final AuthService authService;

	public AdminAccountController(AdminAccountService accountService, AuthService authService) {
		this.accountService = accountService;
		this.authService = authService;
	}

	@GetMapping
	public List<AccountResponse> accounts(@RequestHeader("X-Auth-Token") String token) {
		authService.requireAdmin(token);
		return accountService.findAccounts();
	}

	@PutMapping("/{id}")
	public AccountResponse updateAccount(
			@RequestHeader("X-Auth-Token") String token,
			@PathVariable Long id,
			@Valid @RequestBody UpdateAccountRequest request) {
		AppUser admin = authService.requireAdmin(token);
		return accountService.updateAccount(admin.getId(), id, request);
	}
}
