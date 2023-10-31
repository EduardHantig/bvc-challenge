package com.bvc.exchange.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.bvc.exchange.exception.BadExchangeApiResponseException;
import com.bvc.exchange.exception.SymbolExchangeApiNotFoundException;
import com.bvc.exchange.model.CurrencyRate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class ExchangeServiceTest {
  private static final Logger logger = (Logger) LoggerFactory.getLogger(ExchangeService.class);

  @InjectMocks
  private ExchangeService exchangeService;

  @Mock
  private RestTemplate restTemplate;

  @Test
  public void testGetRatesForBase_shouldReturnExpectedRates_whenApiReturnsRates()
          throws BadExchangeApiResponseException {
    // before
    String base = "USD";
    CurrencyRate mockRate = createMockCurrencyRate();

    when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
        .thenReturn(mockRate);

    // when
    CurrencyRate result = exchangeService.getRatesForBase(base);

    // then
    assertEquals(mockRate, result);
  }

  @Test
  public void testGetRatesForBase_shouldThrowBadExchangeApiResponseException_whenApiResponseIsNull() {
    // before
    ListAppender<ILoggingEvent> logAppender = initLogAppender();
    when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
            .thenReturn(null);

    // when
    Exception acxtualException = assertThrows(BadExchangeApiResponseException.class,
            () -> exchangeService.getRatesForBase("USD"));

    // then
    String expectedMessage = "Unexpected API response! Either you asked for non-existent base currency, either bad " +
            "response received from Exchange API!";
    assertTrue(acxtualException.getMessage().contains(expectedMessage));
    assertEquals(1, logAppender.list.size());
    assertEquals(Level.ERROR, logAppender.list.get(0).getLevel());
    assertTrue(logAppender.list.get(0).getFormattedMessage()
            .contains("Unexpected payload received. API response is: null"));

    // after
    logger.detachAppender(logAppender);
  }

  @Test
  public void testGetRatesForBase_shouldThrowBadExchangeApiResponseException_whenApiQuotesIsNull() {
    // before
    ListAppender<ILoggingEvent> logAppender = initLogAppender();
    CurrencyRate mockRate = new CurrencyRate();
    mockRate.setQuotes(null);

    when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
            .thenReturn(mockRate);

    // when
    Exception acxtualException = assertThrows(BadExchangeApiResponseException.class,
            () -> exchangeService.getRatesForBase("USD"));

    // then
    String expectedMessage = "Unexpected API response! Either you asked for non-existent base currency, either bad " +
            "response received from Exchange API!";
    assertTrue(acxtualException.getMessage().contains(expectedMessage));
    assertEquals(1, logAppender.list.size());
    assertEquals(Level.ERROR, logAppender.list.get(0).getLevel());
    assertTrue(logAppender.list.get(0).getFormattedMessage()
            .contains("Unexpected payload received. API response is: CurrencyRate{source='null', quotes=null}."));

    // after
    logger.detachAppender(logAppender);
  }

  @Test
  public void testGetRatesForBase_shouldThrowBadExchangeApiResponseException_whenApiQuotesIsEmpty() {
    // before
    ListAppender<ILoggingEvent> logAppender = initLogAppender();
    CurrencyRate mockRate = new CurrencyRate();
    mockRate.setQuotes(new HashMap<>());

    when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
            .thenReturn(mockRate);

    // when
    Exception acxtualException = assertThrows(BadExchangeApiResponseException.class,
            () -> exchangeService.getRatesForBase("USD"));

    // then
    String expectedMessage = "Unexpected API response! Either you asked for non-existent base currency, either bad " +
            "response received from Exchange API!";
    assertTrue(acxtualException.getMessage().contains(expectedMessage));
    assertEquals(1, logAppender.list.size());
    assertEquals(Level.ERROR, logAppender.list.get(0).getLevel());
    assertTrue(logAppender.list.get(0).getFormattedMessage()
            .contains("Unexpected payload received. API response is: CurrencyRate{source='null', quotes={}}."));

    // after
    logger.detachAppender(logAppender);
  }

  @Test
  public void testGetRateForBaseToSymbol_shouldReturnExpectedRate_whenApiReturnsRates()
          throws BadExchangeApiResponseException, SymbolExchangeApiNotFoundException {
    // before
    String base = "USD";
    String symbol = "EUR";
    CurrencyRate mockRate = createMockCurrencyRate();

    when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
        .thenReturn(mockRate);

    // when
    Double result = exchangeService.getRateForBaseToSymbol(base, symbol);

    // then
    Double expectedRate = mockRate.getQuotes().get(base + symbol);
    assertEquals(expectedRate, result);
  }

  @Test
  public void testGetRateForBaseToSymbol_shouldThrowSymbolExchangeApiNotFoundException_whenSymbolInApiQuotesNotFound() {
    // before
    ListAppender<ILoggingEvent> logAppender = initLogAppender();
    String base = "USD";
    String symbol = "JPY";
    CurrencyRate mockRate = createMockCurrencyRate();

    when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
            .thenReturn(mockRate);

    // when
    Exception acxtualException = assertThrows(SymbolExchangeApiNotFoundException.class,
            () -> exchangeService.getRateForBaseToSymbol(base, symbol));

    // then
    String expectedMessage = "Provided currency symbol " + symbol + " is not found in received quotes from Exchange API!";
    assertTrue(acxtualException.getMessage().contains(expectedMessage));
    assertEquals(1, logAppender.list.size());
    assertEquals(Level.ERROR, logAppender.list.get(0).getLevel());
    assertTrue(logAppender.list.get(0).getFormattedMessage()
            .contains("Bad request! Provided symbol " + symbol + " is not found from received quotes " +
                    mockRate.getQuotes() + "."));

    // after
    logger.detachAppender(logAppender);
  }

  @Test
  public void testConvertValue_shouldReturnConvertedValue_whenApiReturnsRates() throws BadExchangeApiResponseException,
          SymbolExchangeApiNotFoundException {
    // before
    String base = "USD";
    String symbol = "EUR";
    Double amount = 100.0;
    CurrencyRate mockRate = createMockCurrencyRate();

    when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
        .thenReturn(mockRate);

    // when
    Double result = exchangeService.convertValue(base, symbol, amount);

    // then
    Double expectedConversion = mockRate.getQuotes().get(base + symbol) * amount;
    assertEquals(expectedConversion, result);
  }

  @Test
  public void testConvertToMultipleCurrencies_shouldReturnConvertedCurrencies_whenApiReturnsRates() throws
          BadExchangeApiResponseException, SymbolExchangeApiNotFoundException {
    // before
    String base = "USD";
    Double amount = 100.0;
    CurrencyRate mockRate = createMockCurrencyRate();

    when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
        .thenReturn(mockRate);

    // when
    Map<String, Double> result = exchangeService.convertToMultipleCurrencies(base, Arrays.asList("EUR", "GBP"), amount);

    // then
    Map<String, Double> expectedConversion = new HashMap<>();
    for (String symbol : Arrays.asList("EUR", "GBP")) {
      expectedConversion.put(symbol, mockRate.getQuotes().get(base + symbol) * amount);
    }
    assertEquals(expectedConversion, result);
  }

  @Test
  public void testConvertToMultipleCurrencies_shouldThrowSymbolExchangeApiNotFoundException_whenSymbolInApiQuotesNotFound() {
    // before
    ListAppender<ILoggingEvent> logAppender = initLogAppender();
    String base = "USD";
    Double amount = 100.0;
    CurrencyRate mockRate = createMockCurrencyRate();

    when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
            .thenReturn(mockRate);

    // when
    Exception acxtualException = assertThrows(SymbolExchangeApiNotFoundException.class,
            () -> exchangeService.convertToMultipleCurrencies(base, Arrays.asList("EUR", "JPY"), amount));

    // then
    String expectedMessage = "Provided currency symbol JPY is not found in received quotes from Exchange API!";
    assertTrue(acxtualException.getMessage().contains(expectedMessage));
    assertEquals(1, logAppender.list.size());
    assertEquals(Level.ERROR, logAppender.list.get(0).getLevel());
    assertTrue(logAppender.list.get(0).getFormattedMessage()
            .contains("Bad request! Provided symbol JPY is not found from received quotes " + mockRate.getQuotes() + "."));

    // after
    logger.detachAppender(logAppender);
  }

  private CurrencyRate createMockCurrencyRate() {
    CurrencyRate currencyRate = new CurrencyRate();
    Map<String, Double> quotes = new HashMap<>();
    quotes.put("USDEUR", 0.85);
    quotes.put("USDGBP", 0.75);
    currencyRate.setQuotes(quotes);
    return currencyRate;
  }

  private ListAppender<ILoggingEvent> initLogAppender() {
    ListAppender<ILoggingEvent> logAppender = new ListAppender<>();
    logAppender.start();
    logger.addAppender(logAppender);
    return logAppender;
  }
}