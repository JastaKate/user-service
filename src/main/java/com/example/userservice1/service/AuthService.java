package com.example.userservice1.service;

import com.example.userservice1.dto.AuthenticationRequest;
import com.example.userservice1.dto.AuthenticationResponse;
import com.example.userservice1.dto.RegisterRequest;
import com.example.userservice1.entity.Role;
import com.example.userservice1.entity.Token;
import com.example.userservice1.entity.TokenType;
import com.example.userservice1.entity.User;
import com.example.userservice1.repository.TokenRepo;
import com.example.userservice1.repository.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final TokenRepo tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager manager;

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
//                .id(10)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepo.save(token);
    }

    private void revokeTokens(User user) {
        var validTokens = tokenRepo.findTokensByUser(user.getId());
        if (validTokens.isEmpty()) {
            return;
        }
        validTokens.forEach(t -> {
            t.setRevoked(true);
            t.setExpired(true);
        });
        tokenRepo.saveAll(validTokens);
    }

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
//                .id(1000)
                .name(request.getName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepo.save(user);
        var saveUser = userRepo.save(user);
        var jwtToken = jwtService.generateJwt(user);
        saveUserToken(saveUser, jwtToken);
        var refreshToken = jwtService.generateRefreshJwt(user);
        return AuthenticationResponse.builder()
//                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .accessToken(jwtToken)
                .build();
    }



    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()));
        var user = userRepo.findByUsername(request.getUsername()).orElseThrow();
        var jwtToken = jwtService.generateJwt(user);
        revokeTokens(user);
        saveUserToken(user, jwtToken);
        var refreshToken = jwtService.generateRefreshJwt(user);
        return AuthenticationResponse.builder()
//                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .accessToken(jwtToken)
                .build();
    }


    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            var user = this.userRepo.findByUsername(username).orElseThrow();
            if (jwtService.isValid(refreshToken, user)) {
                var accessToken = jwtService.generateJwt(user);
                revokeTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
