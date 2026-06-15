package com.hoopers.basketball.profile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.hoopers.basketball.auth.AuthService;

@RestController
@RequestMapping("/api/profile")
public class ProfilePhotoController {

	private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "heic", "heif");
	private static final long MAX_BYTES = 5 * 1024 * 1024;
	private static final Path UPLOAD_DIR = Path.of("uploads", "profile").toAbsolutePath().normalize();

	private final AuthService authService;

	public ProfilePhotoController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/photo")
	public PhotoUploadResponse uploadPhoto(
			@RequestHeader("X-Auth-Token") String token,
			@RequestParam("file") MultipartFile file) {
		authService.requireUser(token);
		if (file.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "請選擇照片");
		}
		if (file.getSize() > MAX_BYTES) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "照片不可超過 5MB");
		}
		String extension = extension(file.getOriginalFilename(), file.getContentType());
		if (!ALLOWED_EXTENSIONS.contains(extension)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "只支援 JPG、PNG、WEBP");
		}
		try {
			Files.createDirectories(UPLOAD_DIR);
			String filename = UUID.randomUUID() + "." + extension;
			Path target = UPLOAD_DIR.resolve(filename).normalize();
			Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
			return new PhotoUploadResponse("/uploads/profile/" + filename);
		} catch (IOException error) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "照片上傳失敗");
		}
	}

	private String extension(String filename, String contentType) {
		String normalizedType = contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
		if (normalizedType.equals("image/jpeg") || normalizedType.equals("image/jpg")) return "jpg";
		if (normalizedType.equals("image/png")) return "png";
		if (normalizedType.equals("image/webp")) return "webp";
		if (normalizedType.equals("image/heic")) return "heic";
		if (normalizedType.equals("image/heif")) return "heif";
		if (filename == null || !filename.contains(".")) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "照片檔名格式不正確");
		}
		String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
		return extension;
	}

	public record PhotoUploadResponse(String url) {
	}
}
