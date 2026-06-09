package com.hoopers.basketball.home;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hoopers.basketball.course.Course;
import com.hoopers.basketball.course.CourseRegistrationRepository;
import com.hoopers.basketball.course.CourseRepository;
import com.hoopers.basketball.home.HomeDtos.HomeCourseResponse;
import com.hoopers.basketball.home.HomeDtos.HomeFeedResponse;
import com.hoopers.basketball.home.HomeDtos.HomeGameResponse;
import com.hoopers.basketball.home.HomeDtos.HomeVideoResponse;
import com.hoopers.basketball.league.LeagueSchedule;
import com.hoopers.basketball.league.LeagueScheduleRepository;
import com.hoopers.basketball.video.Video;
import com.hoopers.basketball.video.VideoRepository;

@Service
public class HomeService {

	private final LeagueScheduleRepository scheduleRepository;
	private final CourseRepository courseRepository;
	private final CourseRegistrationRepository registrationRepository;
	private final VideoRepository videoRepository;

	public HomeService(
			LeagueScheduleRepository scheduleRepository,
			CourseRepository courseRepository,
			CourseRegistrationRepository registrationRepository,
			VideoRepository videoRepository) {
		this.scheduleRepository = scheduleRepository;
		this.courseRepository = courseRepository;
		this.registrationRepository = registrationRepository;
		this.videoRepository = videoRepository;
	}

	@Transactional(readOnly = true)
	public HomeFeedResponse feed() {
		return new HomeFeedResponse(
				scheduleRepository.findTop3ByStatusOrderByGameDateAscGameTimeAsc(LeagueSchedule.Status.LIVE).stream().map(this::toGame).toList(),
				scheduleRepository.findTop3ByStatusOrderByGameDateAscGameTimeAsc(LeagueSchedule.Status.UPCOMING).stream().map(this::toGame).toList(),
				scheduleRepository.findTop3ByStatusOrderByGameDateDescGameTimeDesc(LeagueSchedule.Status.FINAL).stream().map(this::toGame).toList(),
				courseRepository.findTop3ByOrderByCreatedAtDesc().stream().map(this::toCourse).toList(),
				videoRepository.findTop5ByOrderByCreatedAtDesc().stream().map(this::toVideo).toList());
	}

	private HomeGameResponse toGame(LeagueSchedule schedule) {
		return new HomeGameResponse(
				schedule.getId(),
				schedule.getGameDate(),
				schedule.getGameTime(),
				schedule.getVenue(),
				schedule.getHomeTeam().getName(),
				schedule.getAwayTeam().getName(),
				schedule.getStatus(),
				schedule.getScore(),
				schedule.getLiveYoutubeUrl(),
				schedule.getReplayYoutubeUrl());
	}

	private HomeCourseResponse toCourse(Course course) {
		return new HomeCourseResponse(
				course.getId(),
				course.getTitle(),
				course.getCoachName(),
				course.getTimeText(),
				course.getLocation(),
				course.getLevel(),
				course.getCapacity(),
				course.getPrice(),
				registrationRepository.countByCourseId(course.getId()));
	}

	private HomeVideoResponse toVideo(Video video) {
		return new HomeVideoResponse(
				video.getId(),
				video.getTitle(),
				video.getYoutubeUrl(),
				video.getType(),
				video.getSourceName(),
				video.getScheduleId(),
				video.getTeamId(),
				video.getPlayerUserId());
	}
}
