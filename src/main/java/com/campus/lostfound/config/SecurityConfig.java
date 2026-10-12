package com.campus.lostfound.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 6 Configuration.
 * 
 * VIVA TIP:
 * Configures stateless JWT authentication:
 * 1. CSRF is disabled because stateless REST APIs do not use browser cookies for auth.
 * 2. Session creation policy is STATELESS (no server-side HTTP session storage).
 * 3. BCryptPasswordEncoder hashes user passwords using salted cryptographic hashing.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless REST API
            .csrf(AbstractHttpConfigurer::disable)
            // Enable CORS configuration
            .cors(Customizer.withDefaults())
            // Configure stateless session management
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Endpoint permission rules
            .authorizeHttpRequests(auth -> auth
                // Public auth endpoints (register and login)
                .requestMatchers("/api/auth/**").permitAll()
                // Public image uploads access
                .requestMatchers("/uploads/**").permitAll()
                // Public read-only browsing of items
                .requestMatchers(HttpMethod.GET, "/api/items/**").permitAll()
                // Admin-only endpoints
                .requestMatchers("/api/users/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/claims/all").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/claims/{id}/status").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/claims/{id}/verify-otp").hasAuthority("ROLE_ADMIN")
                // All other endpoints require authenticated JWT
                .anyRequest().authenticated()
            );

        // Register custom JWT filter before standard username/password filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
