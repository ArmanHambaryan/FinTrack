package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false)
    private String name;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    @jakarta.persistence.PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (created_at == null) {
            created_at = now;
        }
        if (updated_at == null) {
            updated_at = now;
        }
    }

    @jakarta.persistence.PreUpdate
    public void preUpdate() {
        updated_at = LocalDateTime.now();
    }
}
