package com.ecommerce.user_module.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final CorsConfigurationSource corsConfigurationSource;
	private final PasswordEncoder passwordEncoder;
	private final CustomJwtFilter customJwtFilter;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable());
		http.cors(cors -> cors.configurationSource(corsConfigurationSource));
		http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		http.authorizeHttpRequests(request -> request
				// Public/Swagger endpoints
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				
				// Authentication endpoints (no auth required)
				.requestMatchers(HttpMethod.POST, "/users/signin").permitAll()
				.requestMatchers("/users/pwd-encryption").permitAll()
				
				// Registration endpoints (no auth required) - Must come before role-based rules
				.requestMatchers(HttpMethod.POST, "/customers/register").permitAll()
				.requestMatchers(HttpMethod.POST, "/merchants/register").permitAll()
				.requestMatchers(HttpMethod.POST, "/admin/register").permitAll()
				
				// Customer endpoints (require CUSTOMER role)
				.requestMatchers(HttpMethod.PUT, "/customers/update").hasRole("CUSTOMER")
				.requestMatchers(HttpMethod.GET, "/customers/**").hasRole("CUSTOMER")
				
				// Merchant endpoints (require MERCHANT role)
				.requestMatchers(HttpMethod.PUT, "/merchants/update").hasAnyRole("MERCHANT", "ADMIN")
				.requestMatchers(HttpMethod.GET, "/merchants/**").hasAnyRole("MERCHANT", "ADMIN")
				
				// Admin endpoints (require ADMIN role)
				.requestMatchers(HttpMethod.PUT, "/admin/update").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN")
				
				// All other requests require authentication
				.anyRequest().authenticated())
		.addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:3000"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

}