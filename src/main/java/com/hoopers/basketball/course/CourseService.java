package com.hoopers.basketball.course;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hoopers.basketball.auth.AppUser;
import com.hoopers.basketball.auth.AppUserRepository;
import com.hoopers.basketball.auth.AuthService;
import com.hoopers.basketball.course.CourseDtos.CourseOrderResponse;
import com.hoopers.basketball.course.CourseDtos.CourseRequest;
import com.hoopers.basketball.course.CourseDtos.CourseRevenueResponse;
import com.hoopers.basketball.course.CourseDtos.CourseReviewRequest;
import com.hoopers.basketball.course.CourseDtos.CourseReviewResponse;
import com.hoopers.basketball.course.CourseDtos.CourseResponse;
import com.hoopers.basketball.course.CourseDtos.RegistrationResponse;
import com.hoopers.basketball.notification.NotificationService;
import com.hoopers.basketball.order.Order;
import com.hoopers.basketball.order.OrderRepository;

@Service
public class CourseService {

	private final CourseRepository courseRepository;
	private final CourseRegistrationRepository registrationRepository;
	private final CourseReviewRepository reviewRepository;
	private final OrderRepository orderRepository;
	private final NotificationService notificationService;
	private final AppUserRepository userRepository;
	private final AuthService authService;

	public CourseService(
			CourseRepository courseRepository,
			CourseRegistrationRepository registrationRepository,
			CourseReviewRepository reviewRepository,
			OrderRepository orderRepository,
			NotificationService notificationService,
			AppUserRepository userRepository,
			AuthService authService) {
		this.courseRepository = courseRepository;
		this.registrationRepository = registrationRepository;
		this.reviewRepository = reviewRepository;
		this.orderRepository = orderRepository;
		this.notificationService = notificationService;
		this.userRepository = userRepository;
		this.authService = authService;
	}

	@Transactional(readOnly = true)
	public List<CourseResponse> findCourses() {
		return courseRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
	}

	@Transactional
	public CourseResponse createCourse(AppUser coach, CourseRequest request) {
		Course course = courseRepository.save(new Course(
				request.title().trim(),
				coach,
				request.timeText().trim(),
				request.location().trim(),
				request.level().trim(),
				request.capacity(),
				request.price(),
				statusOrOpen(request.status()),
				request.description().trim()));
		return toResponse(course);
	}

