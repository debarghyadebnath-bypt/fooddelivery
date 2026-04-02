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
import jakarta.validation.constraints.DecimalMax;
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
@Table(name = "restaurants")
public class Restaurant extends BaseEntity {

	@NotBlank
	@Size(max = 150)
	@Column(nullable = false, length = 150)
	private String name;

	@NotBlank
	@Size(max = 255)
	@Column(nullable = false, length = 255)
	private String address;

	@NotNull
	@DecimalMin(value = "0.0")
	@DecimalMax(value = "5.0")
	@Digits(integer = 1, fraction = 2)
	@Column(nullable = false, precision = 3, scale = 2)
	private BigDecimal rating = BigDecimal.ZERO;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	@OneToMany(mappedBy = "restaurant")
	private List<MenuItem> menuItems = new ArrayList<>();

	@OneToMany(mappedBy = "restaurant")
	private List<Order> orders = new ArrayList<>();
}