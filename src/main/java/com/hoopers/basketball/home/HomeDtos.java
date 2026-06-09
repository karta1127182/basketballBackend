package com.hoopers.basketball.home;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.hoopers.basketball.league.LeagueSchedule;
import com.hoopers.basketball.video.Video;

public final class HomeDtos {

	private HomeDtos() {
	}

	public record HomeFeedResponse(
			List<HomeGameResponse> liveGames,
			List<HomeGameResponse> upcomingGames,
			List<HomeGameResponse> latestResults,
			List<HomeCourseResponse> featuredCourses,
			List<HomeVideoResponse> latestVideos) {
	}

	public record HomeGameResponse(
			Long id,
			LocalDate date,
			LocalTime time,
			String venue,
			String homeTeam,
			String awayTeam,
			LeagueSchedule.Status status,
			String score,
			String liveYoutubeUrl,
			String replayYoutubeUrl) {
	}

	public record HomeCourseResponse(
			Long id,
			String title,
			String coachName,
			String timeText,
			String location,
			String level,
			int capacity,
			int price,
			long registrationCount) {
	}

	public record HomeVideoResponse(
			Long id,
			String title,
			String youtubeUrl,
			Video.Type type,
			String sourceName,
			Long scheduleId,
			Long teamId,
			Long playerUserId) {
	}
}
