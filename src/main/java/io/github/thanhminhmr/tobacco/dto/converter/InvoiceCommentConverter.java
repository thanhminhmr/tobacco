/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.converter;

import io.github.thanhminhmr.tobacco.dto.model.InvoiceCommentDto;
import io.github.thanhminhmr.tobacco.presistence.model.InvoiceComment;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

@Service
public final class InvoiceCommentConverter implements EntityDtoConverter<InvoiceComment, InvoiceCommentDto> {
	@Override
	public @Nonnull InvoiceCommentDto convert(@Nonnull InvoiceComment comment) {
		return new InvoiceCommentDto(
				comment.getId(),
				null,
				comment.getDisplayComment(),
				comment.getStatusBefore(),
				comment.getStatusAfter(),
				comment.getDeleted(),
				comment.getCreatedAt(),
				comment.getUpdatedAt()
		);
	}
}
