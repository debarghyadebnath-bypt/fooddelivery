package com.example.foodelivery.controller;

import com.example.foodelivery.dto.MenuItemDto;
import com.example.foodelivery.dto.MenuItemRequestDto;
import com.example.foodelivery.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {
	private final MenuService menuService;

	public MenuController(MenuService menuService) {
		this.menuService = menuService;
	}

	@PostMapping
	public ResponseEntity<MenuItemDto> create(@Valid @RequestBody MenuItemRequestDto menuItemRequestDto) {
		MenuItemDto createdMenuItem = menuService.create(menuItemRequestDto);
		return new ResponseEntity<>(createdMenuItem, HttpStatus.CREATED);
	}

	@GetMapping("/restaurant/{restaurantId}")
	public ResponseEntity<List<MenuItemDto>> getByRestaurant(@PathVariable Long restaurantId) {
		List<MenuItemDto> menuItems = menuService.getByRestaurantId(restaurantId);
		return new ResponseEntity<>(menuItems, HttpStatus.OK);
	}

	@PutMapping("/{id}")
	public ResponseEntity<MenuItemDto> update(@PathVariable Long id,
											  @Valid @RequestBody MenuItemRequestDto menuItemRequestDto) {
		MenuItemDto updatedMenuItem = menuService.update(id, menuItemRequestDto);
		return new ResponseEntity<>(updatedMenuItem, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		menuService.delete(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
