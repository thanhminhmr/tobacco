/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.rest;

import io.github.thanhminhmr.tobacco.dto.model.DtoMarker;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Collection;

/**
 * This is a special DTO to deal with paging.
 *
 * @param elements
 * @param numOfPage
 * @param pageNumber
 * @param pageSize
 * @param <Dto> Any DTO that are marked with {@link DtoMarker}.
 */
public record PageDto<Dto extends DtoMarker>(
		@NotNull Collection<Dto> elements,
		@Min(0) int numOfPage,
		@Min(0) int pageNumber,
		@Min(1) @Max(100) int pageSize
) implements Serializable {
}
