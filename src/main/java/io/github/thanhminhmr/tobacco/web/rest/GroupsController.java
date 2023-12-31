/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.web.rest;

import io.github.thanhminhmr.tobacco.dto.model.GroupDto;
import io.github.thanhminhmr.tobacco.dto.rest.PageDto;
import io.github.thanhminhmr.tobacco.dto.validation.DisplayString;
import io.github.thanhminhmr.tobacco.presistence.model.EntityMarker;
import io.github.thanhminhmr.tobacco.presistence.model.Group;
import io.github.thanhminhmr.tobacco.presistence.model.User;
import io.github.thanhminhmr.tobacco.presistence.repository.GroupRepository;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/groups")
public record GroupsController(
		@Nonnull GroupRepository groupRepository
) {
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull PageDto<GroupDto> list(
			@RequestParam(value = "displayName", required = false) @Nullable @DisplayString String displayName,
			@RequestParam(value = "userId", required = false) @Nullable Long userId,
			@RequestParam(value = "deleted", required = false) @Nullable Boolean deleted,
			@RequestParam(value = "createdBefore", required = false) @Nullable Instant createdBefore,
			@RequestParam(value = "createdAfter", required = false) @Nullable Instant createdAfter,
			@RequestParam(value = "updatedBefore", required = false) @Nullable Instant updatedBefore,
			@RequestParam(value = "updatedAfter", required = false) @Nullable Instant updatedAfter,
			@RequestParam(value = "pageNumber", defaultValue = "0") @Min(0) int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "20") @Min(1) @Max(100) int pageSize) {
		return EntityMarker.toPageDto(groupRepository.findAll(
				new GroupListSpecification(displayName, userId, deleted,
						createdBefore, createdAfter, updatedBefore, updatedAfter),
				PageRequest.of(pageNumber, pageSize)
		));
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull GroupDto create(@RequestBody @NotNull @Valid GroupCreateDto dto) {
		return groupRepository.save(new Group()
				.setDisplayName(dto.displayName())
				.setDeleted(false)
		).toDto();
	}

	@GetMapping(value = "/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull GroupDto get(@PathVariable("groupId") long groupId) {
		return groupRepository.getReferenceById(groupId).toDto();
	}

	@PutMapping(value = "/{groupId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull GroupDto update(@PathVariable("groupId") long groupId, @RequestBody @NotNull @Valid GroupUpdateDto dto) {
		final Group group = groupRepository.getReferenceById(groupId);
		if (dto.displayName() != null) group.setDisplayName(dto.displayName());
		return groupRepository.save(group).toDto();
	}

	@DeleteMapping(value = "/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public void delete(@PathVariable("groupId") long groupId) {
		final Group group = groupRepository.getReferenceById(groupId);
		group.setDeleted(true);
		groupRepository.save(group);
	}

	//region DTO

	public record GroupCreateDto(
			@NotNull @DisplayString String displayName
	) {
	}

	public record GroupUpdateDto(
			@Nullable @DisplayString String displayName
	) {
	}

	//endregion DTO

	//region Specification

	private record GroupListSpecification(
			@Nullable @DisplayString String displayName,
			@Nullable Long userId,
			@Nullable Boolean deleted,
			@Nullable Instant createdBefore,
			@Nullable Instant createdAfter,
			@Nullable Instant updatedBefore,
			@Nullable Instant updatedAfter
	) implements Specification<Group> {
		@Override
		public @Nonnull Predicate toPredicate(@Nonnull Root<Group> groupRoot,
				@Nonnull CriteriaQuery<?> query,
				@Nonnull CriteriaBuilder builder) {
			final List<Predicate> predicates = new ArrayList<>();
			if (displayName != null) {
				predicates.add(builder.like(groupRoot.get("displayName"), '%' + displayName + '%'));
			}
			if (userId != null) {
				final Root<User> userRoot = query.from(User.class);
				predicates.add(builder.and(
						builder.equal(userRoot.get("id"), userId),
						builder.isMember(groupRoot, userRoot.<Set<Group>>get("groups"))
				));
			}
			if (deleted != null) {
				predicates.add(builder.equal(groupRoot.get("deleted"), deleted));
			}
			if (createdBefore != null) {
				predicates.add(builder.lessThanOrEqualTo(groupRoot.get("createdAt"), createdBefore));
			}
			if (createdAfter != null) {
				predicates.add(builder.greaterThanOrEqualTo(groupRoot.get("createdAt"), createdAfter));
			}
			if (updatedBefore != null) {
				predicates.add(builder.lessThanOrEqualTo(groupRoot.get("updatedAt"), updatedBefore));
			}
			if (updatedAfter != null) {
				predicates.add(builder.greaterThanOrEqualTo(groupRoot.get("updatedAt"), updatedAfter));
			}
			return builder.and(predicates.toArray(Predicate[]::new));
		}
	}

	//endregion Specification
}
