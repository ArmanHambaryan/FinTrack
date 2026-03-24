package service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import service.CurrencyRateService;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

@Service
public class CbaCurrencyRateServiceImpl implements CurrencyRateService {

    private static final String AMD = "AMD";
    private static final String SERVICE_URL = "https://api.cba.am/exchangerates.asmx";
    private static final DateTimeFormatter CBA_DATE_FORMAT = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral("T00:00:00")
            .toFormatter(Locale.ROOT);

    private final RestClient restClient = RestClient.builder().baseUrl(SERVICE_URL).build();

    @Override
    public BigDecimal getRateToAmd(String currencyCode, LocalDate rateDate) {
        String normalizedCode = normalizeCurrency(currencyCode);
        if (AMD.equals(normalizedCode)) {
            return BigDecimal.ONE;
        }

        String soapAction;
        String requestBody;
        if (rateDate == null) {
            soapAction = "http://www.cba.am/ExchangeRatesLatestByISO";
            requestBody = latestRequest(normalizedCode);
        } else {
            soapAction = "http://www.cba.am/ExchangeRatesByDateByISO";
            requestBody = byDateRequest(normalizedCode, rateDate);
        }

        String response = restClient.post()
                .header("Content-Type", "text/xml; charset=utf-8")
                .header("SOAPAction", "\"" + soapAction + "\"")
                .body(requestBody)
                .retrieve()
                .body(String.class);

        return extractRate(response);
    }

    private BigDecimal extractRate(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));

            NodeList rateNodes = document.getElementsByTagName("Rate");
            NodeList amountNodes = document.getElementsByTagName("Amount");
            if (rateNodes.getLength() == 0 || amountNodes.getLength() == 0) {
                throw new IllegalStateException("CBA response did not contain rate data");
            }

            BigDecimal rate = new BigDecimal(rateNodes.item(0).getTextContent().trim());
            BigDecimal amount = new BigDecimal(amountNodes.item(0).getTextContent().trim());
            if (amount.compareTo(BigDecimal.ZERO) == 0) {
                throw new IllegalStateException("CBA response contained zero amount");
            }

            return rate.divide(amount, 6, RoundingMode.HALF_UP);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to read currency rate from CBA", ex);
        }
    }

    private String latestRequest(String currencyCode) {
        return """
                <?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                               xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                               xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <ExchangeRatesLatestByISO xmlns="http://www.cba.am/">
                      <ISO>%s</ISO>
                    </ExchangeRatesLatestByISO>
                  </soap:Body>
                </soap:Envelope>
                """.formatted(currencyCode);
    }

    private String byDateRequest(String currencyCode, LocalDate rateDate) {
        return """
                <?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                               xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                               xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <ExchangeRatesByDateByISO xmlns="http://www.cba.am/">
                      <date>%s</date>
                      <ISO>%s</ISO>
                    </ExchangeRatesByDateByISO>
                  </soap:Body>
                </soap:Envelope>
                """.formatted(rateDate.format(CBA_DATE_FORMAT), currencyCode);
    }

    private String normalizeCurrency(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            return AMD;
        }
        return currencyCode.trim().toUpperCase(Locale.ROOT);
    }
}
