package com.example.foodelivery.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {

	@NotBlank
	@Size(max = 100)
	@Column(nullable = false, length = 100)
	private String name;

	@NotBlank
	@Email
	@Size(max = 150)
	@Column(nullable = false, unique = true, length = 150)
	private String email;

	@NotBlank
	@Size(min = 8, max = 255)
	@Column(nullable = false)
	private String password;

	@NotBlank
	@Pattern(regexp = "^\\d{10,15}$")
	@Column(nullable = false, unique = true, length = 15)
	private String phone;



	@OneToMany(mappedBy = "owner")
	private List<Restaurant> restaurants = new ArrayList<>();

	@OneToOne(mappedBy = "user")
	private Cart cart;

	@OneToMany(mappedBy = "user")
	private List<Order> orders = new ArrayList<>();
}