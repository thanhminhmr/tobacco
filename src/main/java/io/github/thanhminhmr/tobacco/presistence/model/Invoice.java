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
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "invoices")
public class Invoice implements EntityMarker {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, insertable = false, updatable = false)
	private Long id;

	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private InvoiceStatus status;

	@ToString.Exclude
	@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<InvoiceItem> items;

	@ToString.Exclude
	@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<InvoiceComment> comments;

	@CreationTimestamp(source = SourceType.DB)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp(source = SourceType.DB)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;
}
