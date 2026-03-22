package service.impl;


import lombok.RequiredArgsConstructor;
import model.Transaction;
import org.springframework.stereotype.Service;
import repository.TransactionRepository;
import service.TransactionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public List<Transaction> findAllByUserId(Integer userId) {
        return transactionRepository.findByUserId(userId);
    }

    @Override
    public List<Transaction> findByType(String type) {
        return transactionRepository.findByType(type);
    }

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public void deleteById(Integer id) {
        transactionRepository.deleteById(id);
    }

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public void addIncome(Transaction transaction) {
        transaction.setType("INCOME");
        transaction.setCreated_at(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    @Override
    public void addExpense(Transaction transaction) {
        transaction.setType("EXPENSE");
        transaction.setCreated_at(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    @Override
    public Double getMonthlyExpense(Integer userId) {
        LocalDate now = LocalDate.now();
        LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);
        return transactionRepository.sumMonthlyExpense(userId, start, end);
    }
}
