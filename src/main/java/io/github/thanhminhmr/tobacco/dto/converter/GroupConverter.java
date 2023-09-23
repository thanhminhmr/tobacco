/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.converter;

import io.github.thanhminhmr.tobacco.dto.model.GroupDto;
import io.github.thanhminhmr.tobacco.presistence.model.Group;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

@Service
public final class GroupConverter implements EntityDtoConverter<Group, GroupDto> {
	@Override
	public @Nonnull GroupDto convert(@Nonnull Group group) {
		return new GroupDto(
				group.getId(),
				group.getDisplayName(),
				group.getDeleted(),
				group.getCreatedAt(),
				group.getUpdatedAt()
		);
	}
}
