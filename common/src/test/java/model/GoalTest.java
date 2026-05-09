package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GoalTest {

    @Test
    void getProgressPercentRoundsToSingleDecimal() {
        Goal goal = new Goal();
        goal.setTarget_amount(300.0);
        goal.setSaved_amount(100.0);

        assertEquals(33.3, goal.getProgressPercent());
    }

    @Test
    void getProgressPercentCapsAtHundred() {
        Goal goal = new Goal();
        goal.setTarget_amount(100.0);
        goal.setSaved_amount(150.0);

        assertEquals(100.0, goal.getProgressPercent());
    }

    @Test
    void getProgressPercentReturnsZeroForInvalidTarget() {
        Goal goal = new Goal();
        goal.setTarget_amount(0.0);
        goal.setSaved_amount(50.0);

        assertEquals(0.0, goal.getProgressPercent());
    }
}
