/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.dto.converter;

import io.github.thanhminhmr.tobacco.dto.model.ProductDto;
import io.github.thanhminhmr.tobacco.presistence.model.Product;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

@Service
public class ProductConverter implements EntityDtoConverter<Product, ProductDto> {
	@Override
	public @Nonnull ProductDto convert(@Nonnull Product product) {
		return new ProductDto(
				product.getId(),
				product.getDisplayName(),
				product.getDisplayDescription(),
				product.getDisplayUnit(),
				product.getCurrentPrice(),
				product.getDeleted(),
				product.getCreatedAt(),
				product.getUpdatedAt()
		);
	}
}
