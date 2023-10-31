package com.bvc.exchange.controller;

import com.bvc.exchange.model.CurrencyRate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Exchange Operations", description = "APIs related to currency exchange operations")
public interface ExchangeApi {

  @GetMapping("/exchange-rate")
  @Operation(summary = "Get the exchange rate from a base currency to another currency")
  ResponseEntity<Double> getRate(
      @Parameter(description = "Base currency code", required = true) @RequestParam String base,
      @Parameter(description = "Target currency code", required = true) @RequestParam String currency);

  @GetMapping("/exchange-rates")
  @Operation(summary = "Get all exchange rates for a base currency")
  ResponseEntity<CurrencyRate> getAllExchangeRates(
      @Parameter(description = "Base currency code", required = true) @RequestParam String base);

  @GetMapping("/exchange-conversion")
  @Operation(summary = "Convert a given amount from a base currency to another currency")
  ResponseEntity<Double> convert(
      @Parameter(description = "Base currency code", required = true) @RequestParam String base,
      @Parameter(description = "Target currency code", required = true) @RequestParam String currency,
      @Parameter(description = "Amount to convert", required = true) @RequestParam Double amount);

  @GetMapping("/exchange-conversions")
  @Operation(summary = "Convert a given amount from a base currency to multiple other currencies")
  ResponseEntity<Map<String, Double>> convertToMultipleCurrencies(
      @Parameter(description = "Base currency code", required = true) @RequestParam String base,
      @Parameter(description = "List of target currency codes", required = true) @RequestParam List<String> currencies,
      @Parameter(description = "Amount to convert", required = true) @RequestParam Double amount);
}
