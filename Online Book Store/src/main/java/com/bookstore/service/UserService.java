package com.bookstore.service;

import com.bookstore.domain.entity.Cart;
import com.bookstore.domain.entity.User;
import com.bookstore.domain.enums.Role;
import com.bookstore.dto.auth.RegisterRequest;
import com.bookstore.exception.BadRequestException;
import com.bookstore.repository.CartRepository;
import com.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .email(req.getEmail().toLowerCase())
                .fullName(req.getFullName())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.USER)
                .build();

        user = userRepository.save(user);

        Cart cart = Cart.builder().user(user).build();
        cartRepository.save(cart);

        return user;
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new com.bookstore.exception.NotFoundException("User not found"));
    }
}
