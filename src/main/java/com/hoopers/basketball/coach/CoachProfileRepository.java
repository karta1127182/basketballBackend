package com.hoopers.basketball.coach;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachProfileRepository extends JpaRepository<CoachProfile, Long> {
	Optional<CoachProfile> findByUserId(Long userId);
}
