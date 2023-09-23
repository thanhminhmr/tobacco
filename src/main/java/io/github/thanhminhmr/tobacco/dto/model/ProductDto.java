/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.model;

import io.github.thanhminhmr.tobacco.dto.validation.DisplayString;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;
import lombok.With;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link io.github.thanhminhmr.tobacco.presistence.model.Product}
 */
@With
public record ProductDto(
		@Nullable Long id,
		@Nullable @DisplayString String displayName,
		@Nullable @DisplayString String displayDescription,
		@Nullable @DisplayString String displayUnit,
		@Nullable @Min(0) Long currentPrice,
		@Nullable Boolean deleted,
		@Nullable @Past Instant createdAt,
		@Nullable @Past Instant updatedAt
) implements DtoMarker, Serializable {
}