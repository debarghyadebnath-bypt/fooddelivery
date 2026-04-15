package com.example.foodelivery.service;

import com.example.foodelivery.dto.CartDto;
import com.example.foodelivery.dto.CartItemRequestDto;
import com.example.foodelivery.entity.Cart;
import com.example.foodelivery.entity.CartItem;
import com.example.foodelivery.entity.CartStatus;
import com.example.foodelivery.entity.MenuItem;
import com.example.foodelivery.entity.User;
import com.example.foodelivery.exceptions.ResourceNotFoundException;
import com.example.foodelivery.repository.CartItemRepository;
import com.example.foodelivery.repository.CartRepository;
import com.example.foodelivery.repository.MenuItemRepository;
import com.example.foodelivery.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       MenuItemRepository menuItemRepository,
                       UserRepository userRepository,
                       ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.menuItemRepository = menuItemRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public CartDto getCart(Long userId) {
        Cart cart = findOrCreateCart(userId);
        return modelMapper.map(cart, CartDto.class);
    }

    @Transactional(readOnly = true)
    public Cart getActiveCart(Long userId) {
        return findExistingCart(userId);
    }

    @Transactional
    public CartDto addItem(Long userId , CartItemRequestDto cartItemRequestDto) {
        Cart cart = findOrCreateCart(userId);
        MenuItem menuItem = findMenuItemById(cartItemRequestDto.getMenuItemId());

        CartItem cartItem = cartItemRepository.findByCartIdAndMenuItemId(cart.getId(), menuItem.getId())
                .orElseGet(CartItem::new);

        cartItem.setCart(cart);
        cartItem.setMenuItem(menuItem);
        cartItem.setQuantity(cartItem.getQuantity() == null
                ? cartItemRequestDto.getQuantity()
                : cartItem.getQuantity() + cartItemRequestDto.getQuantity());
        cartItem.setPrice(menuItem.getPrice());

        if (cartItem.getId() == null) {
            cart.getCartItems().add(cartItem);
        }

        cartItemRepository.save(cartItem);
        return modelMapper.map(cart, CartDto.class);
    }

    @Transactional
    public CartDto updateItem(Long userId, CartItemRequestDto cartItemRequestDto) {
        Cart cart = findExistingCart(userId);
        MenuItem menuItem = findMenuItemById(cartItemRequestDto.getMenuItemId());
        CartItem cartItem = findCartItemByCartIdAndMenuItemId(cart.getId(), menuItem.getId());

        cartItem.setQuantity(cartItemRequestDto.getQuantity());
        cartItem.setPrice(menuItem.getPrice());


        cartItemRepository.save(cartItem);
        return modelMapper.map(cart, CartDto.class);
    }

    @Transactional
    public CartDto removeItem(Long userId, Long menuItemId) {
        Cart cart = findExistingCart(userId);
        CartItem cartItem = findCartItemByCartIdAndMenuItemId(cart.getId(), menuItemId);

        cart.getCartItems().removeIf(item -> item.getMenuItem().getId().equals(menuItemId));
        cartItemRepository.delete(cartItem);
        return modelMapper.map(cart, CartDto.class);
    }

    private Cart findOrCreateCart(Long userId) {
        return cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    User user = findUserById(userId);
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setStatus(CartStatus.ACTIVE);
                    return cartRepository.save(cart);
                });
    }

    private Cart findExistingCart(Long userId) {
        return cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));
    }

    private MenuItem findMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private CartItem findCartItemByCartIdAndMenuItemId(Long cartId, Long menuItemId) {
        return cartItemRepository.findByCartIdAndMenuItemId(cartId, menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart item not found for cart id: " + cartId + " and menu item id: " + menuItemId));
    }




}