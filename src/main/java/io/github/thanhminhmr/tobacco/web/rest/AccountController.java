/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.web.rest;

import io.github.thanhminhmr.tobacco.dto.model.UserDto;
import io.github.thanhminhmr.tobacco.dto.validation.DisplayString;
import io.github.thanhminhmr.tobacco.dto.validation.PasswordString;
import io.github.thanhminhmr.tobacco.presistence.model.User;
import io.github.thanhminhmr.tobacco.presistence.repository.UserRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/account")
public record AccountController(
		@Nonnull PasswordEncoder passwordEncoder,
		@Nonnull UserRepository userRepository
) implements UserDetailsService {
	public static @Nonnull User getCurrentUser(@Nonnull Authentication authentication) {
		// check if logged in
		if (authentication.getPrincipal() instanceof User user) {
			// return current user info
			return user;
		} else {
			// this should never happen
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in.");
		}
	}

	@Override
	public @Nonnull User loadUserByUsername(@Nonnull String username) throws UsernameNotFoundException {
		final User user = userRepository.findByUsername(username);
		if (user == null) throw new UsernameNotFoundException(username);
		return user;
	}


	/**
	 * Users get their account info.
	 *
	 * @param authentication Current user authentication.
	 * @return Current user info.
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull UserDto get(@NotNull Authentication authentication) {
		// return current user info
		return getCurrentUser(authentication).toDto();
	}

	/**
	 * Users update their account info.
	 *
	 * @param authentication Current user authentication.
	 * @param dto New user display name.
	 * @return Current user info.
	 */
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull UserDto update(@NotNull Authentication authentication, @RequestBody @NotNull @Valid AccountUpdateDto dto) {
		final User user = getCurrentUser(authentication);
		if (dto.displayName() != null) user.setDisplayName(dto.displayName());
		return userRepository.save(user).toDto();
	}

	/**
	 * Users delete their account themselves.
	 *
	 * @param authentication Current user authentication.
	 * @param dto Current password.
	 */
	@DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void delete(@NotNull Authentication authentication, @RequestBody @NotNull @Valid AccountConfirmPasswordDto dto) {
		final User user = getCurrentUser(authentication);
		// check current password
		if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid current password.");
		}
		// disable user
		user.setDeleted(true);
		userRepository.save(user);
		// TODO logout?
	}

	/**
	 * Users changes their password themselves.
	 *
	 * @param authentication Current user authentication.
	 * @param dto Current and new password.
	 */
	@PutMapping(value = "/password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void changePassword(@NotNull Authentication authentication, @RequestBody @NotNull @Valid AccountChangePasswordDto dto) {
		final User user = getCurrentUser(authentication);
		// check current password
		if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid current password.");
		}
		// save new password
		user.setPassword(passwordEncoder.encode(dto.newPassword()));
		userRepository.save(user);
		// TODO logout?
	}


	//region DTO

	public record AccountChangePasswordDto(
			@NotNull @PasswordString String currentPassword,
			@NotNull @PasswordString String newPassword
	) {
	}

	public record AccountConfirmPasswordDto(
			@NotNull @PasswordString String password
	) {
	}

	public record AccountUpdateDto(
			@Nullable @DisplayString String displayName
	) {
	}

	//endregion DTO
}
