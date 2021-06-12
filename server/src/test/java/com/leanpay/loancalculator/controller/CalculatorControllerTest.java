package com.leanpay.loancalculator.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.leanpay.loancalculator.dto.AmortizationScheduleCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationInputDto;
import com.leanpay.loancalculator.dto.PaymentDto;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CalculatorControllerTest {

  @InjectMocks
  private CalculatorController calculatorController;

  @Test
  void testGenerateLoanCalculation() {

    // Given
    final LoanCalculationInputDto loanCalculationInputDto = LoanCalculationInputDto.builder()
        .amount(BigDecimal.valueOf(20000))
        .annualInterestPercent(BigDecimal.valueOf(5))
        .numberOfMonths(60)
        .build();

    // Action
    final LoanCalculationDto loanCalculationDto = calculatorController
        .generateLoanCalculation(loanCalculationInputDto);

    // Assertion
    assertNotNull(loanCalculationDto);
    assertEquals(loanCalculationDto.getMonthlyPayment(), BigDecimal.valueOf(377.42));
    assertEquals(loanCalculationDto.getTotalInterestPaid(), BigDecimal.valueOf(2645.48));
  }

  @Test
  void testGenerateAmortizationScheduleCalculation() {

    // Given
    final LoanCalculationInputDto loanCalculationInputDto = LoanCalculationInputDto.builder()
        .amount(BigDecimal.valueOf(20000))
        .annualInterestPercent(BigDecimal.valueOf(5))
        .numberOfMonths(2)
        .build();

    // Action
    final AmortizationScheduleCalculationDto amortizationScheduleCalculationDto = calculatorController
        .generateAmortizationScheduleCalculation(loanCalculationInputDto);

    // Assertion
    assertNotNull(amortizationScheduleCalculationDto);
    assertEquals(amortizationScheduleCalculationDto.getMonthlyPayment(),
        BigDecimal.valueOf(10062.54));
    assertEquals(amortizationScheduleCalculationDto.getTotalInterestPaid(),
        BigDecimal.valueOf(125.08));
    assertFalse(amortizationScheduleCalculationDto.getPayments().isEmpty());
    assertEquals(2, amortizationScheduleCalculationDto.getPayments().size());

    final PaymentDto firstPayment = amortizationScheduleCalculationDto.getPayments().get(0);
    assertEquals(1, firstPayment.getPaymentOrder());
    assertEquals(BigDecimal.valueOf(10062.54), firstPayment.getPaymentAmount());
    assertEquals(BigDecimal.valueOf(9979.21), firstPayment.getPrincipalAmount());
    assertEquals(BigDecimal.valueOf(83.33), firstPayment.getInterestAmount());
    assertEquals(BigDecimal.valueOf(10020.79), firstPayment.getBalanceOwed());

    final PaymentDto secondPayment = amortizationScheduleCalculationDto.getPayments().get(1);
    assertEquals(2, secondPayment.getPaymentOrder());
    assertEquals(BigDecimal.valueOf(10062.54), secondPayment.getPaymentAmount());
    assertEquals(BigDecimal.valueOf(10020.79), secondPayment.getPrincipalAmount());
    assertEquals(BigDecimal.valueOf(41.75), secondPayment.getInterestAmount());
    assertEquals(BigDecimal.ZERO, secondPayment.getBalanceOwed());
  }
}
