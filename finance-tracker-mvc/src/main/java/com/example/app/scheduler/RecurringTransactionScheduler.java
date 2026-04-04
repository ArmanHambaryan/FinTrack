package com.example.app.scheduler;

import model.RecurringTransaction;
import model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import repository.RecurringTransactionRepository;
import repository.TransactionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class RecurringTransactionScheduler {

    @Autowired
    private RecurringTransactionRepository recurringRepo;

    @Autowired
    private TransactionRepository transactionRepo;

    // Ամեն օր կեսգիշերին կատարվում է
    @Scheduled(cron = "0 0 0 * * *")
    public void processRecurring() {
        LocalDate today = LocalDate.now();
        List<RecurringTransaction> due =
                recurringRepo.findByNextRunDateLessThanEqualAndActiveTrue(today);

        for (RecurringTransaction rec : due) {
            Transaction tx = new Transaction();
            tx.setUserId(rec.getUserId());
            tx.setType(rec.getType());
            tx.setAmount(rec.getAmount());
            tx.setOriginal_amount(rec.getAmount());      // ← ավելացրու
            tx.setCurrency_code(rec.getCurrency_code()); // ← ուղղիր
            tx.setExchange_rate(rec.getExchange_rate()); // ← ավելացրու
            tx.setCategoryId(rec.getCategoryId());       // ← ավելացրու
            tx.setDescription(rec.getDescription());
            tx.setTransaction_date(LocalDateTime.now()); // ← ուղղիր

            transactionRepo.save(tx);

            rec.setNextRunDate(getNextDate(rec));
            recurringRepo.save(rec);
        }
    }

    private LocalDate getNextDate(RecurringTransaction rec) {
        return switch (rec.getFrequency()) {
            case WEEKLY  -> rec.getNextRunDate().plusWeeks(1);
            case MONTHLY -> rec.getNextRunDate().plusMonths(1);
            case YEARLY  -> rec.getNextRunDate().plusYears(1);
        };
    }
}