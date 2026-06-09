package com.hoopers.basketball.player;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlayerRequest(
		@NotBlank(message = "請輸入姓名")
		@Size(max = 100, message = "姓名最多 100 個字")
		String name,

		@NotBlank(message = "請輸入球隊")
		@Size(max = 100, message = "球隊最多 100 個字")
		String team,

		@NotBlank(message = "請輸入位置")
		@Size(max = 30, message = "位置最多 30 個字")
		String position,

		@Min(value = 0, message = "背號需介於 0 到 99")
		@Max(value = 99, message = "背號需介於 0 到 99")
		Integer jerseyNumber) {
}
