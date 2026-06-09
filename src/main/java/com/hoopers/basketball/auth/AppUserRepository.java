package com.hoopers.basketball.auth;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

	Optional<AppUser> findByPhone(String phone);

	Optional<AppUser> findByNameAndBirthday(String name, LocalDate birthday);

	boolean existsByPhone(String phone);

	long countByRole(AppUser.Role role);
}
