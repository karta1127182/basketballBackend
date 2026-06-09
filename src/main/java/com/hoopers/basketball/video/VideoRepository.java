package com.hoopers.basketball.video;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
	List<Video> findTop5ByOrderByCreatedAtDesc();
	List<Video> findAllByOrderByCreatedAtDesc();
	boolean existsByYoutubeUrl(String youtubeUrl);
}
