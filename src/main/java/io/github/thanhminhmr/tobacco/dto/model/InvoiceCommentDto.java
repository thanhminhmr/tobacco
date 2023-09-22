/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.model;

import io.github.thanhminhmr.tobacco.presistence.model.InvoiceComment;
import io.github.thanhminhmr.tobacco.presistence.model.InvoiceStatus;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link InvoiceComment}
 */
public record InvoiceCommentDto(
		@Nullable Long id,
		@NotBlank(message = "{tobacco.validation.display_comment}") String displayComment,
		@NotNull InvoiceStatus statusBefore,
		@NotNull InvoiceStatus statusAfter,
		@NotNull @Past Instant createdAt
) implements DtoMarker, Serializable {
}