package ar.edu.uba.hogar.auth.model.entity;

import ar.edu.uba.hogar.auth.enums.UserStatusEnum;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "auth_users")
@Data
@ToString(exclude = "passwordReset")
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = true)
    private String firstName;

    @Column(nullable = true)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatusEnum status = UserStatusEnum.ACTIVE;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime lastLogin;

    @OneToOne(
            mappedBy = "authUser",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    private PasswordReset passwordReset;
}