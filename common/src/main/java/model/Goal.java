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
import java.math.BigDecimal;
import java.math.RoundingMode;

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
    private int months;
    private String status;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (created_at == null) {
            created_at = now;
        }
        if (updated_at == null) {
            updated_at = now;
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
        }
    }

    @PreUpdate
    public void preUpdate() {
        updated_at = LocalDateTime.now();
    }

    public double getProgressPercent() {
        if (target_amount <= 0) {
            return 0.0;
        }

        double progress = (saved_amount * 100.0) / target_amount;
        if (progress < 0) {
            return 0.0;
        }
        if (progress > 100) {
            return 100.0;
        }

        return BigDecimal.valueOf(progress)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }
}

