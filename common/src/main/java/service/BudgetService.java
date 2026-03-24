package service;

public interface BudgetService {

    void setBudget(Integer userId, Double amount);

    Double getCurrentMonthBudget(Integer userId);
}
