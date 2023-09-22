/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.converter;

import io.github.thanhminhmr.tobacco.presistence.model.User;
import io.github.thanhminhmr.tobacco.dto.model.UserDto;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

@Service
public final class UserConverter implements EntityDtoConverter<User, UserDto> {
	@Override
	public @Nonnull UserDto convert(@Nonnull User user) {
		return new UserDto(
				user.getId(),
				user.getUsername(),
				user.getDisplayName(),
				user.getDeleted(),
				user.getCreatedAt(),
				user.getUpdatedAt(),
				user.getAuthorities()
		);
	}
}
