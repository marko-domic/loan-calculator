package com.leanpay.loancalculator.controller;

import com.leanpay.loancalculator.api.CalculatorApi;
import com.leanpay.loancalculator.calculator.LoanCalculator;
import com.leanpay.loancalculator.dto.AmortizationScheduleCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationInputDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
    return loanCalculator.calculateLoan(loanCalculationInputDto);
  }

  @Override
  public AmortizationScheduleCalculationDto generateAmortizationScheduleCalculation(
      LoanCalculationInputDto loanCalculationInputDto) {
    log.info("Request received for amortization schedule loan calculation. Input: {}",
        loanCalculationInputDto);
    return loanCalculator.calculateLoanWithAmortizationSchedule(loanCalculationInputDto);
  }
}
