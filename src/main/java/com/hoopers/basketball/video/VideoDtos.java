package com.hoopers.basketball.video;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class VideoDtos {

	private VideoDtos() {
	}

	public record VideoRequest(
			@NotBlank @Size(max = 160) String title,
			@NotBlank @Size(max = 500) String youtubeUrl,
			@NotNull Video.Type type,
			@Size(max = 160) String sourceName,
			Long scheduleId,
			Long teamId,
			Long playerUserId) {
	}

	public record VideoResponse(
			Long id,
			String title,
			String youtubeUrl,
			Video.Type type,
			String sourceName,
			Long scheduleId,
			Long teamId,
			Long playerUserId,
			Instant createdAt) {
	}
}
