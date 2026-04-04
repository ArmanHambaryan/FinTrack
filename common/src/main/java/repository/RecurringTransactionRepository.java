package repository;

import model.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecurringTransactionRepository
        extends JpaRepository<RecurringTransaction, Integer> {

    List<RecurringTransaction> findByUserIdAndActiveTrue(Integer userId);

    // Scheduler-ի համար՝ բոլոր այսօրվա կամ անցած run-ները
    List<RecurringTransaction> findByNextRunDateLessThanEqualAndActiveTrue(
            LocalDate date
    );
}
