package com.example.foodelivery.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.foodelivery.entity.Order;
import com.example.foodelivery.entity.OrderStatus;
import org.springframework.stereotype.Repository;

@Repository

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByUserId(Long userId);
	Page<Order> findByUserId(Long userId, Pageable pageable);
	List<Order> findByRestaurantId(Long restaurantId);
	Page<Order> findByRestaurantId(Long restaurantId, Pageable pageable);
	Page<Order> findByOrderStatusIn(List<OrderStatus> orderStatuses, Pageable pageable);
	Optional<Order> findByIdAndUserId(Long id, Long userId);
}