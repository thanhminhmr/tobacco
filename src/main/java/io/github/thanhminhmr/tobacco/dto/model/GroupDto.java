/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.model;

import io.github.thanhminhmr.tobacco.presistence.model.Group;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.io.Serializable;

/**
 * DTO for {@link Group}
 *
 * @param id Will be ignored when creating a new {@link Group}.
 * @param displayName Display name.
 * @param users Will be null unless specifically requested for.
 */
@Builder
public record GroupDto(
		@Nullable Long id,
		@NotBlank String displayName
) implements DtoMarker, Serializable {
}