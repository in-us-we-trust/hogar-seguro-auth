package ar.edu.uba.hogar.auth.repository;

import ar.edu.uba.hogar.auth.enums.UserStatusEnum;
import ar.edu.uba.hogar.auth.model.entity.AuthUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {

  boolean existsByEmail(String email);

  Optional<AuthUser> findByEmail(String email);

  @Query("SELECT u FROM AuthUser u WHERE u.email = :email AND u.status != :status")
  Optional<AuthUser> findByEmailAndStatusNot(
      @Param("email") String email, @Param("status") UserStatusEnum status);

  default Optional<AuthUser> findActiveUserByEmail(String email) {
    return findByEmailAndStatusNot(email, UserStatusEnum.INACTIVE);
  }
}
