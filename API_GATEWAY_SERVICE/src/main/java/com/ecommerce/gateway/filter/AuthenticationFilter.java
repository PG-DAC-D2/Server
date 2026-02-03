package com.ecommerce.gateway.filter;

import com.ecommerce.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            // ✅ 1. BYPASS OPTIONS REQUESTS (CORS preflight)
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequest().getMethod().name())) {
                return chain.filter(exchange);
            }

            String path = exchange.getRequest().getPath().toString();
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

            // ✅ 2. CHECK AUTHORIZATION HEADER
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header for path: {}", path);
                return unauthorized(exchange);
            }

            String token = authHeader.substring(7);
            Claims claims;

            try {
                claims = jwtUtil.extractClaims(token);
                logger.info("✅ JWT validated for path: {}", path);
            } catch (Exception e) {
                logger.error("❌ JWT validation failed: {}", e.getMessage());
                return unauthorized(exchange);
            }

            // ✅ 3. ROLE AUTHORIZATION CHECK
            String role = claims.get("role", String.class);

            if (!isAuthorized(path, role)) {
                logger.warn("⚠️ Unauthorized access attempt: path={}, role={}", path, role);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // ✅ 4. HEADER PROPAGATION (User Identity Forwarding)
            Object userIdObj = claims.get("user_id");

            if (userIdObj != null) {

                String userId = userIdObj.toString();

                ServerHttpRequest mutatedRequest = exchange.getRequest()
                        .mutate()
                        .header("X-User-Id", userId)
                        .build();

                exchange = exchange.mutate()
                        .request(mutatedRequest)
                        .build();

                logger.info("✅ Injected X-User-Id header: {}", userId);
            } else {
                logger.warn("⚠️ user_id not found in JWT claims");
            }

            return chain.filter(exchange);
        };
    }

    // ✅ ROLE BASED AUTHORIZATION
    private boolean isAuthorized(String path, String role) {

        String normalizedRole = role != null ? role.toUpperCase() : "";

        if (path.startsWith("/api/admin") && !normalizedRole.contains("ADMIN"))
            return false;

        if (path.startsWith("/api/orders") &&
                !(normalizedRole.contains("CUSTOMER") || normalizedRole.contains("ADMIN")))
            return false;

        if (path.startsWith("/api/cart") &&
                !(normalizedRole.contains("CUSTOMER") || normalizedRole.contains("ADMIN")))
            return false;

        if (path.startsWith("/api/products") &&
                !(normalizedRole.contains("MERCHANT") || normalizedRole.contains("ADMIN")))
            return false;

        return true;
    }

    // ✅ UNAUTHORIZED RESPONSE
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    public static class Config {}
}
