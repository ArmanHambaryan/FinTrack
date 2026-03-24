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
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    private Double amount;
    private Double original_amount;
    private String currency_code;
    private Double exchange_rate;
    private String type;
    private LocalDateTime transaction_date;
    private String description;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    @jakarta.persistence.PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (transaction_date == null) {
            transaction_date = now;
        }
        if (currency_code == null || currency_code.isBlank()) {
            currency_code = "AMD";
        }
        if (original_amount == null) {
            original_amount = amount;
        }
        if (exchange_rate == null || exchange_rate <= 0) {
            exchange_rate = 1.0;
        }
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
