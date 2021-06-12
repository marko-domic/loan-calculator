package com.leanpay.loancalculator.api;

import com.leanpay.loancalculator.dto.AmortizationScheduleCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationInputDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/calculator")
public interface CalculatorApi {

  @PostMapping(value = "/loan-calculation", produces = {"application/json"})
  LoanCalculationDto generateLoanCalculation(
      @RequestBody LoanCalculationInputDto loanCalculationInputDto);

  @PostMapping(value = "/amortization-schedule-calculation", produces = {"application/json"})
  AmortizationScheduleCalculationDto generateAmortizationScheduleCalculation(
      @RequestBody LoanCalculationInputDto loanCalculationInputDto);
}
