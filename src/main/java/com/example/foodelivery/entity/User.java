package com.example.foodelivery.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {


	@Column(nullable = false, length = 100)
	private String name;


	@Column(nullable = false, unique = true, length = 150)
	private String email;


	@Column(nullable = false)
	private String password;


	@Column(nullable = false, unique = true, length = 15)
	private String phone;




	@OneToMany(mappedBy = "owner")
	private List<Restaurant> restaurants = new ArrayList<>();

	@OneToOne(mappedBy = "user")
	private Cart cart;

	@OneToMany(mappedBy = "user")
	private List<Order> orders = new ArrayList<>();


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	@Override
	public String getUsername() {
		return email;
	}
}