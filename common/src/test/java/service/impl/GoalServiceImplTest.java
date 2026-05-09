package service.impl;

import model.Goal;
import model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.GoalRepository;
import repository.UserRepository;
import service.CurrencyRateService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrencyRateService currencyRateService;

    @InjectMocks
    private GoalServiceImpl goalService;

    @Captor
    private ArgumentCaptor<Goal> goalCaptor;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void createGoalNormalizesCurrencyAndCalculatesAmdAmount() {
        Goal goal = new Goal();
        goal.setCurrency_code(" usd ");
        goal.setOriginal_target_amount(10.0);
        goal.setTarget_amount(10.0);

        when(currencyRateService.getRateToAmd("USD", LocalDate.now())).thenReturn(BigDecimal.valueOf(400));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Goal saved = goalService.createGoal(goal);

        assertEquals("USD", saved.getCurrency_code());
        assertEquals(10.0, saved.getOriginal_target_amount());
        assertEquals(400.0, saved.getExchange_rate());
        assertEquals(4000.0, saved.getTarget_amount());
    }

    @Test
    void updateProgressCompletesGoalAndDeductsUserBalance() {
        Goal goal = new Goal();
        goal.setId(1);
        goal.setUserId(7);
        goal.setCurrency_code("USD");
        goal.setTarget_amount(400.0);
        goal.setSaved_amount(200.0);
        goal.setStatus("ACTIVE");

        User user = new User();
        user.setId(7);
        user.setBalance(1000.0);

        when(goalRepository.findById(1)).thenReturn(Optional.of(goal));
        when(userRepository.findById(7)).thenReturn(Optional.of(user));
        when(currencyRateService.getRateToAmd("USD", LocalDate.now())).thenReturn(BigDecimal.valueOf(200));

        goalService.updateProgress(1, 1.0);

        verify(userRepository).save(userCaptor.capture());
        verify(goalRepository).save(goalCaptor.capture());

        User savedUser = userCaptor.getValue();
        Goal savedGoal = goalCaptor.getValue();

        assertEquals(800.0, savedUser.getBalance());
        assertEquals(400.0, savedGoal.getSaved_amount());
        assertEquals("COMPLETED", savedGoal.getStatus());
    }

    @Test
    void updateProgressRejectsAmountThatExceedsRemainingBalance() {
        Goal goal = new Goal();
        goal.setId(1);
        goal.setUserId(7);
        goal.setCurrency_code("USD");
        goal.setTarget_amount(400.0);
        goal.setSaved_amount(350.0);

        when(goalRepository.findById(1)).thenReturn(Optional.of(goal));
        when(currencyRateService.getRateToAmd("USD", LocalDate.now())).thenReturn(BigDecimal.valueOf(100));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> goalService.updateProgress(1, 1.0)
        );

        assertEquals("Amount exceeds the remaining goal balance.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(goalRepository, never()).save(any(Goal.class));
    }
}
