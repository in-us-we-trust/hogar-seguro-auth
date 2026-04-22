package ar.edu.uba.hogar.auth.repository;
import ar.edu.uba.hogar.auth.model.entity.PasswordReset;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {

    Optional<PasswordReset> findByToken(UUID token);

    @Modifying
    @Query("DELETE FROM PasswordReset pr WHERE pr.createdAt < :expirationTime")
    int deleteExpiredTokens(@Param("expirationTime") LocalDateTime expirationTime);
}