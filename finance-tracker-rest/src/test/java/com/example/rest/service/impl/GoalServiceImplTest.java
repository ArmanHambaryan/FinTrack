package com.example.rest.service.impl;

import com.example.rest.dto.GoalRestDto;
import model.Goal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.GoalRepository;
import repository.UserRepository;
import com.example.rest.service.CurrencyRateService;
import com.example.rest.service.RestDtoMapperService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrencyRateService currencyRateService;

    @Mock
    private RestDtoMapperService restDtoMapperService;

    @InjectMocks
    private GoalServiceImpl service;

    @Test
    void getAllGoalDtosMapsRepositoryResults() {
        Goal goal = new Goal();
        goal.setId(1);
        GoalRestDto dto = new GoalRestDto(1, 7, "Trip", "AMD", 1000.0, 1.0, 1000.0, 200.0, 20.0, null, "ACTIVE", null, null);

        when(goalRepository.findAll()).thenReturn(List.of(goal));
        when(restDtoMapperService.toGoalDto(goal)).thenReturn(dto);

        List<GoalRestDto> result = service.getAllGoalDtos();

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void updateProgressAndReturnDtoMapsUpdatedGoal() {
        Goal goal = new Goal();
        goal.setId(1);
        goal.setUserId(3);
        goal.setCurrency_code("AMD");
        goal.setTarget_amount(300.0);
        goal.setSaved_amount(100.0);

        model.User user = new model.User();
        user.setId(3);
        user.setBalance(500.0);

        GoalRestDto dto = new GoalRestDto(1, 3, "Goal", "AMD", 300.0, 1.0, 300.0, 150.0, 50.0, null, "ACTIVE", null, null);

        when(goalRepository.findById(1)).thenReturn(Optional.of(goal));
        when(userRepository.findById(3)).thenReturn(Optional.of(user));
        when(currencyRateService.getRateToAmd("AMD", java.time.LocalDate.now())).thenReturn(java.math.BigDecimal.ONE);
        when(restDtoMapperService.toGoalDto(goal)).thenReturn(dto);

        GoalRestDto result = service.updateProgressAndReturnDto(1, 50.0);

        assertEquals(dto, result);
        assertEquals(150.0, goal.getSaved_amount());
        assertEquals(450.0, user.getBalance());
    }
}
