package com.hoopers.basketball.profile;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerStatsRepository extends JpaRepository<PlayerStats, Long> {

	Optional<PlayerStats> findByUserId(Long userId);
}
