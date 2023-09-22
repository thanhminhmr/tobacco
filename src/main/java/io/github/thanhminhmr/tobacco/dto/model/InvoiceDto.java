/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.model;

import io.github.thanhminhmr.tobacco.presistence.model.Invoice;
import io.github.thanhminhmr.tobacco.presistence.model.InvoiceStatus;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link Invoice}
 */
public record InvoiceDto(
		@Nullable Long id,
		@NotNull InvoiceStatus status,
		@NotNull @Past Instant createdAt,
		@NotNull @Past Instant updatedAt
) implements DtoMarker, Serializable {
}