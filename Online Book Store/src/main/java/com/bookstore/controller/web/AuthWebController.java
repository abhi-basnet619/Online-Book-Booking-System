package com.bookstore.controller.web;

import com.bookstore.dto.auth.LoginRequest;
import com.bookstore.dto.auth.RegisterRequest;
import com.bookstore.security.JwtUtil;
import com.bookstore.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthWebController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Value("${app.security.jwt.expiration-minutes}")
    private long expirationMinutes;

    @Value("${app.security.jwt.cookie-name}")
    private String cookieName;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @PostMapping("/login")
    public String doLogin(@Valid @ModelAttribute("loginRequest") LoginRequest req,
                          HttpServletResponse response) {

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        String role = auth.getAuthorities().stream().findFirst().map(a -> a.getAuthority().replace("ROLE_","")).orElse("USER");
        String token = jwtUtil.generateToken(req.getEmail(), com.bookstore.domain.enums.Role.valueOf(role), expirationMinutes);

        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // set true behind HTTPS
        cookie.setPath("/");
        cookie.setMaxAge((int) (expirationMinutes * 60));
        response.addCookie(cookie);

        if ("ADMIN".equals(role)) return "redirect:/admin";
        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("registerRequest") RegisterRequest req) {
        userService.registerUser(req);
        return "redirect:/auth/login?registered=true";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }
}
