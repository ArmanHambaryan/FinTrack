package service.impl;


import lombok.RequiredArgsConstructor;
import model.Transaction;
import model.User;
import org.springframework.stereotype.Service;
import repository.TransactionRepository;
import repository.UserRepository;
import service.TransactionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

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
        applyBalanceChange(transaction, true);
        transactionRepository.save(transaction);
    }

    @Override
    public void addExpense(Transaction transaction) {
        transaction.setType("EXPENSE");
        transaction.setCreated_at(LocalDateTime.now());
        applyBalanceChange(transaction, false);
        transactionRepository.save(transaction);
    }

    @Override
    public Double getMonthlyExpense(Integer userId) {
        LocalDate now = LocalDate.now();
        LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);
        return transactionRepository.sumMonthlyExpense(userId, start, end);
    private void applyBalanceChange(Transaction transaction, boolean isIncome) {
        if (transaction.getUserId() == null) {
            return;
        }
        double amount = transaction.getAmount() == null ? 0.0 : transaction.getAmount();
        userRepository.findById(transaction.getUserId()).ifPresent(user -> {
            double current = user.getBalance();
            double next = isIncome ? current + amount : current - amount;
            user.setBalance(next);
            userRepository.save(user);
        });
    }
}
