package repository;

import model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    Optional<Budget> findByUserIdAndMonthAndYear(Integer userId, int month, int year);
}