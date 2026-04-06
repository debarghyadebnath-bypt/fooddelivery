package com.example.foodelivery.service;

import com.example.foodelivery.dto.PageableDto;
import com.example.foodelivery.dto.RestaurantDto;
import com.example.foodelivery.dto.RestaurantRequestDto;
import com.example.foodelivery.entity.Restaurant;
import com.example.foodelivery.entity.User;
import com.example.foodelivery.exceptions.ResourceNotFoundException;
import com.example.foodelivery.repository.RestaurantRepository;
import com.example.foodelivery.utils.PageResponse;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public RestaurantService(RestaurantRepository restaurantRepository, UserService userService, ModelMapper modelMapper) {
        this.restaurantRepository = restaurantRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    public Restaurant findRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
    }

    @Transactional
    public RestaurantDto create(Long ownerId, RestaurantRequestDto restaurantRequestDto) {
        User owner = userService.getUserById(ownerId);
        Restaurant restaurant = modelMapper.map(restaurantRequestDto, Restaurant.class);
        restaurant.setOwner(owner);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return modelMapper.map(savedRestaurant, RestaurantDto.class);
    }

    public PageResponse<RestaurantDto> getAll(String title, Pageable pageable) {
        if (title != null && !title.isEmpty()) {
            Page<RestaurantDto> restaurantDtos = restaurantRepository.findByNameContainingIgnoreCase(title, pageable)
                    .map(restaurant -> modelMapper.map(restaurant, RestaurantDto.class));

            return new PageResponse<>(restaurantDtos.getContent(),
                    new PageableDto(pageable.getPageNumber(), pageable.getPageSize())
                    , restaurantDtos.getTotalElements(), restaurantDtos.getTotalPages()
                    , restaurantDtos.getSize(), restaurantDtos.getNumberOfElements());

        }
        Page<RestaurantDto> restaurantDtos = restaurantRepository.findAll(pageable)
                .map(restaurant -> modelMapper.map(restaurant, RestaurantDto.class));

            return new PageResponse<>(restaurantDtos.getContent(),
                    new PageableDto(pageable.getPageNumber(), pageable.getPageSize())
                    , restaurantDtos.getTotalElements(), restaurantDtos.getTotalPages()
                    , restaurantDtos.getSize(), restaurantDtos.getNumberOfElements());

    }


    public Restaurant findRestaurant(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
    }


    public void delete(Long id) {
        Restaurant restaurant = findRestaurant(id);
        restaurantRepository.delete(restaurant);
    }


    public RestaurantDto getById(Long id) {
        Restaurant restaurant = findRestaurant(id);
        return modelMapper.map(restaurant, RestaurantDto.class);
    }


    @Transactional
    public RestaurantDto update(Long id, RestaurantDto restaurantDto) {
        Restaurant restaurant = findRestaurant(id);
        modelMapper.map(restaurantDto, restaurant);
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        return modelMapper.map(updatedRestaurant, RestaurantDto.class);
    }

}