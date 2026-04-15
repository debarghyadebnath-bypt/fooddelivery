package com.example.foodelivery.controller;

import com.example.foodelivery.advices.ApiResponse;
import com.example.foodelivery.dto.OrderDto;
import com.example.foodelivery.dto.RestaurantDto;
import com.example.foodelivery.dto.RestaurantRequestDto;
import com.example.foodelivery.entity.User;
import com.example.foodelivery.service.OrderService;
import com.example.foodelivery.service.RestaurantService;
import com.example.foodelivery.utils.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final OrderService orderService;

    public RestaurantController(RestaurantService restaurantService, OrderService orderService) {
        this.restaurantService = restaurantService;
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<RestaurantDto> create(@AuthenticationPrincipal User authenticatedUser,
                                                @Valid @RequestBody RestaurantRequestDto restaurantRequestDto) {
        if (authenticatedUser == null) {
            throw new IllegalStateException("Authenticated user is required to create a restaurant");
        }
        RestaurantDto createdRestaurant = restaurantService.create(authenticatedUser.getId(), restaurantRequestDto);
        return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
    }

    @GetMapping
    public  ResponseEntity<ApiResponse<PageResponse<RestaurantDto>>> getAll(@RequestParam(required = false) String title, @RequestParam(defaultValue = "0") int pageNo,
                                                                            @RequestParam(defaultValue = "3") int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        PageResponse<RestaurantDto> restaurantPageResponse = restaurantService.getAll(title,pageable);;
        return new ResponseEntity<>(new ApiResponse<>(restaurantPageResponse), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDto> getById(@PathVariable Long id) {
        RestaurantDto restaurantDto = restaurantService.getById(id);
        return new ResponseEntity<>(restaurantDto, HttpStatus.FOUND);
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<ApiResponse<PageResponse<OrderDto>>> getOrdersByRestaurantId(@AuthenticationPrincipal User authenticatedUser,
                                                                                       @PathVariable Long id,
                                                                                       @RequestParam(defaultValue = "0") int pageNo,
                                                                                       @RequestParam(defaultValue = "10") int pageSize) {
        if (authenticatedUser == null) {
            throw new IllegalStateException("Authenticated user is required to view restaurant orders");
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        PageResponse<OrderDto> orderPageResponse = orderService.getByRestaurantId(id, pageable);
        return new ResponseEntity<>(new ApiResponse<>(orderPageResponse), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDto> update(@PathVariable Long id,
                                               @Valid @RequestBody RestaurantDto restaurantDto) {
        RestaurantDto updatedRestaurant = restaurantService.update(id, restaurantDto);
        return new ResponseEntity<>(updatedRestaurant, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        restaurantService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }





}