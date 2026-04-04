package service;

import model.RecurringTransaction;
import java.util.List;

public interface RecurringTransactionService {
    RecurringTransaction save(RecurringTransaction rec);
    List<RecurringTransaction> getByUser(Integer userId);
    void deactivate(Integer id);
}