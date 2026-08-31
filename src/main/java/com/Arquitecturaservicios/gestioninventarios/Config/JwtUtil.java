package com.Arquitecturaservicios.gestioninventarios.Config;

import com.Arquitecturaservicios.gestioninventarios.Entities.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret:miClaveSecretaSuperSeguraParaJWT123456789}")
    private String secret;

    @Value("${jwt.expiration:86400}")
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username, String nombre, String documento, UserRole role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("nombre", nombre);
        claims.put("documento", documento);
        claims.put("rol", role.name());
        
        long nowSeconds = System.currentTimeMillis() / 1000;
        long expSeconds = nowSeconds + expiration;

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(expSeconds * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractNombre(String token) {
        return extractClaims(token).get("nombre", String.class);
    }

    public String extractDocumento(String token) {
        return extractClaims(token).get("documento", String.class);
    }

    public UserRole extractRole(String token) {
        String roleStr = extractClaims(token).get("rol", String.class);
        return UserRole.valueOf(roleStr);
    }

    public Long extractExpiration(String token) {
        return extractClaims(token).getExpiration().getTime() / 1000;
    }

    public Long extractIssuedAt(String token) {
        return extractClaims(token).getIssuedAt().getTime() / 1000;
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token) < (System.currentTimeMillis() / 1000);
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
