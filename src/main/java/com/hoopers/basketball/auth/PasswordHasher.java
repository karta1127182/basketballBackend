package com.hoopers.basketball.auth;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Component;

@Component
public class PasswordHasher {

	private static final int ITERATIONS = 120_000;
	private static final int KEY_LENGTH = 256;
	private final SecureRandom secureRandom = new SecureRandom();

	public String hash(String password) {
		byte[] salt = new byte[16];
		secureRandom.nextBytes(salt);
		return ITERATIONS + ":" + encode(salt) + ":" + encode(derive(password, salt, ITERATIONS));
	}

	public boolean matches(String password, String passwordHash) {
		String[] parts = passwordHash.split(":");
		if (parts.length != 3) {
			return false;
		}
		int iterations = Integer.parseInt(parts[0]);
		byte[] salt = Base64.getDecoder().decode(parts[1]);
		byte[] expected = Base64.getDecoder().decode(parts[2]);
		return java.security.MessageDigest.isEqual(expected, derive(password, salt, iterations));
	}

	private byte[] derive(String password, byte[] salt, int iterations) {
		try {
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH);
			return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
			throw new IllegalStateException("Password hashing is unavailable", exception);
		}
	}

	private String encode(byte[] value) {
		return Base64.getEncoder().encodeToString(value);
	}
}
