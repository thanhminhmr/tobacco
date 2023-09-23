/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.converter;

import io.github.thanhminhmr.tobacco.dto.model.InvoiceDto;
import io.github.thanhminhmr.tobacco.presistence.model.Invoice;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

@Service
public final class InvoiceConverter implements EntityDtoConverter<Invoice, InvoiceDto> {
	@Override
	public @Nonnull InvoiceDto convert(@Nonnull Invoice invoice) {
		return new InvoiceDto(
				invoice.getId(),
				null,
				invoice.getDisplayDescription(),
				invoice.getStatus(),
				null,
				null,
				invoice.getDeleted(),
				invoice.getCreatedAt(),
				invoice.getUpdatedAt()
		);
	}
}
