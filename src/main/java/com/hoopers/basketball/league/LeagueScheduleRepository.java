package com.hoopers.basketball.league;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LeagueScheduleRepository extends JpaRepository<LeagueSchedule, Long> {

	List<LeagueSchedule> findAllByOrderByGameDateAscGameTimeAsc();
	List<LeagueSchedule> findTop3ByStatusOrderByGameDateAscGameTimeAsc(LeagueSchedule.Status status);
	List<LeagueSchedule> findTop3ByStatusOrderByGameDateDescGameTimeDesc(LeagueSchedule.Status status);
}
