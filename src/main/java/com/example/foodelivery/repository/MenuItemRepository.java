package com.example.foodelivery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.foodelivery.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
	List<MenuItem> findByRestaurantId(Long restaurantId);
}