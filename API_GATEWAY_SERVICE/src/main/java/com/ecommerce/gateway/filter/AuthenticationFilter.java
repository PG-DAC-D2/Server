package com.ecommerce.gateway.filter;

import com.ecommerce.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorized(exchange);
            }

            String token = authHeader.substring(7);
            Claims claims;
            try {
                claims = jwtUtil.extractClaims(token);
            } catch (Exception e) {
                return unauthorized(exchange);
            }

            String role = claims.get("role", String.class);
            String path = exchange.getRequest().getPath().toString();

            if (!isAuthorized(path, role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    private boolean isAuthorized(String path, String role) {
        if (path.startsWith("/api/admin") && !role.equals("ADMIN")) return false;
        if (path.startsWith("/api/orders") && !(role.equals("CUSTOMER") || role.equals("ADMIN"))) return false;
        if (path.startsWith("/api/products") && !(role.equals("MERCHANT") || role.equals("ADMIN"))) return false;
        return true;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    public static class Config {}
}