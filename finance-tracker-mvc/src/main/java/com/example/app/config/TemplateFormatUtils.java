package com.example.app.config;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Component("templateFormatUtils")
public class TemplateFormatUtils {

    public String formatAmount(Number value) {
        if (value == null) {
            return "0";
        }

        BigDecimal decimal = BigDecimal.valueOf(value.doubleValue())
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros();

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.##", symbols);
        return decimalFormat.format(decimal);
    }
}
