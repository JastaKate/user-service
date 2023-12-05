package com.example.userservice1.controllers;

import com.example.userservice1.dto.AuthenticationRequest;
import com.example.userservice1.dto.AuthenticationResponse;
import com.example.userservice1.dto.RegisterRequest;
import com.example.userservice1.entity.User;
import com.example.userservice1.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }

    @DeleteMapping("/delete/{id}")
    public HttpStatus delete(@PathVariable Integer id) {
        authService.deleteUser(id);
        kafkaTemplate.send("user-topic", "user deleted");
        return HttpStatus.OK;
    }
}
