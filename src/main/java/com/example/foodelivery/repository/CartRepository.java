package com.example.foodelivery.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.foodelivery.entity.Cart;
import com.example.foodelivery.entity.CartStatus;
import org.springframework.stereotype.Repository;

@Repository

public interface CartRepository extends JpaRepository<Cart, Long> {
	Optional<Cart> findByUserId(Long userId);
	Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);
}