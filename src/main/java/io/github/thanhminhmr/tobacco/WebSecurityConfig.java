/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco;

import io.github.thanhminhmr.tobacco.presistence.model.Authority;
import io.github.thanhminhmr.tobacco.web.rest.AccountController;
import jakarta.annotation.Nonnull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {
	@Bean
	public @Nonnull WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring().requestMatchers("/", "/static/**");
	}

	@Bean
	public @Nonnull SecurityFilterChain securityFilterChain(@Nonnull HttpSecurity security) throws Exception {
		return security
				.authorizeHttpRequests(requests -> requests
						.requestMatchers("/api/account/**").authenticated()
						.requestMatchers("/api/users/**").hasAuthority(Authority.Name.SUPER_ADMIN)
						.requestMatchers("/api/**").denyAll()
						.anyRequest().permitAll())
				.httpBasic(Customizer.withDefaults())
//				.csrf(CsrfConfigurer::disable)
				.build();
	}

	@Bean
	public @Nonnull PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public @Nonnull DaoAuthenticationProvider authenticationProvider(@Nonnull AccountController accountController,
			@Nonnull PasswordEncoder passwordEncoder) {
		final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(passwordEncoder);
		authenticationProvider.setUserDetailsService(accountController);
		return authenticationProvider;
	}
}