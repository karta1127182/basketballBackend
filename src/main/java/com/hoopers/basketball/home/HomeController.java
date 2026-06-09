package com.hoopers.basketball.home;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoopers.basketball.home.HomeDtos.HomeFeedResponse;

@RestController
@RequestMapping("/api/home")
public class HomeController {

	private final HomeService homeService;

	public HomeController(HomeService homeService) {
		this.homeService = homeService;
	}

	@GetMapping("/feed")
	public HomeFeedResponse feed() {
		return homeService.feed();
	}
}
