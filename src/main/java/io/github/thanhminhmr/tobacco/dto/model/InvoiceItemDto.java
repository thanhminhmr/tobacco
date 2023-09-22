/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.model;

import io.github.thanhminhmr.tobacco.presistence.model.InvoiceItem;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link InvoiceItem}
 */
public record InvoiceItemDto(
		@Nullable Long id,
		long invoiceId,
		long productId,
		@Min(1) long unitPrice,
		@Min(1) long quantity,
		@NotNull @Past Instant createdAt,
		@NotNull @Past Instant updatedAt
) implements DtoMarker, Serializable {
}