/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.rest;

import jakarta.annotation.Nullable;

public record ResponseDto<Result>(
		boolean success,
		@Nullable String message,
		@Nullable Result result
) {
}
