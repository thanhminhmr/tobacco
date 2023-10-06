/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.web.rest;

import io.github.thanhminhmr.tobacco.dto.model.InvoiceCommentDto;
import io.github.thanhminhmr.tobacco.dto.model.InvoiceDto;
import io.github.thanhminhmr.tobacco.dto.model.InvoiceItemDto;
import io.github.thanhminhmr.tobacco.dto.rest.PageDto;
import io.github.thanhminhmr.tobacco.dto.validation.DisplayString;
import io.github.thanhminhmr.tobacco.presistence.model.*;
import io.github.thanhminhmr.tobacco.presistence.repository.InvoiceCommentRepository;
import io.github.thanhminhmr.tobacco.presistence.repository.InvoiceItemRepository;
import io.github.thanhminhmr.tobacco.presistence.repository.InvoiceRepository;
import io.github.thanhminhmr.tobacco.presistence.repository.ProductRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/invoices")
public record InvoicesController(
		@Nonnull InvoiceRepository invoiceRepository,
		@Nonnull InvoiceItemRepository invoiceItemRepository,
		@Nonnull InvoiceCommentRepository invoiceCommentRepository,
		@Nonnull ProductRepository productRepository
) {
	private boolean isUserCanCreateInvoice(@NotNull User currentUser) {
		return currentUser.getAuthorities().contains(Authority.USER_SALESMAN);
	}

	private boolean isUserAuthorOfThisInvoice(@Nonnull User currentUser, @Nonnull Invoice invoice) {
		return currentUser.getAuthorities().contains(Authority.USER_SALESMAN)
				&& currentUser.equals(invoice.getAuthor());
	}

	private boolean isUserAuthorizedForThisInvoice(@Nonnull User currentUser, @Nonnull Invoice invoice) {
		final Set<Authority> authorities = currentUser.getAuthorities();
		final User author = invoice.getAuthor();
		if (authorities.contains(Authority.USER_ACCOUNTANT)
				|| authorities.contains(Authority.USER_MARKET_DIRECTOR)
				|| authorities.contains(Authority.SUPER_ADMIN)
				|| authorities.contains(Authority.USER_SALESMAN) && currentUser.equals(author)) {
			// current user is Accountant / Market Director / Super Admin
			// or current user is Salesman and also the author of the invoice
			return true;
		} else if (authorities.contains(Authority.USER_SALE_MANAGER)) {
			// shortcut
			if (currentUser.equals(author)) return true;
			// if current user is a Sale Manager
			for (final Group group : currentUser.getGroups()) {
				if (group.getUsers().contains(author)) {
					// current user is the Sale Manager of the author of the invoice
					return true;
				}
			}
		}
		// not authorized
		return false;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull PageDto<InvoiceDto> list(@NotNull Authentication authentication,
			@RequestParam(value = "displayDescription", required = false) @Nullable @DisplayString String displayDescription,
			@RequestParam(value = "invoiceStatus", required = false) @Nullable InvoiceStatus invoiceStatus,
			@RequestParam(value = "deleted", required = false) @Nullable Boolean deleted,
			@RequestParam(value = "createdBefore", required = false) @Nullable Instant createdBefore,
			@RequestParam(value = "createdAfter", required = false) @Nullable Instant createdAfter,
			@RequestParam(value = "updatedBefore", required = false) @Nullable Instant updatedBefore,
			@RequestParam(value = "updatedAfter", required = false) @Nullable Instant updatedAfter,
			@RequestParam(value = "pageNumber", defaultValue = "0") @Min(0) int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "20") @Min(1) @Max(100) int pageSize) {
		final User currentUser = AccountController.getCurrentUser(authentication);
		return EntityMarker.toPageDto(invoiceRepository.findAll(
				new InvoiceListSpecification(currentUser, displayDescription, invoiceStatus,
						deleted, createdBefore, createdAfter, updatedBefore, updatedAfter),
				PageRequest.of(pageNumber, pageSize)
		));
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull InvoiceDto create(@NotNull Authentication authentication,
			@RequestBody @NotNull @Valid InvoiceCreateDto dto) {
		final User currentUser = AccountController.getCurrentUser(authentication);
		if (isUserCanCreateInvoice(currentUser)) {
			return invoiceRepository.save(new Invoice()
					.setAuthor(currentUser)
					.setDisplayDescription(dto.displayDescription())
					.setStatus(InvoiceStatus.CREATED)
					.setDeleted(false)
			).toDto();
		} else {
			// Only salesman can create invoice
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
	}

	@GetMapping(value = "/{invoiceId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull InvoiceDto get(@NotNull Authentication authentication,
			@PathVariable("invoiceId") long invoiceId) {
		final User currentUser = AccountController.getCurrentUser(authentication);
		final Invoice invoice = invoiceRepository.getReferenceById(invoiceId);
		if (isUserAuthorizedForThisInvoice(currentUser, invoice)) {
			return invoice.toDto();
		} else {
			// user are not authorized to get this invoice
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
	}

	@PutMapping(value = "/{invoiceId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull InvoiceDto update(@NotNull Authentication authentication,
			@PathVariable("invoiceId") long invoiceId,
			@RequestBody @NotNull @Valid InvoiceUpdateDto dto) {
		final User currentUser = AccountController.getCurrentUser(authentication);
		final Invoice invoice = invoiceRepository.getReferenceById(invoiceId);
		if (isUserAuthorOfThisInvoice(currentUser, invoice)) {
			switch (invoice.getStatus()) {
				case DONE, ABORTED -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			}
			if (dto.displayDescription() != null) invoice.setDisplayDescription(dto.displayDescription());
			return invoiceRepository.save(invoice).toDto();
		} else {
			// user are not authorized to update this invoice
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
	}

	@DeleteMapping(value = "/{invoiceId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public void delete(@PathVariable("invoiceId") long invoiceId) {
		final Invoice invoice = invoiceRepository.getReferenceById(invoiceId);
		invoice.setDeleted(true);
		invoiceRepository.save(invoice);
	}


	@GetMapping(value = "/{invoiceId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull List<InvoiceCommentDto> getComments(@NotNull Authentication authentication,
			@PathVariable("invoiceId") long invoiceId) {
		final User currentUser = AccountController.getCurrentUser(authentication);
		final Invoice invoice = invoiceRepository.getReferenceById(invoiceId);
		// check if user have the authorization to view comments
		if (isUserAuthorizedForThisInvoice(currentUser, invoice)) {
			return EntityMarker.toDtos(invoice.getComments());
		} else {
			// Current user doesn't have permission to view invoice items for this invoice.
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
	}

	@PostMapping(value = "/{invoiceId}/comments", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull InvoiceCommentDto createComment(@NotNull Authentication authentication,
			@PathVariable("invoiceId") long invoiceId,
			@RequestBody @NotNull @Valid InvoiceCreateCommentDto dto) {
		final User currentUser = AccountController.getCurrentUser(authentication);
		final Invoice invoice = invoiceRepository.getReferenceById(invoiceId);
		// check if user have the authorization to comment
		if (isUserAuthorizedForThisInvoice(currentUser, invoice)) {
			// create the comment and change the status accordingly
			final InvoiceComment comment = invoiceCommentRepository.save(new InvoiceComment()
					.setInvoice(invoice)
					.setUser(currentUser)
					.setDisplayComment(dto.displayComment())
					.setStatusBefore(invoice.getStatus())
					.setStatusAfter(dto.statusAfter())
					.setDeleted(false)
			);
			invoiceRepository.save(invoice.setStatus(dto.statusAfter()));
			return comment.toDto();
		} else {
			// Current user doesn't have permission to create comment for this invoice.
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping(value = "/{invoiceId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull List<InvoiceItemDto> getItems(@NotNull Authentication authentication,
			@PathVariable("invoiceId") long invoiceId) {
		final User currentUser = AccountController.getCurrentUser(authentication);
		final Invoice invoice = invoiceRepository.getReferenceById(invoiceId);
		// check if user have the authorization to view invoice items
		if (isUserAuthorizedForThisInvoice(currentUser, invoice)) {
			return EntityMarker.toDtos(invoice.getItems());
		} else {
			// Current user doesn't have permission to view invoice items for this invoice.
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
	}

	@PostMapping(value = "/{invoiceId}/items",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @Nonnull InvoiceItemDto createItem(@PathVariable("invoiceId") long invoiceId,
			@NotNull Authentication authentication,
			@RequestBody @NotNull @Valid InvoiceCreateItemDto dto) {
		final User currentUser = AccountController.getCurrentUser(authentication);
		final Invoice invoice = invoiceRepository.getReferenceById(invoiceId);
		// check if the current user is the author of this invoice
		if (!isUserAuthorOfThisInvoice(currentUser, invoice)) {
			// Current user doesn't have permission to create invoice item for this invoice
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		final Product product = productRepository.getReferenceById(dto.productId());
		if (product.getDeleted()) {
			// Product is not available
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		// todo should we only create item if the invoice status is CREATED?
		if (invoice.getStatus() != InvoiceStatus.CREATED) {
			// Invoice item is not modifiable
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		// save and return
		final InvoiceItem item = invoiceItemRepository.save(new InvoiceItem()
				.setInvoice(invoice)
				.setProduct(product)
				.setUnitPrice(product.getCurrentPrice())
				.setQuantity(dto.quantity())
				.setDeleted(false)
		);
		return item.toDto();
	}


	//region DTO

	public record InvoiceCreateDto(
			@NotNull @DisplayString String displayDescription
	) {
	}

	public record InvoiceUpdateDto(
			@Nullable @DisplayString String displayDescription
	) {
	}

	public record InvoiceCreateCommentDto(
			@NotNull @DisplayString String displayComment,
			@NotNull InvoiceStatus statusAfter
	) {
	}

	public record InvoiceCreateItemDto(
			@NotNull Long productId,
			@NotNull @Min(0) Long quantity
	) {
	}

	//endregion DTO

	//region Specification

	private record InvoiceListSpecification(
			@Nonnull User currentUser,
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
			// filter all invoice that current user are authorized to view
			final Set<Authority> authorities = currentUser.getAuthorities();
			if (authorities.contains(Authority.SUPER_ADMIN)
					|| authorities.contains(Authority.USER_MARKET_DIRECTOR)
					|| authorities.contains(Authority.USER_ACCOUNTANT)) {
				// they can view all invoice, so no need for a filter
				// intentionally left blank
			} else if (authorities.contains(Authority.USER_SALE_MANAGER)) {
				final Subquery<Group> subquery = query.subquery(Group.class);
				final Root<Group> groupRoot = subquery.from(Group.class);
				final Path<Set<User>> groupUsers = groupRoot.get("users");
				predicates.add(builder.or(
						builder.equal(invoiceRoot.get("author"), currentUser),
						builder.exists(subquery.select(groupRoot)
								.where(builder.and(
										builder.isMember(currentUser, groupUsers),
										builder.isMember(invoiceRoot.get("author"), groupUsers)
								)))
				));
			} else if (authorities.contains(Authority.USER_SALESMAN)) {
				// if the salesman is the author then he can view the invoice
				predicates.add(builder.equal(invoiceRoot.get("authorId"), currentUser.getId()));
			} else {
				// unknown authority, filter everything
				predicates.add(builder.or());
			}
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
