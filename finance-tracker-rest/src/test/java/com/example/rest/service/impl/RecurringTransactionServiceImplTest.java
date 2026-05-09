package com.example.rest.service.impl;

import model.RecurringTransaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import repository.RecurringTransactionRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecurringTransactionServiceImplTest {

    @Mock
    private RecurringTransactionRepository recurringRepo;

    @InjectMocks
    private RecurringTransactionServiceImpl service;

    @Captor
    private ArgumentCaptor<RecurringTransaction> recurringCaptor;

    @Test
    void deactivateMarksTransactionInactive() {
        ReflectionTestUtils.setField(service, "recurringRepo", recurringRepo);

        RecurringTransaction transaction = new RecurringTransaction();
        transaction.setId(8);
        transaction.setActive(true);

        when(recurringRepo.findById(8)).thenReturn(Optional.of(transaction));

        service.deactivate(8);

        verify(recurringRepo).save(recurringCaptor.capture());
        assertFalse(recurringCaptor.getValue().isActive());
    }
}
