package com.example.foodelivery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.foodelivery.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
	List<Restaurant> findByOwnerId(Long ownerId);
}