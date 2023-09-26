/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.presistence.model;

import io.github.thanhminhmr.tobacco.dto.model.InvoiceCommentDto;
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
@Table(name = "invoice_comments")
public class InvoiceComment implements EntityMarker<InvoiceCommentDto> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, insertable = false, updatable = false)
	private Long id;

	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "invoice_id", nullable = false)
	private Invoice invoice;

	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "display_comment", nullable = false)
	private String displayComment;

	@Enumerated(EnumType.STRING)
	@Column(name = "status_before", nullable = false)
	private InvoiceStatus statusBefore;

	@Enumerated(EnumType.STRING)
	@Column(name = "status_after", nullable = false)
	private InvoiceStatus statusAfter;

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
	public @Nonnull InvoiceCommentDto toDto() {
		return new InvoiceCommentDto(
				id,
				user.toDto(),
				displayComment,
				statusBefore,
				statusAfter,
				deleted,
				createdAt,
				updatedAt
		);
	}
}
