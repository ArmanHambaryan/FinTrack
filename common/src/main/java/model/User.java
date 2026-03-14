package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private double balance;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    @Column(name = "last_active")
    private LocalDateTime lastActive;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (created_at == null) {
            created_at = now;
        }
        if (updated_at == null) {
            updated_at = now;
        }
        if (lastActive == null) {
            lastActive = now;
        }
        if (role == null) {
            role = UserRole.USER;
        }

    }
    private boolean is_blocked;
    private LocalDateTime blocked_until;
    private int login_attempts;

    @PreUpdate
    public void preUpdate() {
        updated_at = LocalDateTime.now();
    }
}
