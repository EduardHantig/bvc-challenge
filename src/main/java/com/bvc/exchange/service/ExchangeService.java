package com.bvc.exchange.service;

import com.bvc.exchange.exception.BadExchangeApiResponseException;
import com.bvc.exchange.exception.SymbolExchangeApiNotFoundException;
import com.bvc.exchange.model.CurrencyRate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeService.class);

  @Value("${exchange.api.liveUrl}")
  private String exchangeApiUrl;

  @Value("${exchange.api.accessKey}")
  private String exchangeAccessKey;

  private final RestTemplate restTemplate;

  public ExchangeService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Cacheable(value = "exchangeRates", key = "#base")
  public CurrencyRate getRatesForBase(String base) throws BadExchangeApiResponseException {
    String liveUrl = String.format("%s?access_key=%s&source=%s", exchangeApiUrl, exchangeAccessKey, base);
    CurrencyRate currencyRate = restTemplate.getForObject(liveUrl, CurrencyRate.class);

    if (currencyRate == null || currencyRate.getQuotes() == null || currencyRate.getQuotes().isEmpty()) {
      LOGGER.error("Unexpected payload received. API response is: {}.", currencyRate);
      throw new BadExchangeApiResponseException("Unexpected API response! Either you asked for non-existent base " +
              "currency, either bad response received from Exchange API!");
    }
    return currencyRate;
  }

  public Double getRateForBaseToSymbol(String base, String symbol) throws BadExchangeApiResponseException,
          SymbolExchangeApiNotFoundException {
    CurrencyRate rates = getRatesForBase(base);

    Double rate = rates.getQuotes().get(base + symbol);
    if (rate == null) {
      LOGGER.error("Bad request! Provided symbol {} is not found from received quotes {}.", symbol, rates.getQuotes());
      throw new SymbolExchangeApiNotFoundException("Provided currency symbol " + symbol + " is not found in received " +
              "quotes from Exchange API!");
    }
    return rates.getQuotes().get(base + symbol);
  }

  public Double convertValue(String base, String symbol, Double amount) throws BadExchangeApiResponseException,
          SymbolExchangeApiNotFoundException {
    Double rate = getRateForBaseToSymbol(base, symbol);
    return rate * amount;
  }

  public Map<String, Double> convertToMultipleCurrencies(String base, List<String> symbols, Double amount)
          throws BadExchangeApiResponseException, SymbolExchangeApiNotFoundException {
    Map<String, Double> convertedValues = new HashMap<>();
    CurrencyRate rates = getRatesForBase(base);

    for (String symbol : symbols) {
      Double rate = rates.getQuotes().get(base + symbol);
      if (rate == null) {
        LOGGER.error("Bad request! Provided symbol {} is not found from received quotes {}.", symbol, rates.getQuotes());
        throw new SymbolExchangeApiNotFoundException("Provided currency symbol " + symbol + " is not found in " +
                "received quotes from Exchange API!");
      }
      convertedValues.put(symbol, rate * amount);
    }

    return convertedValues;
  }
}