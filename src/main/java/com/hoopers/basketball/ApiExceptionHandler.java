package com.hoopers.basketball;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.hoopers.basketball.player.PlayerNotFoundException;
import com.hoopers.basketball.auth.AuthException;

@RestControllerAdvice
public class ApiExceptionHandler {

	private static final Map<String, String> MESSAGE_TRANSLATIONS = Map.ofEntries(
			Map.entry("Phone number is already registered", "手機號碼已被註冊"),
			Map.entry("SMS verification is invalid", "簡訊驗證無效"),
			Map.entry("SMS verification code is invalid or expired", "簡訊驗證碼錯誤或已過期"),
			Map.entry("Phone or password is incorrect", "手機號碼或密碼錯誤"),
			Map.entry("Captcha is required", "請先完成圖形驗證"),
			Map.entry("Captcha is incorrect or expired", "圖形驗證錯誤或已過期"),
			Map.entry("Administrator access is required", "需要管理員權限"),
			Map.entry("Coach access is required", "需要教練權限"),
			Map.entry("Login is required", "請先登入"),
			Map.entry("Account not found", "找不到帳號"),
			Map.entry("You cannot remove your own administrator role", "不能移除自己的管理員身分"),
			Map.entry("At least one administrator is required", "至少需要保留一位管理員"),
			Map.entry("Already registered for this course", "你已經報名這堂課程"),
			Map.entry("Course is full", "課程名額已滿"),
			Map.entry("Only the registrant can pay this order", "只有報名本人可以付款"),
			Map.entry("Only the course coach can check in students", "只有此課程教練可以幫學員簽到"),
			Map.entry("Only the course coach can delete this course", "只有此課程教練可以刪除課程"),
			Map.entry("Course has students who have not checked in yet", "課程仍有學員尚未簽到，暫時不能刪除"),
			Map.entry("Only the course coach can view registrations", "只有此課程教練可以查看報名名單"),
			Map.entry("Course not found", "找不到課程"),
			Map.entry("Registration not found", "找不到報名紀錄"),
			Map.entry("Team name already exists", "球隊名稱已存在"),
			Map.entry("Team is used by a schedule", "此球隊已被賽程使用，不能刪除"),
			Map.entry("Schedule not found", "找不到賽程"),
			Map.entry("Team not found", "找不到球隊"),
			Map.entry("Player is not on either scheduled team", "球員不屬於此場賽程的任一球隊"),
			Map.entry("Player stats cannot be negative", "球員數據不能為負數"),
			Map.entry("Made shots cannot exceed shot attempts", "命中數不能大於出手數"),
			Map.entry("YouTube URL already exists", "YouTube 連結已存在"),
			Map.entry("Video not found", "找不到影片"),
			Map.entry("Validation failed", "欄位資料驗證失敗"),
			Map.entry("找不到通知", "找不到通知"),
			Map.entry("只能讀取自己的通知", "只能讀取自己的通知"),
			Map.entry("找不到課程訂單", "找不到課程訂單"),
			Map.entry("訂單類型不正確", "訂單類型不正確"),
			Map.entry("只有此課程教練可以確認付款", "只有此課程教練可以確認付款"),
			Map.entry("只有此課程教練可以退款", "只有此課程教練可以退款"),
			Map.entry("只有此課程教練可以發送課程提醒", "只有此課程教練可以發送課程提醒"));

	@ExceptionHandler(AuthException.class)
	public ResponseEntity<Map<String, String>> handleAuth(AuthException exception) {
		return ResponseEntity.status(exception.getStatus())
				.body(Map.of("message", translate(exception.getMessage())));
	}

	@ExceptionHandler(PlayerNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleNotFound(PlayerNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "找不到球員"));
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException exception) {
		String message = exception.getReason() == null ? exception.getMessage() : exception.getReason();
		return ResponseEntity.status(exception.getStatusCode())
				.body(Map.of("message", translate(message)));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> handleDataIntegrity(DataIntegrityViolationException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(Map.of("message", "資料不符合資料庫限制。若剛新增欄位或身分，請重新啟動後端套用資料庫更新。"));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {
		Map<String, String> errors = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors()
				.forEach(error -> errors.putIfAbsent(fieldName(error.getField()), translateValidation(error.getField(), error.getDefaultMessage())));
		return ResponseEntity.badRequest().body(Map.of(
				"message", "欄位資料驗證失敗",
				"errors", errors));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleUnexpected(Exception exception) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of("message", "伺服器發生錯誤，請稍後再試"));
	}

	private String translate(String message) {
		if (message == null || message.isBlank()) return "發生錯誤，請稍後再試";
		return MESSAGE_TRANSLATIONS.getOrDefault(message, message);
	}

	private String translateValidation(String field, String message) {
		String translated = translate(message);
		if (!translated.equals(message)) return translated;
		if (message == null) return fieldName(field) + "格式不正確";
		if (message.contains("must not be blank")) return "請輸入" + fieldName(field);
		if (message.contains("must not be null")) return "請填寫" + fieldName(field);
		if (message.contains("size must be between")) return fieldName(field) + "長度不符合限制";
		if (message.contains("must match")) return fieldName(field) + "格式不正確";
		return translated;
	}

	private String fieldName(String field) {
		return switch (field) {
			case "name" -> "姓名";
			case "phone" -> "手機號碼";
			case "birthday" -> "生日";
			case "password" -> "密碼";
			case "captchaAnswer" -> "圖形驗證答案";
			case "smsCode" -> "簡訊驗證碼";
			case "title" -> "標題";
			case "location" -> "地點";
			case "youtubeUrl" -> "YouTube 連結";
			case "team" -> "球隊";
			case "position" -> "位置";
			case "jerseyNumber" -> "背號";
			default -> field;
		};
	}
}
