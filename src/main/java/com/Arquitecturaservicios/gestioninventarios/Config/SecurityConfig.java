package com.Arquitecturaservicios.gestioninventarios.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AdminKeyFilter adminKeyFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AdminKeyFilter adminKeyFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.adminKeyFilter = adminKeyFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/auth/reset-admin-password").permitAll()
                .requestMatchers("/roles/**", "/usuario-roles/**").hasRole("ADMIN")
                .requestMatchers("/usuarios/**").hasAnyRole("ADMIN", "GERENTE")
                .requestMatchers("/clientes/**").hasAnyRole("ADMIN", "VENDEDOR", "GERENTE")
                .requestMatchers("/proveedores/**").hasAnyRole("ADMIN", "AUX_BODEGA", "GERENTE")
                .requestMatchers("/productos/**").hasAnyRole("ADMIN", "AUX_BODEGA", "VENDEDOR", "GERENTE")
                .requestMatchers("/ventas/**").hasAnyRole("ADMIN", "VENDEDOR", "GERENTE")
                .anyRequest().authenticated()
            )
            .addFilterBefore(adminKeyFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
