package com.leanpay.loancalculator.controller;

import com.leanpay.loancalculator.api.CalculatorApi;
import com.leanpay.loancalculator.calculator.LoanCalculator;
import com.leanpay.loancalculator.dto.AmortizationScheduleCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationInputDto;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
class CalculatorController implements CalculatorApi {

  private final LoanCalculator loanCalculator;

  @Autowired
  public CalculatorController(LoanCalculator loanCalculator) {
    this.loanCalculator = loanCalculator;
  }

  @Override
  public LoanCalculationDto generateLoanCalculation(
      LoanCalculationInputDto loanCalculationInputDto) {
    log.info("Request received for loan calculation. Input: {}", loanCalculationInputDto);
    try {
      return loanCalculator.calculateLoan(loanCalculationInputDto);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw e;
    }
  }

  @Override
  public AmortizationScheduleCalculationDto generateAmortizationScheduleCalculation(
      LoanCalculationInputDto loanCalculationInputDto) {
    log.info("Request received for amortization schedule loan calculation. Input: {}",
        loanCalculationInputDto);
    try {
      return loanCalculator.calculateLoanWithAmortizationSchedule(loanCalculationInputDto);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw e;
    }
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return errors;
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public Map<String, String> handleBadRequest(HttpMessageNotReadableException ex) {
    return Collections.singletonMap("errorMessage",
        "Some fields in loan calculation input are not in correct format. Please, check inputs once again.");
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public Map<String, String> handleInternalServerError(Exception exception) {
    return Collections.singletonMap("errorMessage",
        "Something went wrong in calculation. Please, refer to error logs for more details.");
  }
}
