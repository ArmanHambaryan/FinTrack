package service.impl;


import lombok.RequiredArgsConstructor;
import model.Transaction;
import org.springframework.stereotype.Service;
import repository.TransactionRepository;
import repository.UserRepository;
import service.CurrencyRateService;
import service.TransactionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CurrencyRateService currencyRateService;

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
        enrichTransactionCurrency(transaction);
        if ("INCOME".equalsIgnoreCase(transaction.getType())) {
            applyBalanceChange(transaction, true);
        } else if ("EXPENSE".equalsIgnoreCase(transaction.getType())) {
            applyBalanceChange(transaction, false);
        }
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
        enrichTransactionCurrency(transaction);
        applyBalanceChange(transaction, true);
        transactionRepository.save(transaction);
    }

    @Override
    public void addExpense(Transaction transaction) {
        transaction.setType("EXPENSE");
        transaction.setCreated_at(LocalDateTime.now());
        enrichTransactionCurrency(transaction);
        applyBalanceChange(transaction, false);
        transactionRepository.save(transaction);
    }

    @Override
    public Double getMonthlyExpense(Integer userId) {
        LocalDate now = LocalDate.now();
        LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);
        return transactionRepository.sumMonthlyExpense(userId, start, end);

    }

    private void applyBalanceChange(Transaction transaction, boolean isIncome) {
    private void applyBalanceChange (Transaction transaction,boolean isIncome){
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

    private void enrichTransactionCurrency(Transaction transaction) {
        String currencyCode = normalizeCurrency(transaction.getCurrency_code());
        BigDecimal originalAmount = BigDecimal.valueOf(transaction.getAmount() == null ? 0.0 : transaction.getAmount());
        if (transaction.getOriginal_amount() != null && transaction.getOriginal_amount() > 0) {
            originalAmount = BigDecimal.valueOf(transaction.getOriginal_amount());
        }

        LocalDate rateDate = transaction.getTransaction_date() == null
                ? LocalDate.now()
                : transaction.getTransaction_date().toLocalDate();
        BigDecimal rate = currencyRateService.getRateToAmd(currencyCode, rateDate);
        BigDecimal convertedAmount = originalAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);

        transaction.setCurrency_code(currencyCode);
        transaction.setOriginal_amount(originalAmount.doubleValue());
        transaction.setExchange_rate(rate.doubleValue());
        transaction.setAmount(convertedAmount.doubleValue());
    }

    private String normalizeCurrency(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            return "AMD";
        }
        return currencyCode.trim().toUpperCase(Locale.ROOT);
    }
}
}
