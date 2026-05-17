package ar.edu.uba.hogar.auth.service.impl;

import ar.edu.uba.hogar.auth.exception.DoorbellException;
import ar.edu.uba.hogar.auth.exception.ExceptionEnum;
import ar.edu.uba.hogar.auth.model.dto.JwtPayload;
import ar.edu.uba.hogar.auth.service.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    // Inyecta los valores del application.yml
    public JwtServiceImpl(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        // Convierte el string del yml en una clave criptográfica HMAC-SHA256
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    @Override
    public String generateToken(JwtPayload payload) {
        return Jwts.builder()
                .setSubject(payload.getEmail())
                .claim("userId", payload.getUserId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public JwtPayload validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return JwtPayload.builder()
                    .email(claims.getSubject())
                    .userId(UUID.fromString(claims.get("userId", String.class)))
                    .build();

        } catch (ExpiredJwtException e) {
            log.warn("Token expirado: {}", e.getMessage());
            throw new DoorbellException(ExceptionEnum.TOKEN_EXPIRED);
        } catch (JwtException e) {
            log.warn("Token inválido: {}", e.getMessage());
            throw new DoorbellException(ExceptionEnum.TOKEN_INVALID);
        } catch (Exception e) {
            log.error("Error validando token: {}", e.getMessage());
            throw new DoorbellException(ExceptionEnum.TOKEN_ERROR);
        }
    }
}