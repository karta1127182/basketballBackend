package com.hoopers.basketball.league;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamMemberLinkService {

	private final TeamMemberRepository memberRepository;

	public TeamMemberLinkService(TeamMemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Transactional
	public void syncUser(Long userId, String name, LocalDate birthday) {
		if (userId == null || name == null || birthday == null) return;
		String normalizedName = name.trim();
		List<TeamMember> currentLinks = sorted(memberRepository.findAllByUserId(userId));
		List<TeamMember> matches = sorted(memberRepository.findAllByNameAndBirthday(normalizedName, birthday));
		TeamMember selectedMember = currentLinks.stream()
				.filter(member -> member.matches(normalizedName, birthday))
				.findFirst()
				.or(() -> matches.stream().findFirst())
				.orElse(null);
		currentLinks.stream()
				.filter(member -> selectedMember == null || !member.getId().equals(selectedMember.getId()))
				.forEach(member -> member.linkUser(null));
		matches.stream()
				.filter(member -> selectedMember == null || !member.getId().equals(selectedMember.getId()))
				.filter(member -> userId.equals(member.getUserId()))
				.forEach(member -> member.linkUser(null));
		if (selectedMember != null) {
			selectedMember.linkUser(userId);
		}
	}

	@Transactional(readOnly = true)
	public Optional<TeamMember> findOtherTeamLink(Long userId, Long teamId) {
		if (userId == null || teamId == null) return Optional.empty();
		return sorted(memberRepository.findAllByUserId(userId)).stream()
				.filter(member -> !member.getTeam().getId().equals(teamId))
				.findFirst();
	}

	private List<TeamMember> sorted(List<TeamMember> members) {
		return members.stream()
				.sorted(Comparator.comparing(TeamMember::getId))
				.toList();
	}
}
