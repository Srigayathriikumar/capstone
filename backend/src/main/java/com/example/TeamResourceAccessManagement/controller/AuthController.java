package com.example.TeamResourceAccessManagement.controller;

import com.example.TeamResourceAccessManagement.dto.LoginRequestDTO;
import com.example.TeamResourceAccessManagement.dto.LoginResponseDTO;
import com.example.TeamResourceAccessManagement.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        String token = jwtService.generateToken(authentication.getName());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
    
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String username = request.get("username");
        
        boolean isValid = jwtService.validateToken(token, username);
        String extractedUsername = jwtService.extractUsername(token);
        Date expiration = jwtService.extractExpiration(token);
        
        return ResponseEntity.ok(Map.of(
            "valid", isValid,
            "username", extractedUsername,
            "expiration", expiration
        ));
    }
    
    @PostMapping("/extract-username")
    public ResponseEntity<Map<String, String>> extractUsername(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String username = jwtService.extractUsername(token);
        
        return ResponseEntity.ok(Map.of("username", username));
    }
    
    @PostMapping("/extract-expiration")
    public ResponseEntity<Map<String, Object>> extractExpiration(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        Date expiration = jwtService.extractExpiration(token);
        
        return ResponseEntity.ok(Map.of("expiration", expiration));
    }
}