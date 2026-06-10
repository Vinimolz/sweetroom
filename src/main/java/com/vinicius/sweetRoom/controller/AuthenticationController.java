package com.vinicius.sweetRoom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinicius.sweetRoom.DTOs.AuthenticationDTO;
import com.vinicius.sweetRoom.model.User;
import com.vinicius.sweetRoom.security.TokenJWTData;
import com.vinicius.sweetRoom.security.TokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/login")
public class AuthenticationController {

    private final AuthenticationManager authManager;
    private final TokenService tokenService;

    public AuthenticationController(AuthenticationManager authManager, TokenService tokenService) {
        this.authManager = authManager;
        this.tokenService = tokenService;
    }

    @PostMapping()
    public ResponseEntity<TokenJWTData> login(@Valid @RequestBody AuthenticationDTO dto) {
        var token = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        var auth = authManager.authenticate(token);
        
        var principal = auth.getPrincipal();
        if (principal instanceof User user) {
            var tokenJWT = tokenService.generateToken(user);
            return ResponseEntity.ok(new TokenJWTData(tokenJWT));
        }
        
        throw new IllegalStateException("Authentication principal is not of type User");
    }
}
