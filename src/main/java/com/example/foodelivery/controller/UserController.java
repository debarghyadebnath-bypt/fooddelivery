package com.example.foodelivery.controller;

import com.example.foodelivery.dto.ProfileDto;
import com.example.foodelivery.entity.User;
import com.example.foodelivery.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> getProfile(@AuthenticationPrincipal User authenticatedUser){
        ProfileDto profileDto = userService.getProfile(authenticatedUser.getId());
        return new ResponseEntity<>(profileDto, HttpStatus.FOUND);
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileDto> updateProfile(@AuthenticationPrincipal User authenticatedUser,
                                                       @Valid @RequestBody ProfileDto profileDto){
        ProfileDto updatedProfile = userService.updateProfile(authenticatedUser.getId(), profileDto);
        return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
    }
}
