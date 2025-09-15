package com.example.TeamResourceAccessManagement.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_BASE64="sKJ9J8yTzMvuQmA4l/0gkV5wW+fJjzt+QbW6RvzcsEk=";
    private static final long EXPIRATION_Ms=86_400_000; // 24 hours

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_BASE64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username) {
        return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_Ms))
        .signWith(getSigningKey(),SignatureAlgorithm.HS256)
        .compact();
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }
    
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }
    
    private boolean istokenexpired(String token){
        return extractExpiration(token).before(new Date());
    }
    
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    public boolean validateToken(String token,String username){
        return (extractUsername(token).equals(username) && !istokenexpired(token));
    }

    public boolean validateToken(String token,UserDetails UserDetails){
        return (extractUsername(token).equals(UserDetails.getUsername()) && !istokenexpired(token));
    }
}