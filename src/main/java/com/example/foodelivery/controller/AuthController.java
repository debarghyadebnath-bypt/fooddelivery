package com.example.foodelivery.controller;

import com.example.foodelivery.dto.LoginDto;
import com.example.foodelivery.dto.UserDto;
import com.example.foodelivery.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register( @Valid  @RequestBody UserDto userDto){
        UserDto registeredUser = userService.register(userDto);
        return ResponseEntity.ok(registeredUser);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginDto> login(@Valid @RequestBody LoginDto loginDto){
        return ResponseEntity.ok(userService.login(loginDto));
    }




}
