package com.hoopers.basketball.video;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class VideoDataInitializer implements CommandLineRunner {

	private final VideoRepository videoRepository;

	public VideoDataInitializer(VideoRepository videoRepository) {
		this.videoRepository = videoRepository;
	}

	@Override
	public void run(String... args) {
		seed("本週最佳進攻回顧", "https://www.youtube.com/watch?v=dQw4w9WgXcQ", Video.Type.HIGHLIGHT, "Hoopers League");
		seed("控球訓練重點示範", "https://www.youtube.com/watch?v=ysz5S6PUM-U", Video.Type.TRAINING, "Hoopers Training");
	}

	private void seed(String title, String youtubeUrl, Video.Type type, String sourceName) {
		if (!videoRepository.existsByYoutubeUrl(youtubeUrl)) {
			videoRepository.save(new Video(title, youtubeUrl, type, sourceName));
		}
	}
}
