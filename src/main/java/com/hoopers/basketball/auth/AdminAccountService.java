package com.hoopers.basketball.auth;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hoopers.basketball.auth.AdminAccountDtos.AccountResponse;
import com.hoopers.basketball.auth.AdminAccountDtos.UpdateAccountRequest;

@Service
public class AdminAccountService {

	private final AppUserRepository userRepository;
	private final PasswordHasher passwordHasher;
	private final UserRoleRepository roleRepository;

	public AdminAccountService(AppUserRepository userRepository, PasswordHasher passwordHasher, UserRoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.passwordHasher = passwordHasher;
		this.roleRepository = roleRepository;
	}

	@Transactional(readOnly = true)
	public List<AccountResponse> findAccounts() {
		return userRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Transactional
	public AccountResponse updateAccount(Long adminUserId, Long id, UpdateAccountRequest request) {
		AppUser user = userRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到帳號"));
		Set<AppUser.Role> nextRoles = normalizeRoles(request.roles(), request.role());
		Set<AppUser.Role> currentRoles = rolesFor(user);
		if (adminUserId.equals(id) && !nextRoles.contains(AppUser.Role.ADMIN)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "不能移除自己的管理員身分");
		}
		String phone = normalizePhone(request.phone());
		userRepository.findByPhone(phone)
				.filter(existing -> !existing.getId().equals(id))
				.ifPresent(existing -> {
					throw new ResponseStatusException(HttpStatus.CONFLICT, "手機號碼已被註冊");
				});
		if (currentRoles.contains(AppUser.Role.ADMIN)
				&& !nextRoles.contains(AppUser.Role.ADMIN)
				&& roleRepository.countByRole(AppUser.Role.ADMIN) == 1) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "至少需要保留一位管理員");
		}
		String passwordHash = request.password() == null || request.password().isBlank()
				? null
				: passwordHasher.hash(request.password());
		user.updateAccount(request.name().trim(), phone, request.birthday(), primaryRole(nextRoles), passwordHash);
		syncRoles(user.getId(), nextRoles);
		return toResponse(user);
	}

	private AccountResponse toResponse(AppUser user) {
		Set<AppUser.Role> roles = rolesFor(user);
		return new AccountResponse(user.getId(), user.getName(), user.getPhone(), user.getBirthday(), primaryRole(roles), roles, user.getCreatedAt());
	}

	private String normalizePhone(String phone) {
		return phone.replace(" ", "").replace("-", "");
	}

	private Set<AppUser.Role> normalizeRoles(Set<AppUser.Role> requestedRoles, AppUser.Role fallbackRole) {
		Set<AppUser.Role> roles = new LinkedHashSet<>();
		roles.add(AppUser.Role.MEMBER);
		if (requestedRoles != null) {
			roles.addAll(requestedRoles);
		} else if (fallbackRole != null) {
			roles.add(fallbackRole);
		}
		return roles;
	}

	private Set<AppUser.Role> rolesFor(AppUser user) {
		Set<AppUser.Role> roles = new LinkedHashSet<>();
		roles.add(AppUser.Role.MEMBER);
		roles.add(user.getRole());
		roleRepository.findAllByUserId(user.getId()).forEach(userRole -> roles.add(userRole.getRole()));
		return roles;
	}

	private AppUser.Role primaryRole(Set<AppUser.Role> roles) {
		if (roles.contains(AppUser.Role.ADMIN)) return AppUser.Role.ADMIN;
		if (roles.contains(AppUser.Role.COACH)) return AppUser.Role.COACH;
		return AppUser.Role.MEMBER;
	}

	private void syncRoles(Long userId, Set<AppUser.Role> roles) {
		for (AppUser.Role role : roles) {
			if (!roleRepository.existsByUserIdAndRole(userId, role)) {
				roleRepository.save(new UserRole(userId, role));
			}
		}
		roleRepository.deleteAllByUserIdAndRoleNotIn(userId, roles);
	}
}
