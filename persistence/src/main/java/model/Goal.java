package model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @Table(name = "goals")
    public class Goal {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        private int user_id;
        private String name;
        private double target_amount;
        private double saved_amount;
        private LocalDate deadline;
        private String status;
        private LocalDateTime created_at;
        private LocalDateTime updated_at;

    }

