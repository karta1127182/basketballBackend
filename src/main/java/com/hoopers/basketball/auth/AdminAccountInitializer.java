package com.hoopers.basketball.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(0)
public class AdminAccountInitializer implements CommandLineRunner {

	private static final String ADMIN_NAME = "系統管理者";
	private static final String ADMIN_PHONE = "0900000000";
	private static final String ADMIN_PASSWORD = "1qaz@WSX3edc";

	private final AppUserRepository userRepository;
	private final PasswordHasher passwordHasher;
	private final UserRoleRepository roleRepository;

	public AdminAccountInitializer(AppUserRepository userRepository, PasswordHasher passwordHasher, UserRoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.passwordHasher = passwordHasher;
		this.roleRepository = roleRepository;
	}

	@Override
	@Transactional
	public void run(String... args) {
		String passwordHash = passwordHasher.hash(ADMIN_PASSWORD);
		AppUser admin = userRepository.findByPhone(ADMIN_PHONE)
				.orElseGet(() -> new AppUser(ADMIN_NAME, ADMIN_PHONE, passwordHash, null));
		admin.configureAsAdmin(ADMIN_NAME, passwordHash);
		AppUser savedAdmin = userRepository.save(admin);
		grantRole(savedAdmin.getId(), AppUser.Role.MEMBER);
		grantRole(savedAdmin.getId(), AppUser.Role.ADMIN);
	}

	private void grantRole(Long userId, AppUser.Role role) {
		if (!roleRepository.existsByUserIdAndRole(userId, role)) {
			roleRepository.save(new UserRole(userId, role));
		}
	}
}
