package com.hoopers.basketball.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hoopers.basketball.auth.AuthDtos.AuthResponse;
import com.hoopers.basketball.auth.AuthDtos.CaptchaResponse;
import com.hoopers.basketball.auth.AuthDtos.LoginRequest;
import com.hoopers.basketball.auth.AuthDtos.RegisterRequest;
import com.hoopers.basketball.auth.AuthDtos.SendSmsRequest;
import com.hoopers.basketball.auth.AuthDtos.SendSmsResponse;
import com.hoopers.basketball.league.TeamMemberLinkService;

@Service
public class AuthService {

	private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
	private static final int SMS_EXPIRY_SECONDS = 300;

	private final AppUserRepository userRepository;
	private final SmsVerificationRepository verificationRepository;
	private final PasswordHasher passwordHasher;
	private final TeamMemberLinkService memberLinkService;
	private final UserRoleRepository roleRepository;
	private final Map<UUID, CaptchaChallenge> captchas = new ConcurrentHashMap<>();
	private final Map<String, Long> sessions = new ConcurrentHashMap<>();
	private final boolean developmentMode;

	public AuthService(
			AppUserRepository userRepository,
			SmsVerificationRepository verificationRepository,
			PasswordHasher passwordHasher,
			TeamMemberLinkService memberLinkService,
			UserRoleRepository roleRepository,
			@Value("${app.sms.development-mode:true}") boolean developmentMode) {
		this.userRepository = userRepository;
		this.verificationRepository = verificationRepository;
		this.passwordHasher = passwordHasher;
		this.memberLinkService = memberLinkService;
		this.roleRepository = roleRepository;
		this.developmentMode = developmentMode;
	}

	public CaptchaResponse createCaptcha() {
		int first = ThreadLocalRandom.current().nextInt(1, 10);
		int second = ThreadLocalRandom.current().nextInt(1, 10);
		UUID id = UUID.randomUUID();
		captchas.put(id, new CaptchaChallenge(String.valueOf(first + second), Instant.now().plus(5, ChronoUnit.MINUTES)));
		return new CaptchaResponse(id, first + " + " + second + " = ?");
	}

	public SendSmsResponse sendSms(SendSmsRequest request) {
		verifyCaptcha(request.captchaId(), request.captchaAnswer());
		String code = String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
		UUID id = UUID.randomUUID();
		verificationRepository.save(new SmsVerification(
				id,
				normalizePhone(request.phone()),
				code,
				Instant.now().plus(SMS_EXPIRY_SECONDS, ChronoUnit.SECONDS)));
		logger.info("Development SMS code for {} is {}", request.phone(), code);
		return new SendSmsResponse(id, SMS_EXPIRY_SECONDS, developmentMode ? code : null);
	}

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		String phone = normalizePhone(request.phone());
		AppUser.Role role = request.role() == AppUser.Role.COACH ? AppUser.Role.COACH : AppUser.Role.MEMBER;
		if (userRepository.existsByPhone(phone)) {
			throw new AuthException(HttpStatus.CONFLICT, "手機號碼已被註冊");
		}
		SmsVerification verification = verificationRepository.findById(request.verificationId())
				.orElseThrow(() -> new AuthException(HttpStatus.BAD_REQUEST, "簡訊驗證無效"));
		if (!verification.matches(phone, request.smsCode())) {
			throw new AuthException(HttpStatus.BAD_REQUEST, "簡訊驗證碼錯誤或已過期");
		}
		verification.markUsed();
		AppUser user = userRepository.save(new AppUser(request.name().trim(), phone, passwordHasher.hash(request.password()), request.birthday(), role));
		grantRole(user.getId(), AppUser.Role.MEMBER);
		if (role == AppUser.Role.COACH) {
			grantRole(user.getId(), AppUser.Role.COACH);
		}
		memberLinkService.syncUser(user.getId(), user.getName(), user.getBirthday());
		return responseFor(user);
	}

	public AuthResponse login(LoginRequest request) {
		AppUser user = userRepository.findByPhone(normalizePhone(request.phone()))
				.orElseThrow(() -> new AuthException(HttpStatus.UNAUTHORIZED, "手機號碼或密碼錯誤"));
		if (!passwordHasher.matches(request.password(), user.getPasswordHash())) {
			throw new AuthException(HttpStatus.UNAUTHORIZED, "手機號碼或密碼錯誤");
		}
		return responseFor(user);
	}

	private void verifyCaptcha(UUID captchaId, String answer) {
		if (captchaId == null) {
			throw new AuthException(HttpStatus.BAD_REQUEST, "請先完成圖形驗證");
		}
		CaptchaChallenge challenge = captchas.remove(captchaId);
		if (challenge == null || challenge.expiresAt().isBefore(Instant.now()) || !challenge.answer().equals(answer.trim())) {
			throw new AuthException(HttpStatus.BAD_REQUEST, "圖形驗證錯誤或已過期");
		}
	}

	private AuthResponse responseFor(AppUser user) {
		String token = UUID.randomUUID().toString();
		sessions.put(token, user.getId());
		return responseFor(user, token);
	}

	public AuthResponse responseFor(AppUser user, String token) {
		return new AuthResponse(user.getId(), user.getName(), user.getPhone(), token, primaryRole(user), rolesFor(user));
	}

	public AppUser requireAdmin(String token) {
		AppUser user = requireUser(token);
		if (user == null || !hasRole(user, AppUser.Role.ADMIN)) {
			throw new AuthException(HttpStatus.FORBIDDEN, "需要管理員權限");
		}
		return user;
	}

	public AppUser requireCoachOrAdmin(String token) {
		AppUser user = requireUser(token);
		if (!hasRole(user, AppUser.Role.COACH) && !hasRole(user, AppUser.Role.ADMIN)) {
			throw new AuthException(HttpStatus.FORBIDDEN, "需要教練權限");
		}
		return user;
	}

	public AppUser requireUser(String token) {
		Long userId = sessions.get(token);
		AppUser user = userId == null ? null : userRepository.findById(userId).orElse(null);
		if (user == null) throw new AuthException(HttpStatus.UNAUTHORIZED, "請先登入");
		return user;
	}

	public void logout(String token) {
		sessions.remove(token);
	}

	public boolean hasRole(AppUser user, AppUser.Role role) {
		return user.getRole() == role || roleRepository.existsByUserIdAndRole(user.getId(), role);
	}

	public Set<AppUser.Role> rolesFor(AppUser user) {
		Set<AppUser.Role> roles = new LinkedHashSet<>();
		roles.add(AppUser.Role.MEMBER);
		roles.add(user.getRole());
		roleRepository.findAllByUserId(user.getId()).forEach(userRole -> roles.add(userRole.getRole()));
		return roles;
	}

	private AppUser.Role primaryRole(AppUser user) {
		Set<AppUser.Role> roles = rolesFor(user);
		if (roles.contains(AppUser.Role.ADMIN)) return AppUser.Role.ADMIN;
		if (roles.contains(AppUser.Role.COACH)) return AppUser.Role.COACH;
		return AppUser.Role.MEMBER;
	}

	private void grantRole(Long userId, AppUser.Role role) {
		if (!roleRepository.existsByUserIdAndRole(userId, role)) {
			roleRepository.save(new UserRole(userId, role));
		}
	}

	private String normalizePhone(String phone) {
		return phone.replace(" ", "").replace("-", "");
	}

	private record CaptchaChallenge(String answer, Instant expiresAt) {
	}
}
