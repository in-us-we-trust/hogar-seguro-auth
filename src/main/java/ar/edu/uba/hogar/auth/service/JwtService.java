package ar.edu.uba.hogar.auth.service;

import java.util.Map;

public interface JwtService {

  // Genera un JWT firmado con el secreto del application.yml
  String generateToken(Map<String, Object> payload);

  // Valida el token y devuelve el payload con los datos del usuario
  Map<String, Object> validateToken(String token);
}
