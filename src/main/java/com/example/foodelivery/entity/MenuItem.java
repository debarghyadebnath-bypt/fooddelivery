package com.example.foodelivery.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "menu_items")
public class MenuItem extends BaseEntity {

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restaurant_id", nullable = false)
	private Restaurant restaurant;

	@NotBlank
	@Size(max = 150)
	@Column(nullable = false, length = 150)
	private String name;

	@Size(max = 500)
	@Column(length = 500)
	private String description;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false)
	@Digits(integer = 10, fraction = 2)
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal price;

	@NotBlank
	@Size(max = 50)
	@Column(nullable = false, length = 50)
	private String category;

	@NotNull
	@Column(nullable = false)
	private Boolean isAvailable = Boolean.TRUE;

	@OneToMany(mappedBy = "menuItem")
	private List<CartItem> cartItems = new ArrayList<>();
}