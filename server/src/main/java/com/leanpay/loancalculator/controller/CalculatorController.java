package com.leanpay.loancalculator.controller;

import com.leanpay.loancalculator.api.CalculatorApi;
import com.leanpay.loancalculator.dto.AmortizationScheduleCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationInputDto;
import com.leanpay.loancalculator.dto.PaymentDto;
import java.math.BigDecimal;
import java.util.Arrays;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
class CalculatorController implements CalculatorApi {

  @Override
  public LoanCalculationDto generateLoanCalculation(
      LoanCalculationInputDto loanCalculationInputDto) {
    // TODO: Implement generateLoanCalculation API
    final LoanCalculationDto dummyLoanCalculation = new LoanCalculationDto(
        BigDecimal.valueOf(377.42), BigDecimal.valueOf(2645.48));
    return dummyLoanCalculation;
  }

  @Override
  public AmortizationScheduleCalculationDto generateAmortizationScheduleCalculation(
      LoanCalculationInputDto loanCalculationInputDto) {
    // TODO: Implement generateAmortizationScheduleCalculation API
    return createDummyCalculation();
  }

  // TODO: Remove this method after implementation of generateAmortizationScheduleCalculation API
  private AmortizationScheduleCalculationDto createDummyCalculation() {
    return new AmortizationScheduleCalculationDto(BigDecimal.valueOf(10062.54),
        BigDecimal.valueOf(125.08),
        Arrays.asList(
            new PaymentDto(1, BigDecimal.valueOf(10062.54), BigDecimal.valueOf(9979.21),
                BigDecimal.valueOf(83.33), BigDecimal.valueOf(10020.79)),
            new PaymentDto(2, BigDecimal.valueOf(10062.54), BigDecimal.valueOf(10020.79),
                BigDecimal.valueOf(41.75), BigDecimal.ZERO)
        ));
  }
}
