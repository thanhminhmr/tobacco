/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.presistence.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "invoice_items")
public class InvoiceItem implements EntityMarker {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, insertable = false, updatable = false)
	private Long id;

	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "invoice_id", nullable = false, updatable = false)
	private Invoice invoice;

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id", nullable = false, updatable = false)
	private Product product;

	@Column(name = "unit_price", nullable = false)
	private Long unitPrice;

	@Column(name = "quantity", nullable = false)
	private Long quantity;

	@CreationTimestamp(source = SourceType.DB)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp(source = SourceType.DB)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;
}
