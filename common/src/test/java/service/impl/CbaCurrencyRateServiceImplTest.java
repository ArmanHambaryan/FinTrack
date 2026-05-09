package service.impl;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CbaCurrencyRateServiceImplTest {

    private final CbaCurrencyRateServiceImpl service = new CbaCurrencyRateServiceImpl();

    @Test
    void getRateToAmdReturnsOneForAmdWithoutRemoteCall() {
        assertEquals(BigDecimal.ONE, service.getRateToAmd(" amd ", LocalDate.now()));
    }
}
