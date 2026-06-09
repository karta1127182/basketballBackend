package com.hoopers.basketball.video;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoopers.basketball.auth.AuthService;
import com.hoopers.basketball.video.VideoDtos.VideoRequest;
import com.hoopers.basketball.video.VideoDtos.VideoResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

	private final VideoService videoService;
	private final AuthService authService;

	public VideoController(VideoService videoService, AuthService authService) {
		this.videoService = videoService;
		this.authService = authService;
	}

	@GetMapping
	public List<VideoResponse> videos() {
		return videoService.findVideos();
	}

	@PostMapping
	public ResponseEntity<VideoResponse> createVideo(@RequestHeader("X-Auth-Token") String token, @Valid @RequestBody VideoRequest request) {
		authService.requireAdmin(token);
		VideoResponse video = videoService.createVideo(request);
		return ResponseEntity.created(URI.create("/api/videos/" + video.id())).body(video);
	}

	@PutMapping("/{id}")
	public VideoResponse updateVideo(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id, @Valid @RequestBody VideoRequest request) {
		authService.requireAdmin(token);
		return videoService.updateVideo(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteVideo(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
		authService.requireAdmin(token);
		videoService.deleteVideo(id);
		return ResponseEntity.noContent().build();
	}
}
