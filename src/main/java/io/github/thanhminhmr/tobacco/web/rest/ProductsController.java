/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.web.rest;

import io.github.thanhminhmr.tobacco.dto.model.ProductDto;
import io.github.thanhminhmr.tobacco.dto.rest.PageDto;
import io.github.thanhminhmr.tobacco.dto.validation.DisplayString;
import io.github.thanhminhmr.tobacco.presistence.model.EntityMarker;
import io.github.thanhminhmr.tobacco.presistence.model.Product;
import io.github.thanhminhmr.tobacco.presistence.repository.ProductRepository;
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

@RestController
@RequestMapping("/api/products")
public record ProductsController(
		@Nonnull ProductRepository productRepository
) {
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull PageDto<ProductDto> list(
			@RequestParam(value = "displayName", required = false) @Nullable @DisplayString String displayName,
			@RequestParam(value = "displayDescription", required = false) @Nullable @DisplayString String displayDescription,
			@RequestParam(value = "displayUnit", required = false) @Nullable @DisplayString String displayUnit,
			@RequestParam(value = "minimumPrice", required = false) @Nullable @Min(0) Long minimumPrice,
			@RequestParam(value = "maximumPrice", required = false) @Nullable @Min(0) Long maximumPrice,
			@RequestParam(value = "deleted", required = false) @Nullable Boolean deleted,
			@RequestParam(value = "createdBefore", required = false) @Nullable Instant createdBefore,
			@RequestParam(value = "createdAfter", required = false) @Nullable Instant createdAfter,
			@RequestParam(value = "updatedBefore", required = false) @Nullable Instant updatedBefore,
			@RequestParam(value = "updatedAfter", required = false) @Nullable Instant updatedAfter,
			@RequestParam(value = "pageNumber", defaultValue = "0") @Min(0) int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "20") @Min(1) @Max(100) int pageSize) {
		return EntityMarker.toPageDto(productRepository.findAll(
				new ProductListSpecification(displayName, displayDescription, displayUnit, minimumPrice, maximumPrice,
						deleted, createdBefore, createdAfter, updatedBefore, updatedAfter),
				PageRequest.of(pageNumber, pageSize)
		));
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull ProductDto create(@RequestBody @NotNull @Valid ProductCreateDto dto) {
		return productRepository.save(new Product()
				.setDisplayName(dto.displayName())
				.setDisplayDescription(dto.displayDescription())
				.setDisplayUnit(dto.displayUnit())
				.setCurrentPrice(dto.currentPrice())
				.setDeleted(false)
		).toDto();
	}

	@GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull ProductDto get(@PathVariable("productId") long productId) {
		return productRepository.getReferenceById(productId).toDto();
	}

	@PutMapping(value = "/{productId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull ProductDto update(@PathVariable("productId") long productId, @RequestBody @NotNull @Valid ProductUpdateDto dto) {
		final Product product = productRepository.getReferenceById(productId);
		if (dto.displayName() != null) product.setDisplayName(dto.displayName());
		if (dto.displayDescription() != null) product.setDisplayDescription(dto.displayDescription());
		if (dto.displayUnit() != null) product.setDisplayUnit(dto.displayUnit());
		if (dto.currentPrice() != null) product.setCurrentPrice(dto.currentPrice());
		return productRepository.save(product).toDto();
	}

	@DeleteMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public void delete(@PathVariable("productId") long productId) {
		final Product product = productRepository.getReferenceById(productId);
		product.setDeleted(true);
		productRepository.save(product);
	}

	//region DTO

	public record ProductCreateDto(
			@NotNull @DisplayString String displayName,
			@NotNull @DisplayString String displayDescription,
			@NotNull @DisplayString String displayUnit,
			@NotNull @Min(0) Long currentPrice
	) {
	}

	public record ProductUpdateDto(
			@Nullable @DisplayString String displayName,
			@Nullable @DisplayString String displayDescription,
			@Nullable @DisplayString String displayUnit,
			@Nullable @Min(0) Long currentPrice
	) {
	}

	//endregion DTO

	//region Specification

	private record ProductListSpecification(
			@Nullable @DisplayString String displayName,
			@Nullable @DisplayString String displayDescription,
			@Nullable @DisplayString String displayUnit,
			@Nullable @Min(0) Long minimumPrice,
			@Nullable @Min(0) Long maximumPrice,
			@Nullable Boolean deleted,
			@Nullable Instant createdBefore,
			@Nullable Instant createdAfter,
			@Nullable Instant updatedBefore,
			@Nullable Instant updatedAfter
	) implements Specification<Product> {
		@Override
		public @Nonnull Predicate toPredicate(@Nonnull Root<Product> productRoot,
				@Nonnull CriteriaQuery<?> query,
				@Nonnull CriteriaBuilder builder) {
			final List<Predicate> predicates = new ArrayList<>();
			if (displayName != null) {
				predicates.add(builder.like(productRoot.get("displayName"), '%' + displayName + '%'));
			}
			if (displayDescription != null) {
				predicates.add(builder.like(productRoot.get("displayDescription"), '%' + displayDescription + '%'));
			}
			if (displayUnit != null) {
				predicates.add(builder.like(productRoot.get("displayUnit"), '%' + displayUnit + '%'));
			}
			if (minimumPrice != null) {
				predicates.add(builder.greaterThanOrEqualTo(productRoot.get("currentPrice"), minimumPrice));
			}
			if (maximumPrice != null) {
				predicates.add(builder.lessThanOrEqualTo(productRoot.get("currentPrice"), maximumPrice));
			}
			if (deleted != null) {
				predicates.add(builder.equal(productRoot.get("deleted"), deleted));
			}
			if (createdBefore != null) {
				predicates.add(builder.lessThanOrEqualTo(productRoot.get("createdAt"), createdBefore));
			}
			if (createdAfter != null) {
				predicates.add(builder.greaterThanOrEqualTo(productRoot.get("createdAt"), createdAfter));
			}
			if (updatedBefore != null) {
				predicates.add(builder.lessThanOrEqualTo(productRoot.get("updatedAt"), updatedBefore));
			}
			if (updatedAfter != null) {
				predicates.add(builder.greaterThanOrEqualTo(productRoot.get("updatedAt"), updatedAfter));
			}
			return builder.and(predicates.toArray(Predicate[]::new));
		}
	}

	//endregion Specification
}
