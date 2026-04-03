package com.example.foodelivery.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.foodelivery.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	List<CartItem> findByCartId(Long cartId);
	Optional<CartItem> findByCartIdAndMenuItemId(Long cartId, Long menuItemId);
}