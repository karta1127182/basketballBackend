package com.hoopers.basketball.auth;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
	boolean existsByUserIdAndRole(Long userId, AppUser.Role role);
	long countByRole(AppUser.Role role);
	List<UserRole> findAllByUserId(Long userId);
	void deleteAllByUserIdAndRoleNotIn(Long userId, Collection<AppUser.Role> roles);
}
