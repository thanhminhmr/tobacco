/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.presistence.model;

import io.github.thanhminhmr.tobacco.dto.model.DtoMarker;
import io.github.thanhminhmr.tobacco.dto.rest.PageDto;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface EntityMarker<Dto extends DtoMarker> {
	@Nonnull Dto toDto();


	static <Dto extends DtoMarker> @Nonnull List<Dto> toDtos(@Nonnull List<? extends EntityMarker<Dto>> entities) {
		final List<Dto> dtos = new ArrayList<>(entities.size());
		for (final EntityMarker<Dto> entity : entities) dtos.add(entity.toDto());
		return Collections.unmodifiableList(dtos);
	}

	static <Dto extends DtoMarker> @Nonnull PageDto<Dto> toPageDto(@Nonnull Page<? extends EntityMarker<Dto>> entityPage) {
		return new PageDto<>(
				toDtos(entityPage.getContent()),
				entityPage.getTotalPages(),
				entityPage.getNumber(),
				entityPage.getSize()
		);
	}
}