	@Transactional
	public CourseResponse register(Long courseId, AppUser user) {
		Course course = findCourse(courseId);
		if (registrationRepository.existsByCourseIdAndUserId(courseId, user.getId())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "你已經報名這堂課程");
		}
		if (registrationRepository.countByCourseId(courseId) >= course.getCapacity()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "課程名額已滿");
		}
		CourseRegistration registration = registrationRepository.save(new CourseRegistration(course, user));
		Order order = orderRepository.save(new Order(user.getId(), Order.Type.COURSE, registration.getId(), course.getPrice()));
		if (order.getStatus() == Order.Status.PAID) {
			registration.markPaid();
		}
		notificationService.notifyUser(user.getId(), "COURSE_REGISTERED", "報名成功", "你已成功報名「" + course.getTitle() + "」。", "COURSE", course.getId());
		notificationService.notifyUser(course.getCoachUserId(), "COURSE_REGISTERED", "有新學員報名", user.getName() + " 已報名「" + course.getTitle() + "」。", "COURSE", course.getId());
		return toResponse(course);
	}

	@Transactional
	public RegistrationResponse payRegistration(Long registrationId, AppUser user) {
		CourseRegistration registration = findRegistration(registrationId);
		if (!registration.getUserId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有報名本人可以付款");
		}
		Order order = orderFor(registration);
		order.markPaid("SIMULATED");
		registration.markPaid();
		notificationService.notifyUser(user.getId(), "COURSE_PAYMENT_PAID", "課程付款成功", "你已完成「" + registration.getCourse().getTitle() + "」付款。", "COURSE", registration.getCourse().getId());
		notificationService.notifyUser(registration.getCourse().getCoachUserId(), "COURSE_PAYMENT_PAID", "學員已付款", registration.getName() + " 已完成「" + registration.getCourse().getTitle() + "」付款。", "COURSE", registration.getCourse().getId());
		return toRegistrationResponse(registration);
	}

	@Transactional
	public RegistrationResponse confirmPayment(Long orderId, AppUser user) {
		Order order = findCourseOrder(orderId);
		CourseRegistration registration = findRegistration(order.getTargetId());
		requireCourseOwnerOrAdmin(registration.getCourse(), user, "只有此課程教練可以確認付款");
		order.markPaid("MANUAL");
		registration.markPaid();
		notificationService.notifyUser(registration.getUserId(), "COURSE_PAYMENT_PAID", "課程付款已確認", "「" + registration.getCourse().getTitle() + "」付款已確認。", "COURSE", registration.getCourse().getId());
		return toRegistrationResponse(registration);
	}

	@Transactional
	public RegistrationResponse refundPayment(Long orderId, AppUser user) {
		Order order = findCourseOrder(orderId);
		CourseRegistration registration = findRegistration(order.getTargetId());
		requireCourseOwnerOrAdmin(registration.getCourse(), user, "只有此課程教練可以退款");
		order.markRefunded();
		registration.markRefunded();
		notificationService.notifyUser(registration.getUserId(), "COURSE_PAYMENT_REFUNDED", "課程已退款", "「" + registration.getCourse().getTitle() + "」付款狀態已更新為退款。", "COURSE", registration.getCourse().getId());
		return toRegistrationResponse(registration);
	}

	@Transactional
	public RegistrationResponse checkIn(Long courseId, Long registrationId, AppUser user) {
		Course course = findCourse(courseId);
		requireCourseOwnerOrAdmin(course, user, "只有此課程教練可以幫學員簽到");
		CourseRegistration registration = findRegistration(registrationId);
		registration.checkIn();
		return toRegistrationResponse(registration);
	}

	@Transactional
	public void sendCourseReminder(Long courseId, AppUser user) {
		Course course = findCourse(courseId);
		requireCourseOwnerOrAdmin(course, user, "只有此課程教練可以發送課程提醒");
		registrationRepository.findAllByCourseIdOrderByCreatedAtAsc(courseId)
				.forEach(registration -> notificationService.notifyUser(
						registration.getUserId(),
						"COURSE_REMINDER",
						"課程提醒",
						"「" + course.getTitle() + "」將於 " + course.getTimeText() + " 在 " + course.getLocation() + " 上課。",
						"COURSE",
						course.getId()));
	}

	@Transactional
	public void deleteCourse(Long courseId, AppUser user) {
		Course course = findCourse(courseId);
		requireCourseOwnerOrAdmin(course, user, "只有此課程教練可以刪除課程");
		if (registrationRepository.existsByCourseIdAndCheckedInFalse(courseId)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "課程仍有學員尚未簽到，暫時不能刪除");
		}
		List<Long> registrationIds = registrationRepository.findAllByCourseIdOrderByCreatedAtAsc(courseId).stream()
				.map(CourseRegistration::getId)
				.toList();
		if (!registrationIds.isEmpty()) {
			orderRepository.deleteAllByTypeAndTargetIdIn(Order.Type.COURSE, registrationIds);
		}
		registrationRepository.deleteAllByCourseId(courseId);
		courseRepository.delete(course);
	}

	@Transactional(readOnly = true)
	public List<RegistrationResponse> findRegistrations(Long courseId, AppUser user) {
		Course course = findCourse(courseId);
		requireCourseOwnerOrAdmin(course, user, "只有此課程教練可以查看報名名單");
		return registrationRepository.findAllByCourseIdOrderByCreatedAtAsc(courseId).stream()
				.map(this::toRegistrationResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<CourseOrderResponse> findCoachOrders(AppUser user) {
		List<CourseRegistration> registrations = courseRegistrationsForCoach(user);
		List<Long> registrationIds = registrations.stream().map(CourseRegistration::getId).toList();
		if (registrationIds.isEmpty()) return List.of();
		return orderRepository.findAllByTypeAndTargetIdIn(Order.Type.COURSE, registrationIds).stream()
				.map(order -> toOrderResponse(order, registrationById(registrations, order.getTargetId())))
				.toList();
	}

	@Transactional(readOnly = true)
	public CourseRevenueResponse findCoachRevenue(AppUser user) {
		List<CourseRegistration> registrations = courseRegistrationsForCoach(user);
		List<Long> registrationIds = registrations.stream().map(CourseRegistration::getId).toList();
		List<Order> orders = registrationIds.isEmpty() ? List.of() : orderRepository.findAllByTypeAndTargetIdIn(Order.Type.COURSE, registrationIds);
		long paidCount = orders.stream().filter(order -> order.getStatus() == Order.Status.PAID).count();
		long refundedCount = orders.stream().filter(order -> order.getStatus() == Order.Status.REFUNDED).count();
		int paidRevenue = orders.stream().filter(order -> order.getStatus() == Order.Status.PAID).mapToInt(Order::getAmount).sum();
		int expectedRevenue = orders.stream().filter(order -> order.getStatus() != Order.Status.REFUNDED).mapToInt(Order::getAmount).sum();
		long courseCount = courseRepository.findAllByOrderByCreatedAtDesc().stream()
				.filter(course -> authService.hasRole(user, AppUser.Role.ADMIN) || course.getCoachUserId().equals(user.getId()))
				.count();
		return new CourseRevenueResponse(
				courseCount,
				registrations.size(),
				paidCount,
				registrations.size() - paidCount - refundedCount,
				refundedCount,
				paidRevenue,
				expectedRevenue);
	}

	@Transactional(readOnly = true)
	public List<CourseOrderResponse> findMyOrders(AppUser user) {
		List<CourseRegistration> registrations = registrationRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
		List<Long> registrationIds = registrations.stream().map(CourseRegistration::getId).toList();
		if (registrationIds.isEmpty()) return List.of();
		return orderRepository.findAllByTypeAndTargetIdIn(Order.Type.COURSE, registrationIds).stream()
				.map(order -> toOrderResponse(order, registrationById(registrations, order.getTargetId())))
				.toList();
	}

	@Transactional
	public CourseReviewResponse reviewRegistration(Long registrationId, AppUser user, CourseReviewRequest request) {
		CourseRegistration registration = findRegistration(registrationId);
		if (!registration.getUserId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只能評價自己的課程");
		}
		if (!registration.isCheckedIn()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "完成簽到後才能評價課程");
		}
		if (reviewRepository.existsByRegistrationId(registrationId)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "這堂課程已經評價過");
		}
		String comment = request.comment() == null ? "" : request.comment().trim();
		CourseReview review = reviewRepository.save(new CourseReview(registration, request.rating(), comment));
		notificationService.notifyUser(
				registration.getCourse().getCoachUserId(),
				"COURSE_REVIEWED",
				"收到課程評價",
				user.getName() + " 評價了「" + registration.getCourse().getTitle() + "」。",
				"COURSE",
				registration.getCourse().getId());
		return toReviewResponse(review);
	}

	@Transactional(readOnly = true)
	public List<CourseReviewResponse> findCourseReviews(Long courseId, AppUser user) {
		Course course = findCourse(courseId);
		requireCourseOwnerOrAdmin(course, user, "只有此課程教練可以查看評價");
		return reviewRepository.findAllByCourseIdOrderByCreatedAtDesc(courseId).stream()
				.map(this::toReviewResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<RegistrationResponse> findMyRegistrations(AppUser user) {
		return registrationRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId()).stream()
				.map(this::toRegistrationResponse)
				.toList();
	}

	private Course findCourse(Long id) {
		return courseRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到課程"));
	}

	private CourseResponse toResponse(Course course) {
		return new CourseResponse(
				course.getId(),
				course.getTitle(),
				course.getCoachUserId(),
				course.getCoachName(),
				course.getTimeText(),
				course.getLocation(),
				course.getLevel(),
				course.getCapacity(),
				course.getPrice(),
				course.getStatus(),
				registrationRepository.countByCourseId(course.getId()),
				course.getDescription());
	}

	private RegistrationResponse toRegistrationResponse(CourseRegistration registration) {
		Order order = orderRepository.findByTypeAndTargetId(Order.Type.COURSE, registration.getId()).orElse(null);
		return new RegistrationResponse(
				registration.getId(),
				registration.getCourse().getId(),
				registration.getCourse().getTitle(),
				registration.getUserId(),
				registration.getName(),
				registration.getPhone(),
				defaultText(registration.getStatus(), "REGISTERED"),
				defaultText(registration.getPaymentStatus(), "UNPAID"),
				registration.isCheckedIn(),
				order == null ? null : order.getId(),
				order == null ? registration.getCourse().getPrice() : order.getAmount(),
				registration.getCreatedAt());
	}

	private CourseOrderResponse toOrderResponse(Order order, CourseRegistration registration) {
		return new CourseOrderResponse(
				order.getId(),
				registration.getId(),
				registration.getCourse().getId(),
				registration.getCourse().getTitle(),
				registration.getName(),
				registration.getPhone(),
				order.getAmount(),
				order.getStatus().name(),
				order.getPaymentProvider(),
				"COURSE-" + order.getId(),
				order.getPaidAt(),
				order.getCreatedAt());
	}

	private CourseReviewResponse toReviewResponse(CourseReview review) {
		Course course = findCourse(review.getCourseId());
		String reviewerName = userRepository.findById(review.getUserId())
				.map(AppUser::getName)
				.orElse("會員");
		return new CourseReviewResponse(
				review.getId(),
				review.getCourseId(),
				course.getTitle(),
				review.getCoachUserId(),
				review.getUserId(),
				reviewerName,
				review.getRating(),
				review.getComment(),
				review.getCreatedAt());
	}

	private CourseRegistration findRegistration(Long id) {
		return registrationRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到報名紀錄"));
	}

	private Order orderFor(CourseRegistration registration) {
		return orderRepository.findByTypeAndTargetId(Order.Type.COURSE, registration.getId())
				.orElseGet(() -> orderRepository.save(new Order(registration.getUserId(), Order.Type.COURSE, registration.getId(), 0)));
	}

	private Order findCourseOrder(Long id) {
		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到課程訂單"));
		if (order.getType() != Order.Type.COURSE) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "訂單類型不正確");
		}
		return order;
	}

	private List<CourseRegistration> courseRegistrationsForCoach(AppUser user) {
		return courseRepository.findAllByOrderByCreatedAtDesc().stream()
				.filter(course -> authService.hasRole(user, AppUser.Role.ADMIN) || course.getCoachUserId().equals(user.getId()))
				.flatMap(course -> registrationRepository.findAllByCourseIdOrderByCreatedAtAsc(course.getId()).stream())
				.toList();
	}

	private CourseRegistration registrationById(List<CourseRegistration> registrations, Long id) {
		return registrations.stream()
				.filter(registration -> registration.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到報名紀錄"));
	}

	private void requireCourseOwnerOrAdmin(Course course, AppUser user, String message) {
		if (!authService.hasRole(user, AppUser.Role.ADMIN) && !course.getCoachUserId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
		}
	}

	private String statusOrOpen(String status) {
		return status == null || status.isBlank() ? "OPEN" : status.trim();
	}

	private String defaultText(String value, String fallback) {
		return value == null || value.isBlank() ? fallback : value;
	}
}
