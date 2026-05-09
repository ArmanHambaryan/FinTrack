package com.example.rest.service.impl;

import model.Budget;
import model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.BudgetRepository;
import repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServiceImplTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BudgetServiceImpl service;

    @Captor
    private ArgumentCaptor<Budget> budgetCaptor;

    @Test
    void setBudgetCreatesBudgetForCurrentMonthWhenMissing() {
        User user = new User();
        user.setId(3);

        when(budgetRepository.findByUserIdAndMonthAndYear(
                3,
                java.time.LocalDate.now().getMonthValue(),
                java.time.LocalDate.now().getYear()
        )).thenReturn(Optional.empty());
        when(userRepository.findById(3)).thenReturn(Optional.of(user));

        service.setBudget(3, 1200.0);

        verify(budgetRepository).save(budgetCaptor.capture());
        Budget saved = budgetCaptor.getValue();
        assertEquals(1200.0, saved.getAmount());
        assertEquals(user, saved.getUser());
    }
}
