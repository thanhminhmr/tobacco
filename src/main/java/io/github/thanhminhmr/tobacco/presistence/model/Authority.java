/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.presistence.model;

import jakarta.annotation.Nonnull;
import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
	SUPER_ADMIN(Name.SUPER_ADMIN),
	NORMAL_USER(Name.NORMAL_USER),
	;

	// TODO These are some sample authorities


	private final @Nonnull String authority;

	Authority(@Nonnull String authority) {
		this.authority = authority;
	}

	@Override
	public final @Nonnull String getAuthority() {
		return authority;
	}


	public static final class Name {
		private Name() {
		}

		public static final @Nonnull String SUPER_ADMIN = "SUPER_ADMIN";
		public static final @Nonnull String NORMAL_USER = "NORMAL_USER";
	}
}
