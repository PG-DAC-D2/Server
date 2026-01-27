package com.ecommerce.security;

import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component~
public class JwtUtil {

    private String secret = "sldfjl2r3o48kk2j3h4lkjhklj"; // Change this to a secure key
    private int jwtExpirationMs = 86400000; // 24 hours

    public String createToken(Authentication auth) {
        org.springframework.security.core.userdetails.UserDetails user = (org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}