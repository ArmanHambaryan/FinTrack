package service.impl;

import model.Transaction;
import repository.TransactionRepository;
import service.TransactionService;

import java.util.List;

public class TransactionServiceImpl implements TransactionService {
    private TransactionRepository transactionRepository;

    @Override
    public List<Transaction> findAllByUserId(int Id) {
       return transactionRepository.findByUserId(Id);

    }

    @Override
    public List<Transaction> findByType(int userId) {
       return  transactionRepository.findByType("");
    }

    @Override
    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Override
    public void delete(Transaction transaction) {
        transactionRepository.delete(transaction);

    }

    @Override
    public List<Transaction> getAllTransaction(Transaction transaction) {
        return transactionRepository.findAll();
    }
}
