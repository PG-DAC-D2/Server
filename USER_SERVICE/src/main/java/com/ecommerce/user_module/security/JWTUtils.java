package com.ecommerce.user_module.security;


import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ecommerce.user_module.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JWTUtils {
	@Value("${jwt.secret.key}")
	private String secretKey;
	@Value("${jwt.expiration.time}")
	private long expTime;
	private SecretKey key;

	@PostConstruct
	public void myInit() {
		key = Keys.hmacShaKeyFor(secretKey.getBytes());
		log.info("exp time {} key {}", expTime, key);

	}

	// generate token
	public String generateToken(User user) {
		Date createdOn = new Date();
		Date expDate = new Date(createdOn.getTime() + expTime);
		return Jwts.builder().subject(user.getEmail())
				.issuedAt(createdOn)
				.expiration(expDate)
				.claims(Map.of("user_id", user.getId(), "role", user.getRole().name()))
				.signWith(key).compact();
	}

	// validate token
	public Claims validateJWT(String jwt) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();

	}
}