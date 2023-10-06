/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.presistence.model;

import jakarta.annotation.Nonnull;
import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
	SUPER_ADMIN(Name.SUPER_ADMIN),
	USER_SALESMAN(Name.USER_SALESMAN),
	USER_SALE_MANAGER(Name.USER_SALE_MANAGER),
	USER_ACCOUNTANT(Name.USER_ACCOUNTANT),
	USER_MARKET_DIRECTOR(Name.USER_MARKET_DIRECTOR),
	;


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
		public static final @Nonnull String USER_SALESMAN = "USER_SALESMAN";
		public static final @Nonnull String USER_SALE_MANAGER = "USER_SALE_MANAGER";
		public static final @Nonnull String USER_ACCOUNTANT = "USER_ACCOUNTANT";
		public static final @Nonnull String USER_MARKET_DIRECTOR = "USER_MARKET_DIRECTOR";
	}
}
