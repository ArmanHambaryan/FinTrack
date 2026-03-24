package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;
    private String name;
    private String currency_code;
    private Double original_target_amount;
    private Double exchange_rate;
    private double target_amount;
    private double saved_amount;
    private LocalDate deadline;

    private String name;

    @Column(name = "target_amount")
    private double targetAmount;

    @Column(name = "saved_amount")
    private double savedAmount;

    private LocalDate deadline;

    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (status == null) {
            status = "ACTIVE";
        }
        if (currency_code == null || currency_code.isBlank()) {
            currency_code = "AMD";
        }
        if (original_target_amount == null) {
            original_target_amount = target_amount;
        }
        if (exchange_rate == null || exchange_rate <= 0) {
            exchange_rate = 1.0;
        }
        if (saved_amount == 0) {
            saved_amount = 0.0;
        if (savedAmount == 0) {
            savedAmount = 0.0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

