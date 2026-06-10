package com.hoopers.basketball.league;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

	Optional<TeamMember> findFirstByUserIdOrderByIdAsc(Long userId);

	List<TeamMember> findAllByUserId(Long userId);

	List<TeamMember> findAllByNameAndBirthday(String name, LocalDate birthday);
}
