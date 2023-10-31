package com.bvc.exchange.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class ExchangeServiceTest {

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
    when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
            .thenReturn(null);

    // then
    assertThrows(BadExchangeApiResponseException.class, () -> exchangeService.getRatesForBase("USD"));
  }

  @Test
  public void testGetRatesForBase_shouldThrowBadExchangeApiResponseException_whenApiQuotesIsNull() {
    // before
    CurrencyRate mockRate = new CurrencyRate();
    mockRate.setQuotes(null);

    when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
            .thenReturn(mockRate);

    // then
    assertThrows(BadExchangeApiResponseException.class, () -> exchangeService.getRatesForBase("USD"));
  }

  @Test
  public void testGetRatesForBase_shouldThrowBadExchangeApiResponseException_whenApiQuotesIsEmpty() {
    // before
    CurrencyRate mockRate = new CurrencyRate();
    mockRate.setQuotes(new HashMap<>());

    when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
            .thenReturn(mockRate);

    // then
    assertThrows(BadExchangeApiResponseException.class, () -> exchangeService.getRatesForBase("USD"));
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
    String base = "USD";
    String symbol = "JPY";
    CurrencyRate mockRate = createMockCurrencyRate();

    when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
            .thenReturn(mockRate);

    // then
    assertThrows(SymbolExchangeApiNotFoundException.class, () -> exchangeService.getRateForBaseToSymbol(base, symbol));
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
    String base = "USD";
    Double amount = 100.0;
    CurrencyRate mockRate = createMockCurrencyRate();

    when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
            .thenReturn(mockRate);

    // then
    assertThrows(SymbolExchangeApiNotFoundException.class,
            () -> exchangeService.convertToMultipleCurrencies(base, Arrays.asList("EUR", "JPY"), amount));
  }

  private CurrencyRate createMockCurrencyRate() {
    CurrencyRate currencyRate = new CurrencyRate();
    Map<String, Double> quotes = new HashMap<>();
    quotes.put("USDEUR", 0.85);
    quotes.put("USDGBP", 0.75);
    currencyRate.setQuotes(quotes);
    return currencyRate;
  }
}