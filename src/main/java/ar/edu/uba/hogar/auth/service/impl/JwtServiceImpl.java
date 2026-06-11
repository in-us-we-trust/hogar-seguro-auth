package ar.edu.uba.hogar.auth.service.impl;

import ar.edu.uba.hogar.auth.exception.DoorbellException;
import ar.edu.uba.hogar.auth.exception.ExceptionEnum;
import ar.edu.uba.hogar.auth.service.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

  private final SecretKey secretKey;
  private final long expirationMs;

  public JwtServiceImpl(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.expiration-ms}") long expirationMs) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMs = expirationMs;
  }

  @Override
  public String generateToken(Map<String, Object> payload) {
    return Jwts.builder()
        .claim("user", payload)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, Object> validateToken(String token) {
    try {
      Claims claims =
          Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();

      return claims.get("user", Map.class);

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
