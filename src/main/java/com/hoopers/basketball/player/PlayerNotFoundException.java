package com.hoopers.basketball.player;

public class PlayerNotFoundException extends RuntimeException {

	public PlayerNotFoundException(Long id) {
		super("Player not found: " + id);
	}
}
