package ar.edu.uba.hogar.auth.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "auth_user_id", referencedColumnName = "id", unique = true)
    private AuthUser authUser;

    @Column(nullable = false, unique = true)
    private UUID token;

    // Los refresh tokens duran más que el JWT
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime createdAt = LocalDateTime.now();
}