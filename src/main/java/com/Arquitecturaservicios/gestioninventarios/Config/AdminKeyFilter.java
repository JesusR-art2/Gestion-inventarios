package com.Arquitecturaservicios.gestioninventarios.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AdminKeyFilter extends OncePerRequestFilter {

    @Value("${ADMIN_KEY:dev-admin-secret-key-2024}")
    private String adminKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Solo aplicar filtro al endpoint de reset de admin
        if (path.equals("/auth/reset-admin-password")) {
            String providedKey = request.getHeader("X-Admin-Key");
            
            if (providedKey == null || !providedKey.equals(adminKey)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or missing X-Admin-Key header");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
