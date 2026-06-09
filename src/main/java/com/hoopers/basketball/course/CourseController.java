package com.hoopers.basketball.course;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoopers.basketball.auth.AppUser;
import com.hoopers.basketball.auth.AuthService;
import com.hoopers.basketball.course.CourseDtos.CourseRequest;
import com.hoopers.basketball.course.CourseDtos.CourseOrderResponse;
import com.hoopers.basketball.course.CourseDtos.CourseRevenueResponse;
import com.hoopers.basketball.course.CourseDtos.CourseReviewRequest;
import com.hoopers.basketball.course.CourseDtos.CourseReviewResponse;
import com.hoopers.basketball.course.CourseDtos.CourseResponse;
import com.hoopers.basketball.course.CourseDtos.RegistrationResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

	private final CourseService courseService;
	private final AuthService authService;

	public CourseController(CourseService courseService, AuthService authService) {
		this.courseService = courseService;
		this.authService = authService;
	}

	@GetMapping
	public List<CourseResponse> courses() {
		return courseService.findCourses();
	}

	@PostMapping
	public ResponseEntity<CourseResponse> createCourse(
			@RequestHeader("X-Auth-Token") String token,
			@Valid @RequestBody CourseRequest request) {
		AppUser coach = authService.requireCoachOrAdmin(token);
		CourseResponse course = courseService.createCourse(coach, request);
		return ResponseEntity.created(URI.create("/api/courses/" + course.id())).body(course);
	}

	@PostMapping("/{id}/registrations")
	public CourseResponse register(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
		return courseService.register(id, authService.requireUser(token));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCourse(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
		courseService.deleteCourse(id, authService.requireCoachOrAdmin(token));
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/registrations/{registrationId}/pay")
	public RegistrationResponse payRegistration(@RequestHeader("X-Auth-Token") String token, @PathVariable Long registrationId) {
		return courseService.payRegistration(registrationId, authService.requireUser(token));
	}

	@PostMapping("/orders/{orderId}/confirm")
	public RegistrationResponse confirmPayment(@RequestHeader("X-Auth-Token") String token, @PathVariable Long orderId) {
		return courseService.confirmPayment(orderId, authService.requireCoachOrAdmin(token));
	}

	@PostMapping("/orders/{orderId}/refund")
	public RegistrationResponse refundPayment(@RequestHeader("X-Auth-Token") String token, @PathVariable Long orderId) {
		return courseService.refundPayment(orderId, authService.requireCoachOrAdmin(token));
	}

	@GetMapping("/orders/coach")
	public List<CourseOrderResponse> coachOrders(@RequestHeader("X-Auth-Token") String token) {
		return courseService.findCoachOrders(authService.requireCoachOrAdmin(token));
	}

	@GetMapping("/orders/me")
	public List<CourseOrderResponse> myOrders(@RequestHeader("X-Auth-Token") String token) {
		return courseService.findMyOrders(authService.requireUser(token));
	}

	@GetMapping("/revenue/me")
	public CourseRevenueResponse coachRevenue(@RequestHeader("X-Auth-Token") String token) {
		return courseService.findCoachRevenue(authService.requireCoachOrAdmin(token));
	}

	@GetMapping("/registrations/me")
	public List<RegistrationResponse> myRegistrations(@RequestHeader("X-Auth-Token") String token) {
		return courseService.findMyRegistrations(authService.requireUser(token));
	}

	@PostMapping("/registrations/{registrationId}/reviews")
	public CourseReviewResponse reviewRegistration(
			@RequestHeader("X-Auth-Token") String token,
			@PathVariable Long registrationId,
			@Valid @RequestBody CourseReviewRequest request) {
		return courseService.reviewRegistration(registrationId, authService.requireUser(token), request);
	}

	@PostMapping("/{id}/registrations/{registrationId}/check-in")
	public RegistrationResponse checkIn(
			@RequestHeader("X-Auth-Token") String token,
			@PathVariable Long id,
			@PathVariable Long registrationId) {
		return courseService.checkIn(id, registrationId, authService.requireCoachOrAdmin(token));
	}

	@PostMapping("/{id}/reminders")
	public ResponseEntity<Void> sendReminder(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
		courseService.sendCourseReminder(id, authService.requireCoachOrAdmin(token));
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}/registrations")
	public List<RegistrationResponse> registrations(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
		return courseService.findRegistrations(id, authService.requireCoachOrAdmin(token));
	}

	@GetMapping("/{id}/reviews")
	public List<CourseReviewResponse> reviews(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
		return courseService.findCourseReviews(id, authService.requireCoachOrAdmin(token));
	}
}
