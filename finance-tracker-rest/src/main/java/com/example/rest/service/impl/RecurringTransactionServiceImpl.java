package com.example.rest.service.impl;

import model.RecurringTransaction;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.RecurringTransactionRepository;
import com.example.rest.service.RecurringTransactionService;

import java.util.List;

@Service
@CacheConfig(cacheNames = "recurringTransactions")
public class RecurringTransactionServiceImpl implements RecurringTransactionService {

    @Autowired
    private RecurringTransactionRepository recurringRepo;

    @Override
    @CacheEvict(allEntries = true)
    public RecurringTransaction save(RecurringTransaction rec) {
        return recurringRepo.save(rec);
    }

    @Override
    @Cacheable(key = "#userId")
    public List<RecurringTransaction> getByUser(Integer userId) {
        return recurringRepo.findByUserIdAndActiveTrue(userId);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void deactivate(Integer id) {
        RecurringTransaction rec = recurringRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        rec.setActive(false);
        recurringRepo.save(rec);
    }
}
