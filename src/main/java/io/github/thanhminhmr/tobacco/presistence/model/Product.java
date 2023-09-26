/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.presistence.model;

import io.github.thanhminhmr.tobacco.dto.model.ProductDto;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "products")
public class Product implements EntityMarker<ProductDto> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, insertable = false, updatable = false)
	private Long id;

	@Column(name = "display_name", nullable = false)
	private String displayName;

	@Column(name = "display_description", nullable = false)
	private String displayDescription;

	@Column(name = "display_unit", nullable = false)
	private String displayUnit;

	@Column(name = "current_price", nullable = false)
	private Long currentPrice;

	@Column(name = "deleted", nullable = false)
	private Boolean deleted;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Instant updatedAt;

	@Override
	public @Nonnull ProductDto toDto() {
		return new ProductDto(
				id,
				displayName,
				displayDescription,
				displayUnit,
				currentPrice,
				deleted,
				createdAt,
				updatedAt
		);
	}
}
