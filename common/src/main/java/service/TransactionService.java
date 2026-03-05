package service;

import model.Transaction;

import java.util.List;

public interface TransactionService {
    public List<Transaction> findAllByUserId(int userId);

    public List<Transaction> findByType(int userId);

    public void save(Transaction transaction);

    public void delete(Transaction transaction);

    public List<Transaction>getAllTransaction(Transaction transaction);


}
