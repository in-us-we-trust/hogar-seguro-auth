package ar.edu.uba.hogar.auth.repository;

import ar.edu.uba.hogar.auth.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(UUID token);

    /// Elimina el refresh token de un usuario (para logout)
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.authUser.id = :userId")
    void deleteByAuthUserId(@Param("userId") java.util.UUID userId);

    /// Limpieza de tokens expirados
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}