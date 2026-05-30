package com.aitravel.smartplanner.auth;

import com.aitravel.smartplanner.security.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/demo-token")
    TokenResponse demoToken(@Valid @RequestBody DemoTokenRequest request) {
        return new TokenResponse(jwtService.createToken(request.email(), request.name()));
    }

    public record DemoTokenRequest(@Email String email, @NotBlank String name) {
    }

    public record TokenResponse(String token) {
    }
}
