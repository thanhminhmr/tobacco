/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.converter;

import io.github.thanhminhmr.tobacco.presistence.model.EntityMarker;
import io.github.thanhminhmr.tobacco.dto.model.DtoMarker;
import io.github.thanhminhmr.tobacco.dto.rest.PageDto;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface EntityDtoConverter<Entity extends EntityMarker, Dto extends DtoMarker> {
	@Nonnull Dto convert(@Nonnull Entity entity);

	default @Nonnull List<Dto> convert(@Nonnull List<Entity> entities) {
		final List<Dto> dtos = new ArrayList<>(entities.size());
		for (final Entity entity : entities) dtos.add(convert(entity));
		return Collections.unmodifiableList(dtos);
	}

	default @Nonnull PageDto<Dto> convert(@Nonnull Page<Entity> entityPage) {
		return PageDto.<Dto>builder()
				.numOfPage(entityPage.getTotalPages())
				.elements(convert(entityPage.getContent()))
				.pageSize(entityPage.getSize())
				.pageNumber(entityPage.getNumber())
				.build();
	}
}
