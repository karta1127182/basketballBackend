package com.hoopers.basketball.profile;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hoopers.basketball.auth.AppUser;
import com.hoopers.basketball.auth.AppUserRepository;
import com.hoopers.basketball.auth.PasswordHasher;
import com.hoopers.basketball.league.Team;
import com.hoopers.basketball.league.TeamMemberRepository;
import com.hoopers.basketball.league.TeamRepository;

@Component
@Order(2)
public class DemoMemberDataInitializer implements CommandLineRunner {

	public static final String DEMO_PHONE = "0911111111";
	public static final String DEMO_PASSWORD = "hoopers123";

	private final AppUserRepository userRepository;
	private final PasswordHasher passwordHasher;
	private final TeamRepository teamRepository;
	private final TeamMemberRepository memberRepository;
	private final PlayerStatsRepository statsRepository;

	public DemoMemberDataInitializer(
			AppUserRepository userRepository,
			PasswordHasher passwordHasher,
			TeamRepository teamRepository,
			TeamMemberRepository memberRepository,
			PlayerStatsRepository statsRepository) {
		this.userRepository = userRepository;
		this.passwordHasher = passwordHasher;
		this.teamRepository = teamRepository;
		this.memberRepository = memberRepository;
		this.statsRepository = statsRepository;
	}

	@Override
	@Transactional
	public void run(String... args) {
		LocalDate birthday = LocalDate.of(1998, 8, 18);
		AppUser member = userRepository.findByPhone(DEMO_PHONE)
				.orElseGet(() -> userRepository.save(new AppUser("王柏翔", DEMO_PHONE, passwordHasher.hash(DEMO_PASSWORD), birthday)));
		Team team = teamRepository.findByName("Hoopers apex")
				.orElseThrow(() -> new IllegalStateException("Demo team is missing"));
		if (memberRepository.findByUserId(member.getId()).isEmpty()) {
			team.addMember(member.getName(), birthday, member.getId());
			teamRepository.save(team);
		}
		if (statsRepository.findByUserId(member.getId()).isEmpty()) {
			statsRepository.save(new PlayerStats(
					member.getId(), 8, 18.6, 6.4, 4.8,
					52.3, 38.5, 81.2, 1.9, 0.7,
					183, 78, "得分後衛"));
		}
	}
}
