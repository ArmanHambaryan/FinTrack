package service.impl;

import model.RecurringTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.RecurringTransactionRepository;
import service.RecurringTransactionService;

import java.util.List;

@Service
public class RecurringTransactionServiceImpl implements RecurringTransactionService {

    @Autowired
    private RecurringTransactionRepository recurringRepo;

    @Override
    public RecurringTransaction save(RecurringTransaction rec) {
        return recurringRepo.save(rec);
    }

    @Override
    public List<RecurringTransaction> getByUser(Integer userId) {
        return recurringRepo.findByUserIdAndActiveTrue(userId);
    }

    @Override
    public void deactivate(Integer id) {
        RecurringTransaction rec = recurringRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        rec.setActive(false);
        recurringRepo.save(rec);
    }
}