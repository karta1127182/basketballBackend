package com.hoopers.basketball.video;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hoopers.basketball.video.VideoDtos.VideoRequest;
import com.hoopers.basketball.video.VideoDtos.VideoResponse;

@Service
public class VideoService {

	private final VideoRepository videoRepository;

	public VideoService(VideoRepository videoRepository) {
		this.videoRepository = videoRepository;
	}

	@Transactional(readOnly = true)
	public List<VideoResponse> findVideos() {
		return videoRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
	}

	@Transactional
	public VideoResponse createVideo(VideoRequest request) {
		if (videoRepository.existsByYoutubeUrl(request.youtubeUrl().trim())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "YouTube 連結已存在");
		}
		Video video = videoRepository.save(new Video(
				request.title().trim(),
				request.youtubeUrl().trim(),
				request.type(),
				trimToNull(request.sourceName()),
				request.scheduleId(),
				request.teamId(),
				request.playerUserId()));
		return toResponse(video);
	}

	@Transactional
	public VideoResponse updateVideo(Long id, VideoRequest request) {
		Video video = findVideo(id);
		video.update(
				request.title().trim(),
				request.youtubeUrl().trim(),
				request.type(),
				trimToNull(request.sourceName()),
				request.scheduleId(),
				request.teamId(),
				request.playerUserId());
		return toResponse(video);
	}

	@Transactional
	public void deleteVideo(Long id) {
		if (!videoRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到影片");
		}
		videoRepository.deleteById(id);
	}

	private Video findVideo(Long id) {
		return videoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到影片"));
	}

	private VideoResponse toResponse(Video video) {
		return new VideoResponse(
				video.getId(),
				video.getTitle(),
				video.getYoutubeUrl(),
				video.getType(),
				video.getSourceName(),
				video.getScheduleId(),
				video.getTeamId(),
				video.getPlayerUserId(),
				video.getCreatedAt());
	}

	private String trimToNull(String value) {
		if (value == null || value.isBlank()) return null;
		return value.trim();
	}
}
