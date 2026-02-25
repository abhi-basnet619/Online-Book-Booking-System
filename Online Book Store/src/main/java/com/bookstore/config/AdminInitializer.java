package com.bookstore.config;

import com.bookstore.domain.entity.Cart;
import com.bookstore.domain.entity.User;
import com.bookstore.domain.enums.Role;
import com.bookstore.repository.CartRepository;
import com.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository,
                                CartRepository cartRepository,
                                PasswordEncoder encoder) {
        return args -> {
            String email = "admin@bookstore.com";
            if (userRepository.existsByEmail(email)) return;

            User admin = User.builder()
                    .email(email)
                    .fullName("System Admin")
                    .password(encoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .build();

            admin = userRepository.save(admin);

            // optional cart for admin (safe)
            cartRepository.save(Cart.builder().user(admin).build());
        };
    }
}