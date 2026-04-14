package com.example.rest.service;

import com.example.rest.dto.BudgetRestDto;
import com.example.rest.dto.GoalRestDto;
import com.example.rest.dto.PasswordResetTokenRestDto;
import com.example.rest.dto.RecurringTransactionRestDto;
import com.example.rest.dto.TransactionRestDto;
import com.example.rest.dto.UserRestDto;
import model.Goal;
import model.PasswordResetToken;
import model.RecurringTransaction;
import model.Transaction;
import model.User;
import org.springframework.stereotype.Service;

@Service
public class RestDtoMapperService {

    public UserRestDto toUserDto(User user) {
        return new UserRestDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getBalance(),
                user.isBlocked(),
                user.getCreated_at(),
                user.getUpdated_at(),
                user.getLastActive()
        );
    }

    public TransactionRestDto toTransactionDto(Transaction transaction) {
        return new TransactionRestDto(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getAmount(),
                transaction.getOriginal_amount(),
                transaction.getCurrency_code(),
                transaction.getExchange_rate(),
                transaction.getType(),
                transaction.getCategoryId(),
                transaction.getTransaction_date(),
                transaction.getDescription(),
                transaction.getCreated_at(),
                transaction.getUpdated_at()
        );
    }

    public GoalRestDto toGoalDto(Goal goal) {
        return new GoalRestDto(
                goal.getId(),
                goal.getUserId(),
                goal.getName(),
                goal.getCurrency_code(),
                goal.getOriginal_target_amount(),
                goal.getExchange_rate(),
                goal.getTarget_amount(),
                goal.getSaved_amount(),
                goal.getProgressPercent(),
                goal.getDeadline(),
                goal.getStatus(),
                goal.getCreated_at(),
                goal.getUpdated_at()
        );
    }

    public RecurringTransactionRestDto toRecurringDto(RecurringTransaction recurringTransaction) {
        return new RecurringTransactionRestDto(
                recurringTransaction.getId(),
                recurringTransaction.getUserId(),
                recurringTransaction.getAmount(),
                recurringTransaction.getCurrency_code(),
                recurringTransaction.getExchange_rate(),
                recurringTransaction.getType(),
                recurringTransaction.getCategoryId(),
                recurringTransaction.getDescription(),
                recurringTransaction.getFrequency(),
                recurringTransaction.getStartDate(),
                recurringTransaction.getNextRunDate(),
                recurringTransaction.isActive(),
                recurringTransaction.getCreated_at()
        );
    }

    public BudgetRestDto toBudgetDto(Integer id, Integer userId, Double amount, int month, int year) {
        return new BudgetRestDto(id, userId, amount, month, year);
    }

    public PasswordResetTokenRestDto toPasswordResetTokenDto(PasswordResetToken token) {
        return new PasswordResetTokenRestDto(
                token.getId(),
                token.getToken(),
                token.getUser().getId(),
                token.getUser().getEmail(),
                token.getExpiryDate(),
                token.isExpired()
        );
    }
}
