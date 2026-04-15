package com.example.foodelivery;

import com.example.foodelivery.entity.Order;
import com.example.foodelivery.entity.OrderStatus;
import com.example.foodelivery.entity.Restaurant;
import com.example.foodelivery.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FoodeliveryApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	void testOrder() {
		User user = new User();
		user.setId(1L);
		user.setName("Test User");
		user.setEmail("test@example.com");
		user.setPassword("password");
		user.setPhone("0123456789");

		Restaurant restaurant = new Restaurant();
		restaurant.setId(2L);
		restaurant.setName("Test Restaurant");
		restaurant.setAddress("123 Test St");

		Order order = new Order();
		order.setUser(user);
		order.setRestaurant(restaurant);
		order.setTotalAmount(new BigDecimal("99.99"));
		order.setOrderStatus(OrderStatus.PLACED);

		assertNotNull(order);
		assertEquals(OrderStatus.PLACED, order.getOrderStatus());
		assertEquals(1L, order.getUser().getId());
		assertEquals(2L, order.getRestaurant().getId());
		assertEquals(new BigDecimal("99.99"), order.getTotalAmount());
	}
}
