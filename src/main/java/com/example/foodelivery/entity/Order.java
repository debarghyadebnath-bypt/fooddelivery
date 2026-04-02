package com.example.foodelivery.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restaurant_id", nullable = false)
	private Restaurant restaurant;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false)
	@Digits(integer = 12, fraction = 2)
	@Column(nullable = false, precision = 14, scale = 2)
	private BigDecimal totalAmount;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private OrderStatus orderStatus = OrderStatus.PLACED;

	@OneToOne(mappedBy = "order")
	private Payment payment;
}