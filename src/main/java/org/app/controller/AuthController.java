package org.app.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.app.DTO.AppUserDTO;
import org.app.DTO.AuthDTO;
import org.app.DTO.AuthResponseDTO;
import org.app.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register (@RequestBody AppUserDTO appUserDTO) {

        AuthResponseDTO response = authService.registerUser(appUserDTO);

        if ("Success".equals(response.getMessage())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login (@RequestBody AuthDTO authDTO,
                                                  HttpServletResponse resp) {
        AuthResponseDTO response = authService.loginUser(authDTO);
        if (!"Success".equals(response.getMessage())) return ResponseEntity.badRequest().body(response);

        ResponseCookie cookie = ResponseCookie.from("token", response.getToken())
                .httpOnly(true)
                .secure(false)           // true if HTTPS
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofHours(8))
                .build();

        resp.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(response);
    }
}
