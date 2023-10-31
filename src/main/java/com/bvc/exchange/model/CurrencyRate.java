package com.bvc.exchange.model;

import java.util.Map;
import java.util.Objects;

public class CurrencyRate {
  private String source;
  private Map<String, Double> quotes;

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public Map<String, Double> getQuotes() {
    return quotes;
  }

  public void setQuotes(Map<String, Double> quotes) {
    this.quotes = quotes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CurrencyRate that = (CurrencyRate) o;
    return Objects.equals(source, that.source) && Objects.equals(quotes, that.quotes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(source, quotes);
  }

  @Override
  public String toString() {
    return "CurrencyRate{" +
        "source='" + source + '\'' +
        ", quotes=" + quotes +
        '}';
  }
}