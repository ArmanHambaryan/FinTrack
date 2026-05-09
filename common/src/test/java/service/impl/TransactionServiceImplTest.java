package service.impl;

import model.Transaction;
import model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.TransactionRepository;
import repository.UserRepository;
import service.CurrencyRateService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrencyRateService currencyRateService;

    @InjectMocks
    private TransactionServiceImpl service;

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void saveNormalizesCurrencyAndAppliesIncomeToBalance() {
        Transaction transaction = new Transaction();
        transaction.setUserId(5);
        transaction.setType("income");
        transaction.setAmount(10.0);
        transaction.setCurrency_code(" usd ");

        User user = new User();
        user.setId(5);
        user.setBalance(100.0);

        when(currencyRateService.getRateToAmd("USD", java.time.LocalDate.now())).thenReturn(BigDecimal.valueOf(400));
        when(userRepository.findById(5)).thenReturn(Optional.of(user));
        when(transactionRepository.save(org.mockito.ArgumentMatchers.any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction saved = service.save(transaction);

        verify(userRepository).save(userCaptor.capture());
        assertEquals(4100.0, userCaptor.getValue().getBalance());
        assertEquals("USD", saved.getCurrency_code());
        assertEquals(4000.0, saved.getAmount());
    }

    @Test
    void addExpenseRequiresCategory() {
        Transaction transaction = new Transaction();
        transaction.setAmount(10.0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.addExpense(transaction)
        );

        assertEquals("Category is required for expenses.", exception.getMessage());
    }

    @Test
    void exportToExcelReturnsWorkbookBytes() throws IOException {
        when(transactionRepository.findByUserId(9)).thenReturn(java.util.List.of());

        byte[] bytes = service.exportToExcel(9);

        assertFalse(bytes.length == 0);
        verify(transactionRepository).findByUserId(9);
    }
}
