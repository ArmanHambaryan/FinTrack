package model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recurring_transactions")
public class RecurringTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    private Double amount;
    private String currency_code;  // քո Transaction-ի նման
    private Double exchange_rate;
    private String type;            // "INCOME" or "EXPENSE"
    private Integer categoryId;
    private String description;

    @Enumerated(EnumType.STRING)
    private FrequencyType frequency; // WEEKLY, MONTHLY, YEARLY

    private LocalDate startDate;
    private LocalDate nextRunDate;
    private boolean active;

    private LocalDateTime created_at;

    @PrePersist
    public void prePersist() {
        if (currency_code == null || currency_code.isBlank()) {
            currency_code = "AMD";
        }
        if (exchange_rate == null || exchange_rate <= 0) {
            exchange_rate = 1.0;
        }
        if (nextRunDate == null) {
            nextRunDate = startDate;
        }
        active = true;
        created_at = LocalDateTime.now();
    }
}