package service;

import model.Transaction;

import java.io.IOException;
import java.util.List;

public interface TransactionService {
    List<Transaction> findAllByUserId(Integer userId);

    List<Transaction> findByType(String type);

    Transaction save(Transaction transaction);

    void deleteById(Integer id);

    List<Transaction> findAll();

    void addIncome(Transaction transaction);

    void addExpense(Transaction transaction);

    Double getMonthlyExpense(Integer userId);

    byte[] exportToExcel(Integer userId) throws IOException;

}
