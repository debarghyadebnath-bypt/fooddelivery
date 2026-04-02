package com.example.foodelivery.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "carts")
public class Cart extends BaseEntity {

	@NotNull
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private CartStatus status = CartStatus.ACTIVE;

	@OneToMany(mappedBy = "cart", orphanRemoval = true)
	private List<CartItem> cartItems = new ArrayList<>();
}