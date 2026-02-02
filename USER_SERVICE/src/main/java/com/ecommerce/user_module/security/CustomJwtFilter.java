package com.ecommerce.user_module.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecommerce.user_module.dtos.JWTdto;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomJwtFilter extends OncePerRequestFilter {

	private final JWTUtils jwtUtils;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();

		return path.equals("/users/signin") || path.equals("/users/pwd-encryption")
				|| path.equals("/customers/register") || path.equals("/merchants/register")
				|| path.equals("/admin/register")
				|| path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String headerValue = request.getHeader("Authorization");

		if (headerValue != null && headerValue.startsWith("Bearer ")) {

			String jwt = headerValue.substring(7);
			log.info("jwt found {}", jwt);

			Claims claims = jwtUtils.validateJWT(jwt);

			String role = claims.get("role", String.class);
			Long userId = claims.get("user_id", Long.class);
			String addr = claims.get("address", String.class);

			JWTdto dto = new JWTdto(userId, role, addr);

			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(dto, null,
					List.of(new SimpleGrantedAuthority(role)));

			log.info("Granted authorities: {}", auth.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(auth);
			log.info("security context populated");
		}

		filterChain.doFilter(request, response);
	}
}