package com.leanpay.loancalculator.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.leanpay.loancalculator.calculator.LoanCalculator;
import com.leanpay.loancalculator.dto.AmortizationScheduleCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationInputDto;
import com.leanpay.loancalculator.dto.PaymentDto;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CalculatorControllerTest {

  @Mock
  private LoanCalculator loanCalculator;

  @InjectMocks
  private CalculatorController calculatorController;

  @Test
  void testGenerateLoanCalculation() {

    // Given
    final LoanCalculationInputDto loanCalculationInput = LoanCalculationInputDto.builder()
        .amount(BigDecimal.valueOf(20000))
        .annualInterestPercent(BigDecimal.valueOf(5))
        .numberOfMonths(60)
        .build();
    final LoanCalculationDto loanCalculation = new LoanCalculationDto(BigDecimal.valueOf(377.42),
        BigDecimal.valueOf(2645.52));

    // Mock actions
    doReturn(loanCalculation).when(loanCalculator).calculateLoan(eq(loanCalculationInput));

    // Action
    final LoanCalculationDto loanCalculationResult = calculatorController
        .generateLoanCalculation(loanCalculationInput);

    // Assertion
    assertNotNull(loanCalculationResult);
    assertEquals(BigDecimal.valueOf(377.42), loanCalculationResult.getMonthlyPayment());
    assertEquals(BigDecimal.valueOf(2645.52), loanCalculationResult.getTotalInterestPaid());
    verify(loanCalculator).calculateLoan(eq(loanCalculationInput));
  }

  @Test
  void testGenerateLoanCalculationWithException() {

    // Given
    final String exceptionMessage = "Exception on calculateLoan method call.";
    final LoanCalculationInputDto loanCalculationInput = LoanCalculationInputDto.builder()
        .amount(BigDecimal.valueOf(20000))
        .annualInterestPercent(BigDecimal.valueOf(5))
        .numberOfMonths(60)
        .build();

    // Mock actions
    doThrow(new RuntimeException(exceptionMessage)).when(loanCalculator)
        .calculateLoan(eq(loanCalculationInput));

    // Action
    final Exception exception = assertThrows(RuntimeException.class,
        () -> calculatorController.generateLoanCalculation(loanCalculationInput));

    // Assertion
    assertEquals(exceptionMessage, exception.getMessage());
    verify(loanCalculator).calculateLoan(eq(loanCalculationInput));
  }

  @Test
  void testGenerateAmortizationScheduleCalculation() {

    // Given
    final LoanCalculationInputDto loanCalculationInput = LoanCalculationInputDto.builder()
        .amount(BigDecimal.valueOf(20000))
        .annualInterestPercent(BigDecimal.valueOf(5))
        .numberOfMonths(2)
        .build();
    final AmortizationScheduleCalculationDto amortizationScheduleCalculation = new AmortizationScheduleCalculationDto(
        BigDecimal.valueOf(10062.54),
        BigDecimal.valueOf(125.08),
        Arrays.asList(
            new PaymentDto(1, BigDecimal.valueOf(10062.54), BigDecimal.valueOf(9979.21),
                BigDecimal.valueOf(83.33), BigDecimal.valueOf(10020.79)),
            new PaymentDto(2, BigDecimal.valueOf(10062.54), BigDecimal.valueOf(10020.79),
                BigDecimal.valueOf(41.75), BigDecimal.ZERO)
        ));

    // Mock actions
    doReturn(amortizationScheduleCalculation).when(loanCalculator)
        .calculateLoanWithAmortizationSchedule(eq(loanCalculationInput));

    // Action
    final AmortizationScheduleCalculationDto amortizationScheduleCalculationResult = calculatorController
        .generateAmortizationScheduleCalculation(loanCalculationInput);

    // Assertion
    assertNotNull(amortizationScheduleCalculationResult);
    assertEquals(BigDecimal.valueOf(10062.54), amortizationScheduleCalculationResult.getMonthlyPayment());
    assertEquals(BigDecimal.valueOf(125.08), amortizationScheduleCalculationResult.getTotalInterestPaid());
    assertFalse(amortizationScheduleCalculationResult.getPayments().isEmpty());
    assertEquals(2, amortizationScheduleCalculationResult.getPayments().size());

    final PaymentDto firstPayment = amortizationScheduleCalculationResult.getPayments().get(0);
    assertEquals(1, firstPayment.getPaymentOrder());
    assertEquals(BigDecimal.valueOf(10062.54), firstPayment.getPaymentAmount());
    assertEquals(BigDecimal.valueOf(9979.21), firstPayment.getPrincipalAmount());
    assertEquals(BigDecimal.valueOf(83.33), firstPayment.getInterestAmount());
    assertEquals(BigDecimal.valueOf(10020.79), firstPayment.getBalanceOwed());

    final PaymentDto secondPayment = amortizationScheduleCalculationResult.getPayments().get(1);
    assertEquals(2, secondPayment.getPaymentOrder());
    assertEquals(BigDecimal.valueOf(10062.54), secondPayment.getPaymentAmount());
    assertEquals(BigDecimal.valueOf(10020.79), secondPayment.getPrincipalAmount());
    assertEquals(BigDecimal.valueOf(41.75), secondPayment.getInterestAmount());
    assertEquals(BigDecimal.ZERO, secondPayment.getBalanceOwed());

    verify(loanCalculator).calculateLoanWithAmortizationSchedule(eq(loanCalculationInput));
  }

  @Test
  void testGenerateAmortizationScheduleCalculationWithException() {

    // Given
    final String exceptionMessage = "Exception on calculateLoanWithAmortizationSchedule method call.";
    final LoanCalculationInputDto loanCalculationInput = LoanCalculationInputDto.builder()
        .amount(BigDecimal.valueOf(20000))
        .annualInterestPercent(BigDecimal.valueOf(5))
        .numberOfMonths(2)
        .build();

    // Mock actions
    doThrow(new RuntimeException(exceptionMessage)).when(loanCalculator)
        .calculateLoanWithAmortizationSchedule(eq(loanCalculationInput));

    // Action
    final Exception exception = assertThrows(RuntimeException.class,
        () -> calculatorController.generateAmortizationScheduleCalculation(loanCalculationInput));

    // Assertion
    assertEquals(exceptionMessage, exception.getMessage());
    verify(loanCalculator).calculateLoanWithAmortizationSchedule(eq(loanCalculationInput));
  }
}
