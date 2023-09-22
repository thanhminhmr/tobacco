/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.presistence.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class User implements EntityMarker, UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, insertable = false, updatable = false)
	private Long id;

	@Column(name = "username", nullable = false, unique = true, updatable = false)
	private String username;

	@ToString.Exclude
	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "display_name", nullable = false)
	private String displayName;

	@Column(name = "deleted", nullable = false)
	private Boolean deleted;

	@CreationTimestamp(source = SourceType.DB)
	@Column(name = "created_at", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Instant createdAt;

	@UpdateTimestamp(source = SourceType.DB)
	@Column(name = "updated_at", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Instant updatedAt;


	@ElementCollection(targetClass = Authority.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "users_authorities", joinColumns = @JoinColumn(name = "user_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "authority", nullable = false)
	private Set<Authority> authorities;

	@ToString.Exclude
	@ManyToMany
	@JoinTable(joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "group_id"),
			name = "users_groups")
	private Set<Group> groups;


	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return !getDeleted();
	}
}
