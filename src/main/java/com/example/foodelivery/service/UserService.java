package com.example.foodelivery.service;

import com.example.foodelivery.dto.LoginDto;
import com.example.foodelivery.dto.ProfileDto;
import com.example.foodelivery.dto.UserDto;
import com.example.foodelivery.entity.User;
import com.example.foodelivery.entity.UserRole;
import com.example.foodelivery.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ModelMapper   modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    public ProfileDto getProfile(Long id) {
        return modelMapper.map(getUserById(id), ProfileDto.class);
    }

    public UserDto register(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        if (user.getRole() == null) {
            user.setRole(UserRole.CUSTOMER);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedEntity = userRepository.save(user);
        return modelMapper.map(savedEntity, UserDto.class);


    }

    @Transactional
    public ProfileDto updateProfile(Long id, ProfileDto profileDto) {
        User user = getUserById(id);
        modelMapper.map(profileDto, user);
        if (profileDto.getRole() != null) {
            user.setRole(profileDto.getRole());
        }
        return modelMapper.map(userRepository.save(user), ProfileDto.class);
    }

    public LoginDto login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + loginDto.getEmail()));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        LoginDto response = new LoginDto();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setToken(jwtService.generateAccessToken(user));
        return response;
    }

}
