package com.hoopers.basketball.player;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerService {

	private final PlayerRepository playerRepository;

	public PlayerService(PlayerRepository playerRepository) {
		this.playerRepository = playerRepository;
	}

	public List<Player> findAll() {
		return playerRepository.findAll();
	}

	public Player findById(Long id) {
		return playerRepository.findById(id)
				.orElseThrow(() -> new PlayerNotFoundException(id));
	}

	public Player create(PlayerRequest request) {
		return playerRepository.save(new Player(
				request.name(),
				request.team(),
				request.position(),
				request.jerseyNumber()));
	}

	@Transactional
	public Player update(Long id, PlayerRequest request) {
		Player player = findById(id);
		player.update(request);
		return player;
	}

	public void delete(Long id) {
		playerRepository.delete(findById(id));
	}
}
