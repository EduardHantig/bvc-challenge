package com.bvc.exchange.controller;

import com.bvc.exchange.exception.BadExchangeApiResponseException;
import com.bvc.exchange.exception.SymbolExchangeApiNotFoundException;
import com.bvc.exchange.model.CurrencyRate;
import com.bvc.exchange.service.ExchangeService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exchange")
public class ExchangeController implements ExchangeApi {

  private final ExchangeService exchangeService;

  public ExchangeController(ExchangeService exchangeService) {
    this.exchangeService = exchangeService;
  }

  @GetMapping("/exchange-rate")
  public ResponseEntity<Double> getRate(String base, String currency) throws BadExchangeApiResponseException,
          SymbolExchangeApiNotFoundException {
    return ResponseEntity.ok(exchangeService.getRateForBaseToSymbol(base, currency));
  }

  @GetMapping("/exchange-rates")
  public ResponseEntity<CurrencyRate> getAllExchangeRates(String base) throws BadExchangeApiResponseException {
    return ResponseEntity.ok(exchangeService.getRatesForBase(base));
  }

  @GetMapping("/exchange-conversion")
  public ResponseEntity<Double> convert(String base, String currency, Double amount)
          throws BadExchangeApiResponseException, SymbolExchangeApiNotFoundException {
    return ResponseEntity.ok(exchangeService.convertValue(base, currency, amount));
  }

  @GetMapping("/exchange-conversions")
  public ResponseEntity<Map<String, Double>> convertToMultipleCurrencies(String base, List<String> currencies,
                                                                         Double amount)
          throws BadExchangeApiResponseException, SymbolExchangeApiNotFoundException {
    return ResponseEntity.ok(exchangeService.convertToMultipleCurrencies(base, currencies, amount));
  }
}
