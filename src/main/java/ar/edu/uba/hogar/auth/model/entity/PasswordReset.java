package ar.edu.uba.hogar.auth.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "password_reset")
@Data
public class PasswordReset {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "auth_user_id", referencedColumnName = "id", unique = true)
  private AuthUser authUser;

  // Token único que se manda por email para resetear la contraseña
  @Column(nullable = false, unique = true)
  private UUID token;

  private LocalDateTime createdAt = LocalDateTime.now();
}
