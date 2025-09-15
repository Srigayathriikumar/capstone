package com.example.TeamResourceAccessManagement.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    public void testTokenGeneration() {
        String username = "testuser";
        String token = jwtService.generateToken(username);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        System.out.println("Generated Token: " + token);
    }

    @Test
    public void testExtractUsername() {
        String username = "testuser";
        String token = jwtService.generateToken(username);
        String extractedUsername = jwtService.extractUsername(token);
        
        assertEquals(username, extractedUsername);
        System.out.println("Original Username: " + username);
        System.out.println("Extracted Username: " + extractedUsername);
    }

    @Test
    public void testExtractExpiration() {
        String username = "testuser";
        String token = jwtService.generateToken(username);
        Date expiration = jwtService.extractExpiration(token);
        
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
        System.out.println("Token Expiration: " + expiration);
    }

    @Test
    public void testTokenValidation() {
        String username = "testuser";
        String token = jwtService.generateToken(username);
        boolean isValid = jwtService.validateToken(token, username);
        
        assertTrue(isValid);
        System.out.println("Token is valid: " + isValid);
    }

    @Test
    public void testTokenValidationWithWrongUsername() {
        String username = "testuser";
        String wrongUsername = "wronguser";
        String token = jwtService.generateToken(username);
        boolean isValid = jwtService.validateToken(token, wrongUsername);
        
        assertFalse(isValid);
        System.out.println("Token validation with wrong username: " + isValid);
    }

    @Test
    public void testCompleteTokenFlow() {
        System.out.println("\n=== JWT Token Flow Test ===");
        
        // 1. Generate token
        String username = "admin";
        String token = jwtService.generateToken(username);
        System.out.println("1. Generated token for user '" + username + "'");
        System.out.println("   Token: " + token);
        
        // 2. Extract username
        String extractedUsername = jwtService.extractUsername(token);
        System.out.println("2. Extracted username: " + extractedUsername);
        
        // 3. Extract expiration
        Date expiration = jwtService.extractExpiration(token);
        System.out.println("3. Token expires at: " + expiration);
        
        // 4. Validate token
        boolean isValid = jwtService.validateToken(token, username);
        System.out.println("4. Token validation result: " + isValid);
        
        // 5. Check token structure
        String[] tokenParts = token.split("\\.");
        System.out.println("5. Token has " + tokenParts.length + " parts (header.payload.signature)");
        
        // Assertions
        assertEquals(username, extractedUsername);
        assertTrue(expiration.after(new Date()));
        assertTrue(isValid);
        assertEquals(3, tokenParts.length);
        
        System.out.println("✅ All JWT operations completed successfully!");
    }
}