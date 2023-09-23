/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.converter;

import io.github.thanhminhmr.tobacco.dto.model.InvoiceItemDto;
import io.github.thanhminhmr.tobacco.presistence.model.InvoiceItem;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

@Service
public final class InvoiceItemConverter implements EntityDtoConverter<InvoiceItem, InvoiceItemDto> {
	@Override
	public @Nonnull InvoiceItemDto convert(@Nonnull InvoiceItem comment) {
		return new InvoiceItemDto(
				comment.getId(),
				null,
				comment.getUnitPrice(),
				comment.getQuantity(),
				comment.getDeleted(),
				comment.getCreatedAt(),
				comment.getUpdatedAt()
		);
	}
}
