package repository;

import model.Transaction;
import model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    @Query("SELECT t.categoryId, SUM(t.amount) " +
            "FROM Transaction t " +
            "WHERE t.userId = :userId AND t.type = 'EXPENSE' " +
            "GROUP BY t.categoryId")
    List<Object[]> getExpensesByCategory(@Param("userId") Integer userId);

    @Query("SELECT MONTH(t.transaction_date), " +
            "SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), " +
            "SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) " +
            "FROM Transaction t " +
            "WHERE t.userId = :userId " +
            "GROUP BY MONTH(t.transaction_date) " +
            "ORDER BY MONTH(t.transaction_date)")
    List<Object[]> getMonthlyStats(@Param("userId") Integer userId);

}
