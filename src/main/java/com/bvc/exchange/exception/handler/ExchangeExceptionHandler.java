package com.bvc.exchange.exception.handler;

import com.bvc.exchange.exception.BadExchangeApiResponseException;
import com.bvc.exchange.exception.SymbolExchangeApiNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
public class ExchangeExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeExceptionHandler.class);

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<String> handleRestClientException(Exception e) {
        LOGGER.error("Error when calling Exchange API! Exception message is " + e.getMessage(), e);
        return new ResponseEntity<>("Error when calling Exchange API! Exception message is " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadExchangeApiResponseException.class)
    public ResponseEntity<String> handleBadExchangeApiResponseException(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SymbolExchangeApiNotFoundException.class)
    public ResponseEntity<String> handleSymbolExchangeApiNotFoundException(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
