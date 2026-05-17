package ar.edu.uba.hogar.auth.service;

import ar.edu.uba.hogar.auth.model.dto.JwtPayload;

public interface JwtService {

    // Genera un JWT firmado con el secreto del application.yml
    String generateToken(JwtPayload payload);

    // Valida el token y devuelve el payload con los datos del usuario
    JwtPayload validateToken(String token);
}