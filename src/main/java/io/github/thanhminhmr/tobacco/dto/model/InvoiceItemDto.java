/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;
import lombok.With;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link io.github.thanhminhmr.tobacco.presistence.model.InvoiceItem}
 */
@With
public record InvoiceItemDto(
		@Nullable Long id,
		@Nullable ProductDto product,
		@Nullable @Min(0) Long unitPrice,
		@Nullable @Min(0) Long quantity,
		@Nullable Boolean deleted,
		@Nullable @Past Instant createdAt,
		@Nullable @Past Instant updatedAt
) implements DtoMarker, Serializable {
}