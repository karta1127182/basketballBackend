package com.hoopers.basketball.coach;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hoopers.basketball.auth.AppUser;
import com.hoopers.basketball.auth.AppUserRepository;
import com.hoopers.basketball.coach.CoachDtos.CoachCourseSummary;
import com.hoopers.basketball.coach.CoachDtos.CoachProfileRequest;
import com.hoopers.basketball.coach.CoachDtos.CoachProfileResponse;
import com.hoopers.basketball.coach.CoachDtos.CoachRatingBreakdown;
import com.hoopers.basketball.coach.CoachDtos.CoachReviewSummary;
import com.hoopers.basketball.course.Course;
import com.hoopers.basketball.course.CourseRegistration;
import com.hoopers.basketball.course.CourseRegistrationRepository;
import com.hoopers.basketball.course.CourseRepository;
import com.hoopers.basketball.course.CourseReview;
import com.hoopers.basketball.course.CourseReviewRepository;
import com.hoopers.basketball.order.Order;
import com.hoopers.basketball.order.OrderRepository;

@Service
public class CoachProfileService {

	private final CoachProfileRepository profileRepository;
	private final AppUserRepository userRepository;
	private final CourseRepository courseRepository;
	private final CourseRegistrationRepository registrationRepository;
	private final CourseReviewRepository reviewRepository;
	private final OrderRepository orderRepository;

	public CoachProfileService(
			CoachProfileRepository profileRepository,
			AppUserRepository userRepository,
			CourseRepository courseRepository,
			CourseRegistrationRepository registrationRepository,
			CourseReviewRepository reviewRepository,
			OrderRepository orderRepository) {
		this.profileRepository = profileRepository;
		this.userRepository = userRepository;
		this.courseRepository = courseRepository;
		this.registrationRepository = registrationRepository;
		this.reviewRepository = reviewRepository;
		this.orderRepository = orderRepository;
	}

	@Transactional(readOnly = true)
	public List<CoachProfileResponse> findCoaches() {
		return userRepository.findAll().stream()
				.filter(user -> user.getRole() == AppUser.Role.COACH || user.getRole() == AppUser.Role.ADMIN)
				.map(user -> profileFor(user.getId()))
				.toList();
	}

	@Transactional(readOnly = true)
	public CoachProfileResponse profileFor(Long coachUserId) {
		AppUser coach = userRepository.findById(coachUserId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到教練"));
		CoachProfile profile = profileRepository.findByUserId(coachUserId)
				.orElseGet(() -> new CoachProfile(coachUserId));
		List<Course> courses = coursesFor(coachUserId);
		List<CourseRegistration> registrations = courses.stream()
				.flatMap(course -> registrationRepository.findAllByCourseIdOrderByCreatedAtAsc(course.getId()).stream())
				.toList();
		List<Long> registrationIds = registrations.stream().map(CourseRegistration::getId).toList();
		List<Order> orders = registrationIds.isEmpty()
				? List.of()
				: orderRepository.findAllByTypeAndTargetIdIn(Order.Type.COURSE, registrationIds);
		List<CourseReview> reviews = reviewRepository.findAllByCoachUserIdOrderByCreatedAtDesc(coachUserId);
		int paidRevenue = orders.stream()
				.filter(order -> order.getStatus() == Order.Status.PAID)
				.mapToInt(Order::getAmount)
				.sum();
		long paidCount = orders.stream().filter(order -> order.getStatus() == Order.Status.PAID).count();
		double averageRating = reviews.isEmpty()
				? 0
				: Math.round(reviews.stream().mapToInt(CourseReview::getRating).average().orElse(0) * 10.0) / 10.0;
		List<CoachRatingBreakdown> ratingBreakdown = java.util.stream.IntStream.iterate(5, rating -> rating - 1)
				.limit(5)
				.mapToObj(rating -> new CoachRatingBreakdown(
						rating,
						reviews.stream().filter(review -> review.getRating() == rating).count()))
				.toList();
		List<CoachReviewSummary> reviewSummaries = reviews.stream()
				.map(review -> new CoachReviewSummary(
						review.getId(),
						courseTitle(review.getCourseId()),
						reviewerName(review.getUserId()),
						review.getRating(),
						review.getComment(),
						review.getCreatedAt()))
				.toList();
		List<CoachCourseSummary> summaries = courses.stream()
				.map(course -> new CoachCourseSummary(
						course.getId(),
						course.getTitle(),
						course.getTimeText(),
						course.getLocation(),
						course.getPrice(),
						registrationRepository.countByCourseId(course.getId())))
				.toList();
		return new CoachProfileResponse(
				coach.getId(),
				coach.getName(),
				profile.getBio(),
				profile.getSpecialties(),
				profile.getPhotoUrl(),
				courses.size(),
				registrations.size(),
				paidCount,
				paidRevenue,
				averageRating,
				reviews.size(),
				ratingBreakdown,
				reviewSummaries,
				summaries,
				profile.getUpdatedAt());
	}

	@Transactional
	public CoachProfileResponse updateMyProfile(AppUser coach, CoachProfileRequest request) {
		CoachProfile profile = profileRepository.findByUserId(coach.getId())
				.orElseGet(() -> profileRepository.save(new CoachProfile(coach.getId())));
		profile.update(request.bio(), request.specialties(), request.photoUrl());
		return profileFor(coach.getId());
	}

	private List<Course> coursesFor(Long coachUserId) {
		return courseRepository.findAllByOrderByCreatedAtDesc().stream()
				.filter(course -> course.getCoachUserId().equals(coachUserId))
				.toList();
	}

	private String courseTitle(Long courseId) {
		return courseRepository.findById(courseId)
				.map(Course::getTitle)
				.orElse("課程");
	}

	private String reviewerName(Long userId) {
		return userRepository.findById(userId)
				.map(AppUser::getName)
				.orElse("會員");
	}
}
