/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.web.rest;

import io.github.thanhminhmr.tobacco.dto.converter.*;
import io.github.thanhminhmr.tobacco.dto.model.InvoiceCommentDto;
import io.github.thanhminhmr.tobacco.dto.model.InvoiceDto;
import io.github.thanhminhmr.tobacco.dto.model.InvoiceItemDto;
import io.github.thanhminhmr.tobacco.dto.rest.PageDto;
import io.github.thanhminhmr.tobacco.dto.validation.DisplayString;
import io.github.thanhminhmr.tobacco.presistence.model.Invoice;
import io.github.thanhminhmr.tobacco.presistence.model.InvoiceComment;
import io.github.thanhminhmr.tobacco.presistence.model.InvoiceItem;
import io.github.thanhminhmr.tobacco.presistence.model.InvoiceStatus;
import io.github.thanhminhmr.tobacco.presistence.repository.InvoiceCommentRepository;
import io.github.thanhminhmr.tobacco.presistence.repository.InvoiceItemRepository;
import io.github.thanhminhmr.tobacco.presistence.repository.InvoiceRepository;
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
@RequestMapping("/api/invoices")
public record InvoicesController(
		@Nonnull InvoiceRepository invoiceRepository,
		@Nonnull InvoiceConverter invoiceConverter,
		@Nonnull InvoiceItemRepository invoiceItemRepository,
		@Nonnull InvoiceItemConverter invoiceItemConverter,
		@Nonnull InvoiceCommentRepository invoiceCommentRepository,
		@Nonnull InvoiceCommentConverter invoiceCommentConverter,
		@Nonnull UserConverter userConverter,
		@Nonnull ProductConverter productConverter
) {
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull PageDto<InvoiceDto> list(
			@RequestParam(value = "displayDescription", required = false) @Nullable @DisplayString String displayDescription,
			@RequestParam(value = "invoiceStatus", required = false) @Nullable InvoiceStatus invoiceStatus,
			@RequestParam(value = "deleted", required = false) @Nullable Boolean deleted,
			@RequestParam(value = "createdBefore", required = false) @Nullable Instant createdBefore,
			@RequestParam(value = "createdAfter", required = false) @Nullable Instant createdAfter,
			@RequestParam(value = "updatedBefore", required = false) @Nullable Instant updatedBefore,
			@RequestParam(value = "updatedAfter", required = false) @Nullable Instant updatedAfter,
			@RequestParam(value = "pageNumber", defaultValue = "0") @Min(0) int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "20") @Min(1) @Max(100) int pageSize) {
		return invoiceConverter.convert(invoiceRepository.findAll(
				new InvoiceListSpecification(displayDescription, invoiceStatus,
						deleted, createdBefore, createdAfter, updatedBefore, updatedAfter),
				PageRequest.of(pageNumber, pageSize)
		));
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull InvoiceDto create(@RequestBody @NotNull @Valid InvoiceCreateDto dto) {
		return invoiceConverter.convert(invoiceRepository.save(Invoice.builder()
				.displayDescription(dto.displayDescription())
				.status(dto.status())
				.deleted(false)
				.build()));
	}

	@GetMapping(value = "/{invoiceId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull InvoiceDto get(@PathVariable("invoiceId") long invoiceId) {
		final Invoice invoice = invoiceRepository.getReferenceById(invoiceId);
		// get all comments with the commenter info
		final List<InvoiceCommentDto> commentDtos = new ArrayList<>();
		for (final InvoiceComment comment : invoice.getComments()) {
			commentDtos.add(invoiceCommentConverter.convert(comment)
					.withUser(userConverter.convert(comment.getUser())));
		}
		// get all items with the product info
		final List<InvoiceItemDto> itemDtos = new ArrayList<>();
		for (final InvoiceItem item : invoice.getItems()) {
			itemDtos.add(invoiceItemConverter.convert(item)
					.withProduct(productConverter.convert(item.getProduct())));
		}
		return invoiceConverter.convert(invoice)
				.withUser(userConverter.convert(invoice.getUser()))
				.withComments(commentDtos)
				.withItems(itemDtos);
	}

	@PutMapping(value = "/{invoiceId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull InvoiceDto update(@PathVariable("invoiceId") long invoiceId, @RequestBody @NotNull @Valid InvoiceUpdateDto dto) {
		final Invoice invoice = invoiceRepository.getReferenceById(invoiceId);
		if (dto.displayDescription() != null) invoice.setDisplayDescription(dto.displayDescription());
		if (dto.status() != null) invoice.setStatus(dto.status());
		return invoiceConverter.convert(invoiceRepository.save(invoice));
	}

	@DeleteMapping(value = "/{invoiceId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public void delete(@PathVariable("invoiceId") long invoiceId) {
		final Invoice invoice = invoiceRepository.getReferenceById(invoiceId);
		invoice.setDeleted(true);
		invoiceRepository.save(invoice);
	}

	//region DTO

	public record InvoiceCreateDto(
			@NotNull @DisplayString String displayDescription,
			@NotNull InvoiceStatus status
	) {
	}

	public record InvoiceUpdateDto(
			@Nullable @DisplayString String displayDescription,
			@Nullable InvoiceStatus status
	) {
	}

	//endregion DTO

	//region Specification

	private record InvoiceListSpecification(
			@Nullable @DisplayString String displayDescription,
			@Nullable InvoiceStatus invoiceStatus,
			@Nullable Boolean deleted,
			@Nullable Instant createdBefore,
			@Nullable Instant createdAfter,
			@Nullable Instant updatedBefore,
			@Nullable Instant updatedAfter
	) implements Specification<Invoice> {
		@Override
		public @Nonnull Predicate toPredicate(@Nonnull Root<Invoice> invoiceRoot,
				@Nonnull CriteriaQuery<?> query,
				@Nonnull CriteriaBuilder builder) {
			final List<Predicate> predicates = new ArrayList<>();
			if (displayDescription != null) {
				predicates.add(builder.like(invoiceRoot.get("displayDescription"), '%' + displayDescription + '%'));
			}
			if (invoiceStatus != null) {
				predicates.add(builder.equal(invoiceRoot.get("invoiceStatus"), invoiceStatus));
			}
			if (deleted != null) {
				predicates.add(builder.equal(invoiceRoot.get("deleted"), deleted));
			}
			if (createdBefore != null) {
				predicates.add(builder.lessThanOrEqualTo(invoiceRoot.get("createdAt"), createdBefore));
			}
			if (createdAfter != null) {
				predicates.add(builder.greaterThanOrEqualTo(invoiceRoot.get("createdAt"), createdAfter));
			}
			if (updatedBefore != null) {
				predicates.add(builder.lessThanOrEqualTo(invoiceRoot.get("updatedAt"), updatedBefore));
			}
			if (updatedAfter != null) {
				predicates.add(builder.greaterThanOrEqualTo(invoiceRoot.get("updatedAt"), updatedAfter));
			}
			return builder.and(predicates.toArray(Predicate[]::new));
		}
	}

	//endregion Specification
}
