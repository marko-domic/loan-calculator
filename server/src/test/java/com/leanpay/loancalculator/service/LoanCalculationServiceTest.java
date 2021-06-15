package com.leanpay.loancalculator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.leanpay.loancalculator.model.LoanCalculation;
import com.leanpay.loancalculator.model.Payment;
import com.leanpay.loancalculator.repository.LoanCalculationRepository;
import com.leanpay.loancalculator.repository.PaymentRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LoanCalculationService.class})
public class LoanCalculationServiceTest {

  @MockBean
  private LoanCalculationRepository loanCalculationRepository;

  @MockBean
  private PaymentRepository paymentRepository;

  @Autowired
  private LoanCalculationService loanCalculationService;

  @Test
  void testSaveLoanCalculation() {

    // Given
    final Payment payment1 = createPayment(1, 10062.54, 9979.21, 83.33, 10020.79);
    final Payment payment2 = createPayment(2, 10062.54, 10020.79, 41.75, 0);
    final List<Payment> payments = Arrays.asList(payment1, payment2);
    final LoanCalculation loanCalculation = LoanCalculation.builder()
        .amount(20000.0)
        .annualInterestPercent(5.0)
        .numberOfMonths(2)
        .monthlyPayment(10062.54)
        .totalInterestPaid(125.08)
        .payments(payments)
        .build();

    // Mocks
    doReturn(loanCalculation).when(loanCalculationRepository).saveAndFlush(eq(loanCalculation));
    doReturn(payment1).when(paymentRepository).saveAndFlush(eq(payment1));
    doReturn(payment2).when(paymentRepository).saveAndFlush(eq(payment2));

    // Action
    loanCalculationService.saveLoanCalculation(loanCalculation);

    // Assertion
    verify(loanCalculationRepository).saveAndFlush(eq(loanCalculation));
    verify(paymentRepository).saveAndFlush(eq(payment1));
    verify(paymentRepository).saveAndFlush(eq(payment2));
  }

  @Test
  void testSaveLoanCalculationWithException() {

    // Given
    final String exceptionMessage = "Exception message on loan calculation save method call.";
    final LoanCalculation loanCalculation = LoanCalculation.builder()
        .amount(20000.0)
        .annualInterestPercent(5.0)
        .numberOfMonths(2)
        .monthlyPayment(10062.54)
        .totalInterestPaid(125.08)
        .payments(Collections.emptyList())
        .build();

    // Mocks
    doThrow(new RuntimeException(exceptionMessage)).when(loanCalculationRepository)
        .saveAndFlush(eq(loanCalculation));

    // Action
    final Exception exception = assertThrows(RuntimeException.class,
        () -> loanCalculationService.saveLoanCalculation(loanCalculation));

    // Assertion
    assertEquals(exceptionMessage, exception.getMessage());
    verify(loanCalculationRepository).saveAndFlush(eq(loanCalculation));
    verify(paymentRepository, never()).saveAndFlush(any(Payment.class));
  }

  private Payment createPayment(int paymentOrder, double paymentAmount,
      double principalAmount, double interestAmount, double balanceOwed) {
    return Payment.builder()
        .paymentOrder(paymentOrder)
        .paymentAmount(paymentAmount)
        .principalAmount(principalAmount)
        .interestAmount(interestAmount)
        .balanceOwed(balanceOwed)
        .build();
  }
}
