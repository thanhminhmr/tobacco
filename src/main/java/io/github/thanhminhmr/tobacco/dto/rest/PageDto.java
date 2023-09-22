/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.rest;

import io.github.thanhminhmr.tobacco.dto.model.DtoMarker;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;
import java.util.Collection;

/**
 * This is a special DTO to deal with paging.
 *
 * @param numOfPage
 * @param elements
 * @param pageNumber
 * @param pageSize
 * @param <Dto> Any DTO that are marked with {@link DtoMarker}.
 */
@Builder
public record PageDto<Dto extends DtoMarker>(
		@Min(1) int numOfPage,
		@NotNull Collection<Dto> elements,
		@Min(1) int pageNumber,
		@Min(1) @Max(100) int pageSize
) implements Serializable {
}
