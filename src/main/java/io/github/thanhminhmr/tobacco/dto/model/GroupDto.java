/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.model;

import io.github.thanhminhmr.tobacco.dto.validation.DisplayString;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Past;
import lombok.With;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link io.github.thanhminhmr.tobacco.presistence.model.Group}
 */
@With
public record GroupDto(
		@Nullable Long id,
		@Nullable @DisplayString String displayName,
		@Nullable Boolean deleted,
		@Nullable @Past Instant createdAt,
		@Nullable @Past Instant updatedAt
) implements DtoMarker, Serializable {
}