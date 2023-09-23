/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.model;

import io.github.thanhminhmr.tobacco.dto.validation.DisplayString;
import io.github.thanhminhmr.tobacco.dto.validation.UsernameString;
import io.github.thanhminhmr.tobacco.presistence.model.Authority;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Past;
import lombok.With;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

/**
 * DTO for {@link io.github.thanhminhmr.tobacco.presistence.model.User}
 */
@With
public record UserDto(
		@Nullable Long id,
		@Nullable @UsernameString String username,
		@Nullable @DisplayString String displayName,
		@Nullable Boolean deleted,
		@Nullable @Past Instant createdAt,
		@Nullable @Past Instant updatedAt,
		@Nullable Set<Authority> authorities
) implements DtoMarker, Serializable {
}