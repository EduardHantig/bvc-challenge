package com.bvc.exchange.service;

import com.bvc.exchange.model.CurrencyRate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeService {

  @Value("${exchange.api.liveUrl}")
  private String exchangeApiUrl;

  @Value("${exchange.api.accessKey}")
  private String exchangeAccessKey;

  private final RestTemplate restTemplate;

  public ExchangeService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Cacheable(value = "exchangeRates", key = "#base")
  public CurrencyRate getRatesForBase(String base) {
    return restTemplate.getForObject(exchangeApiUrl + "?access_key=" + exchangeAccessKey + "&source=" + base, CurrencyRate.class);
  }

  public Double getRateForBaseToSymbol(String base, String symbol) {
    CurrencyRate rates = getRatesForBase(base);
    return rates.getQuotes().get(base + symbol);
  }

  public Double convertValue(String base, String symbol, Double amount) {
    Double rate = getRateForBaseToSymbol(base, symbol);
    return rate * amount;
  }

  public Map<String, Double> convertToMultipleCurrencies(String base, List<String> symbols, Double amount) {
    Map<String, Double> convertedValues = new HashMap<>();
    CurrencyRate rates = getRatesForBase(base);

    for (String symbol : symbols) {
      Double rate = rates.getQuotes().get(base + symbol);
      if (rate != null) {
        convertedValues.put(symbol, rate * amount);
      }
    }

    return convertedValues;
  }
}