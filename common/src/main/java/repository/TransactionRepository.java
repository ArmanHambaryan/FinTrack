package repository;

import model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByUserId(Integer userId);

    List<Transaction> findByType(String type);

    @Query("""
            select coalesce(sum(t.amount), 0)
            from Transaction t
            where t.userId = :userId
              and t.type = 'EXPENSE'
              and t.transaction_date between :start and :end
            """)
    Double sumMonthlyExpense(@Param("userId") Integer userId,
                             @Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end);
}
