/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.web.rest;

import io.github.thanhminhmr.tobacco.dto.converter.UserConverter;
import io.github.thanhminhmr.tobacco.dto.model.UserDto;
import io.github.thanhminhmr.tobacco.dto.rest.PageDto;
import io.github.thanhminhmr.tobacco.dto.validation.DisplayString;
import io.github.thanhminhmr.tobacco.dto.validation.UsernameString;
import io.github.thanhminhmr.tobacco.presistence.model.Authority;
import io.github.thanhminhmr.tobacco.presistence.model.Group;
import io.github.thanhminhmr.tobacco.presistence.model.User;
import io.github.thanhminhmr.tobacco.presistence.repository.GroupRepository;
import io.github.thanhminhmr.tobacco.presistence.repository.UserRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/users")
public record UsersController(
		@Nonnull PasswordEncoder passwordEncoder,
		@Nonnull UserRepository userRepository,
		@Nonnull UserConverter userConverter,
		@Nonnull GroupRepository groupRepository
) {
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull PageDto<UserDto> list(
			@RequestParam(value = "displayName", required = false) @Nullable @DisplayString String displayName,
			@RequestParam(value = "authority", required = false) @Nullable Authority authority,
			@RequestParam(value = "groupId", required = false) @Nullable Long groupId,
			@RequestParam(value = "deleted", required = false) @Nullable Boolean deleted,
			@RequestParam(value = "createdBefore", required = false) @Nullable Instant createdBefore,
			@RequestParam(value = "createdAfter", required = false) @Nullable Instant createdAfter,
			@RequestParam(value = "updatedBefore", required = false) @Nullable Instant updatedBefore,
			@RequestParam(value = "updatedAfter", required = false) @Nullable Instant updatedAfter,
			@RequestParam(value = "pageNumber", defaultValue = "0") @Min(0) int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "20") @Min(1) @Max(100) int pageSize) {
		return userConverter.convert(userRepository.findAll(
				new UserListSpecification(displayName, authority, groupId, deleted,
						createdBefore, createdAfter, updatedBefore, updatedAfter),
				PageRequest.of(pageNumber, pageSize)
		));
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull UserDto create(@RequestBody @NotNull @Valid UserCreateDto dto) {
		// check username already exists
		if (userRepository.existsByUsername(dto.username())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists.");
		}
		// generate a random 9 digits password
		final String password = String.format("%09d", ThreadLocalRandom.current().nextInt(1_000_000_000));
		// save user
		final User user = userRepository.save(User.builder()
				.username(dto.username())
				.password(passwordEncoder.encode(password))
				.displayName(dto.displayName())
				.authorities(Objects.requireNonNullElse(dto.authorities(), Set.of()))
				.deleted(Objects.requireNonNullElse(dto.deleted(), false))
				.build());
		// TODO: the new password needs to be returned
		return userConverter.convert(user);
	}

	@GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull UserDto get(@PathVariable("userId") long userId) {
		return userConverter.convert(userRepository.getReferenceById(userId));
	}

	@PutMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull UserDto update(@PathVariable("userId") long userId, @RequestBody @NotNull @Valid UserUpdateDto dto) {
		final User user = userRepository.getReferenceById(userId);
		if (dto.displayName() != null) user.setDisplayName(dto.displayName());
		if (dto.authorities() != null) user.setAuthorities(dto.authorities());
		if (dto.deleted() != null) user.setDeleted(dto.deleted());
		return userConverter.convert(userRepository.save(user));
	}

	@DeleteMapping(value = "/{userId}")
	public void delete(@PathVariable("userId") long userId) {
		final User user = userRepository.getReferenceById(userId);
		user.setDeleted(true);
		userRepository.save(user);
	}

	//region DTO

	public record UserUpdateDto(
			@Nullable @DisplayString String displayName,
			@Nullable Set<Authority> authorities,
			@Nullable Boolean deleted
	) {
	}

	public record UserCreateDto(
			@NotNull @UsernameString String username,
			@NotNull @DisplayString String displayName,
			@Nullable Set<Authority> authorities,
			@Nullable Boolean deleted
	) {
	}

	//endregion DTO

	//region Specification

	private record UserListSpecification(
			@Nullable @DisplayString String displayName,
			@Nullable Authority authority,
			@Nullable Long groupId,
			@Nullable Boolean deleted,
			@Nullable Instant createdBefore,
			@Nullable Instant createdAfter,
			@Nullable Instant updatedBefore,
			@Nullable Instant updatedAfter
	) implements Specification<User> {
		@Override
		public @Nonnull Predicate toPredicate(@Nonnull Root<User> userRoot,
				@Nonnull CriteriaQuery<?> query,
				@Nonnull CriteriaBuilder builder) {
			final List<Predicate> predicates = new ArrayList<>();
			if (displayName != null) {
				predicates.add(builder.like(userRoot.get("displayName"), '%' + displayName + '%'));
			}
			if (authority != null) {
				predicates.add(builder.isMember(authority, userRoot.<Set<Authority>>get("authorities")));
			}
			if (groupId != null) {
				final Root<Group> groupRoot = query.from(Group.class);
				predicates.add(builder.and(
						builder.equal(groupRoot.get("id"), groupId),
						builder.isMember(userRoot, groupRoot.<Set<User>>get("users"))
				));
			}
			if (deleted != null) {
				predicates.add(builder.equal(userRoot.get("deleted"), deleted));
			}
			if (createdBefore != null) {
				predicates.add(builder.lessThanOrEqualTo(userRoot.get("createdAt"), createdBefore));
			}
			if (createdAfter != null) {
				predicates.add(builder.greaterThanOrEqualTo(userRoot.get("createdAt"), createdAfter));
			}
			if (updatedBefore != null) {
				predicates.add(builder.lessThanOrEqualTo(userRoot.get("updatedAt"), updatedBefore));
			}
			if (updatedAfter != null) {
				predicates.add(builder.greaterThanOrEqualTo(userRoot.get("updatedAt"), updatedAfter));
			}
			return builder.and(predicates.toArray(Predicate[]::new));
		}
	}

	//endregion Specification
}
