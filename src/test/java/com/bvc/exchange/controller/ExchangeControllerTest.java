package com.bvc.exchange.controller;

import com.bvc.exchange.exception.BadExchangeApiResponseException;
import com.bvc.exchange.exception.SymbolExchangeApiNotFoundException;
import com.bvc.exchange.model.CurrencyRate;
import com.bvc.exchange.service.ExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeControllerTest {

  @InjectMocks
  private ExchangeController exchangeController;

  @Mock
  private ExchangeService exchangeService;

  private CurrencyRate mockCurrencyRate;

  @BeforeEach
  void setUp() {
    mockCurrencyRate = new CurrencyRate();
    Map<String, Double> quotes = new HashMap<>();
    quotes.put("USDEUR", 0.85);
    quotes.put("USDGBP", 0.75);
    mockCurrencyRate.setQuotes(quotes);
  }

  @Test
  void testGetRate_shouldReturnExpectedRate_whenServiceReturnsRate()
    throws BadExchangeApiResponseException, SymbolExchangeApiNotFoundException {
    // before
    when(exchangeService.getRateForBaseToSymbol(anyString(), anyString())).thenReturn(0.85);

    // when
    ResponseEntity<Double> response = exchangeController.getRate("USD", "EUR");

    // then
    assertEquals(0.85, response.getBody());
  }

  @Test
  void testGetAllExchangeRates_shouldReturnExpectedRates_whenServiceReturnsRates()
    throws BadExchangeApiResponseException {
    // before
    when(exchangeService.getRatesForBase(anyString())).thenReturn(mockCurrencyRate);

    // when
    ResponseEntity<CurrencyRate> response = exchangeController.getAllExchangeRates("USD");

    // then
    assertEquals(mockCurrencyRate, response.getBody());
  }

  @Test
  void testConvert_shouldReturnExpectedConversion_whenServiceReturnsConversion()
    throws BadExchangeApiResponseException, SymbolExchangeApiNotFoundException {
    // before
    when(exchangeService.convertValue(anyString(), anyString(), anyDouble())).thenReturn(85.0);

    // when
    ResponseEntity<Double> response = exchangeController.convert("USD", "EUR", 100.0);

    // then
    assertEquals(85.0, response.getBody());
  }

  @Test
  void testConvertToMultipleCurrencies_shouldReturnExpectedConversions_whenServiceReturnsConversions()
    throws BadExchangeApiResponseException, SymbolExchangeApiNotFoundException {
    // before
    Map<String, Double> conversions = new HashMap<>();
    conversions.put("EUR", 85.0);
    conversions.put("GBP", 75.0);
    when(exchangeService.convertToMultipleCurrencies(anyString(), anyList(), anyDouble())).thenReturn(conversions);

    // when
    ResponseEntity<Map<String, Double>> response = exchangeController.convertToMultipleCurrencies("USD", Arrays.asList("EUR", "GBP"), 100.0);

    // then
    assertEquals(conversions, response.getBody());
  }
}