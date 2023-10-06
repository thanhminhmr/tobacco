/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.presistence.model;

import io.github.thanhminhmr.tobacco.dto.model.UserDto;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Set;

@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class User implements EntityMarker<UserDto>, UserDetails {
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

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Instant createdAt;

	@UpdateTimestamp
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

	@Override
	public @Nonnull UserDto toDto() {
		return new UserDto(
				id,
				username,
				displayName,
				deleted,
				createdAt,
				updatedAt,
				null
		);
	}

	@Override
	public int hashCode() {
		return User.class.hashCode();
	}

	@Override
	public boolean equals(@Nonnull Object object) {
		return object == this || id != null && object instanceof User user && id.equals(user.id);
	}
}
