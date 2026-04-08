package com.example.foodelivery.configs;

import com.example.foodelivery.dto.CartDto;
import com.example.foodelivery.dto.CartItemDto;
import com.example.foodelivery.entity.Cart;
import com.example.foodelivery.entity.CartItem;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        TypeMap<CartItem, CartItemDto> cartItemMap = modelMapper.createTypeMap(CartItem.class, CartItemDto.class);
        cartItemMap.addMappings(mapper -> {
            mapper.map(source -> source.getCart().getId(), CartItemDto::setCartId);
            mapper.map(source -> source.getMenuItem().getId(), CartItemDto::setMenuItemId);
        });

        TypeMap<Cart, CartDto> cartMap = modelMapper.createTypeMap(Cart.class, CartDto.class);
        cartMap.addMappings(mapper -> {
            mapper.map(source -> source.getUser().getId(), CartDto::setUserId);
            mapper.map(Cart::getCartItems, CartDto::setItems);
        });

        Converter<Cart, BigDecimal> totalAmountConverter = context -> context.getSource().getCartItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cartMap.addMappings(mapper -> mapper.using(totalAmountConverter).map(source -> source, CartDto::setTotalAmount));

        return modelMapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
