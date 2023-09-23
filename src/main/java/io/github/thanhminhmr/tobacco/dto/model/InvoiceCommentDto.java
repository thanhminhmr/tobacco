/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.model;

import io.github.thanhminhmr.tobacco.dto.validation.DisplayString;
import io.github.thanhminhmr.tobacco.presistence.model.InvoiceStatus;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Past;
import lombok.With;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link io.github.thanhminhmr.tobacco.presistence.model.InvoiceComment}
 */
@With
public record InvoiceCommentDto(
		@Nullable Long id,
		@Nullable UserDto user,
		@Nullable @DisplayString String displayComment,
		@Nullable InvoiceStatus statusBefore,
		@Nullable InvoiceStatus statusAfter,
		@Nullable Boolean deleted,
		@Nullable @Past Instant createdAt,
		@Nullable @Past Instant updatedAt
) implements DtoMarker, Serializable {
}