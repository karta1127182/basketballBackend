package com.hoopers.basketball.player;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

	private final PlayerService playerService;

	public PlayerController(PlayerService playerService) {
		this.playerService = playerService;
	}

	@GetMapping
	public List<Player> findAll() {
		return playerService.findAll();
	}

	@GetMapping("/{id}")
	public Player findById(@PathVariable Long id) {
		return playerService.findById(id);
	}

	@PostMapping
	public ResponseEntity<Player> create(@Valid @RequestBody PlayerRequest request) {
		Player player = playerService.create(request);
		return ResponseEntity.created(URI.create("/api/players/" + player.getId())).body(player);
	}

	@PutMapping("/{id}")
	public Player update(@PathVariable Long id, @Valid @RequestBody PlayerRequest request) {
		return playerService.update(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		playerService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
