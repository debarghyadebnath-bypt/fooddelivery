package com.example.foodelivery.service;

import com.example.foodelivery.dto.MenuItemDto;
import com.example.foodelivery.dto.MenuItemRequestDto;
import com.example.foodelivery.entity.MenuItem;
import com.example.foodelivery.entity.Restaurant;
import com.example.foodelivery.exceptions.ResourceNotFoundException;
import com.example.foodelivery.repository.MenuItemRepository;
import com.example.foodelivery.repository.RestaurantRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuService {
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final ModelMapper modelMapper;

    public MenuService(MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository, ModelMapper modelMapper) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public MenuItemDto create(MenuItemRequestDto menuItemRequestDto) {
        Restaurant restaurant = findRestaurantById(menuItemRequestDto.getRestaurantId());
        MenuItem menuItem = new MenuItem();
        menuItem.setRestaurant(restaurant);
        menuItem.setName(menuItemRequestDto.getName());
        menuItem.setDescription(menuItemRequestDto.getDescription());
        menuItem.setPrice(menuItemRequestDto.getPrice());
        menuItem.setCategory(menuItemRequestDto.getCategory());
        menuItem.setIsAvailable(menuItemRequestDto.getIsAvailable() == null ? Boolean.TRUE : menuItemRequestDto.getIsAvailable());
        menuItemRepository.save(menuItem);
        return modelMapper.map(menuItem, MenuItemDto.class);
    }

    @Transactional(readOnly = true)
    public List<MenuItemDto> getByRestaurantId(Long restaurantId) {
        findRestaurantById(restaurantId);
        return menuItemRepository.findByRestaurant_Id(restaurantId)
                .stream()
                .map(menuItem -> modelMapper.map(menuItem, MenuItemDto.class))
                .toList();
    }

    @Transactional
    public MenuItemDto update(Long id, MenuItemRequestDto menuItemRequestDto) {

        MenuItem menuItem = findMenuItemById(id);
        /*
        modelMapper.map(menuItemRequestDto, menuItem);

        Restaurant restaurant = findRestaurantById(menuItemRequestDto.getRestaurantId());
        menuItem.setRestaurant(restaurant);
        menuItemRepository.save(menuItem);
         return  modelMapper.map(menuItem,MenuItemDto.class);

*/
        Restaurant restaurant = findRestaurantById(menuItemRequestDto.getRestaurantId());
        menuItem.setRestaurant(restaurant);
        menuItem.setName(menuItemRequestDto.getName());
        menuItem.setDescription(menuItemRequestDto.getDescription());
        menuItem.setPrice(menuItemRequestDto.getPrice());
        menuItem.setCategory(menuItemRequestDto.getCategory());
        if (menuItemRequestDto.getName() != null) {
            menuItem.setName(menuItemRequestDto.getName());
        }
        menuItemRepository.save(menuItem);
        return modelMapper.map(menuItem, MenuItemDto.class);





    }

    public void delete(Long id) {
        MenuItem menuItem = findMenuItemById(id);
        menuItemRepository.delete(menuItem);
    }

    private MenuItem findMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
    }

    private Restaurant findRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
    }
    /*
    private MenuItemDto toDto(MenuItem menuItem) {
        MenuItemDto menuItemDto = modelMapper.map(menuItem, MenuItemDto.class);
        menuItemDto.setRestaurantId(menuItem.getRestaurant().getId());
        return menuItemDto;
    }

     */
}