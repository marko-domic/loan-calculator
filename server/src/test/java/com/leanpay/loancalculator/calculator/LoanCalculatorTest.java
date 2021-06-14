package com.leanpay.loancalculator.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.leanpay.loancalculator.converter.LoanCalculationConverter;
import com.leanpay.loancalculator.dto.AmortizationScheduleCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationInputDto;
import com.leanpay.loancalculator.dto.PaymentDto;
import com.leanpay.loancalculator.service.LoanCalculationService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LoanCalculatorTest {

  private static final int CALCULATION_SCALE = 30;
  private static final int DISPLAY_SCALE = 2;

  @Mock
  private LoanCalculationService loanCalculationService;

  @Mock
  private LoanCalculationConverter loanCalculationConverter;

  private LoanCalculator loanCalculator;

  @BeforeEach
  void setup() {
    loanCalculator = new LoanCalculator(CALCULATION_SCALE, DISPLAY_SCALE, loanCalculationService,
        loanCalculationConverter);
  }

  @ParameterizedTest
  @MethodSource("calculateLoanTestParameters")
  void testCalculateLoan(int amount, double interestRate, int numOfMonths,
      double expectedMonthlyPayment, double expectedTotalInterest) {

    // Given
    final LoanCalculationInputDto loanCalculationInput = LoanCalculationInputDto.builder()
        .amount(BigDecimal.valueOf(amount))
        .annualInterestPercent(BigDecimal.valueOf(interestRate))
        .numberOfMonths(numOfMonths)
        .build();

    // Action
    final LoanCalculationDto loanCalculation = loanCalculator.calculateLoan(loanCalculationInput);

    // Assertion
    assertNotNull(loanCalculation);
    assertEquals(BigDecimal.valueOf(expectedMonthlyPayment), loanCalculation.getMonthlyPayment());
    assertEquals(BigDecimal.valueOf(expectedTotalInterest), loanCalculation.getTotalInterestPaid());
  }

  @Test
  void testCalculateLoanWithAmortizationSchedule() {

    // Given
    final LoanCalculationInputDto loanCalculationInput = LoanCalculationInputDto.builder()
        .amount(BigDecimal.valueOf(1000))
        .annualInterestPercent(BigDecimal.valueOf(5))
        .numberOfMonths(10)
        .build();

    // Action
    final AmortizationScheduleCalculationDto amortizationScheduleCalculation = loanCalculator
        .calculateLoanWithAmortizationSchedule(loanCalculationInput);

    // Assertion
    assertNotNull(amortizationScheduleCalculation);
    assertEquals(BigDecimal.valueOf(102.31), amortizationScheduleCalculation.getMonthlyPayment());
    assertEquals(BigDecimal.valueOf(23.06), amortizationScheduleCalculation.getTotalInterestPaid());
    assertFalse(amortizationScheduleCalculation.getPayments().isEmpty());
    assertEquals(10, amortizationScheduleCalculation.getPayments().size());
    final List<PaymentDto> payments = amortizationScheduleCalculation.getPayments();
    validatePayment(payments.get(0), 1, 102.31, 98.14, 4.17, 901.86);
    validatePayment(payments.get(1), 2, 102.31, 98.55, 3.76, 803.31);
    validatePayment(payments.get(2), 3, 102.31, 98.96, 3.35, 704.35);
    validatePayment(payments.get(3), 4, 102.31, 99.38, 2.93, 604.97);
    validatePayment(payments.get(4), 5, 102.31, 99.79, 2.52, 505.18);
    validatePayment(payments.get(5), 6, 102.31, 100.21, 2.10, 404.97);
    validatePayment(payments.get(6), 7, 102.31, 100.62, 1.69, 304.35);
    validatePayment(payments.get(7), 8, 102.31, 101.04, 1.27, 203.31);
    validatePayment(payments.get(8), 9, 102.31, 101.46, 0.85, 101.85);
    validatePayment(payments.get(9), 10, 102.27, 101.85, 0.42, 0);
  }

  private void validatePayment(PaymentDto payment, int expectedOrder, double expectedPaymentAmount,
      double expectedPrincipalAmount, double expectedInterestAmount, double expectedBalanceOwed) {
    assertEquals(expectedOrder, payment.getPaymentOrder());
    assertEquals(BigDecimal.valueOf(expectedPaymentAmount).setScale(2, RoundingMode.HALF_EVEN),
        payment.getPaymentAmount());
    assertEquals(BigDecimal.valueOf(expectedPrincipalAmount).setScale(2, RoundingMode.HALF_EVEN),
        payment.getPrincipalAmount());
    assertEquals(BigDecimal.valueOf(expectedInterestAmount).setScale(2, RoundingMode.HALF_EVEN),
        payment.getInterestAmount());
    assertEquals(BigDecimal.valueOf(expectedBalanceOwed).setScale(2, RoundingMode.HALF_EVEN),
        payment.getBalanceOwed());
  }

  private static Stream<Arguments> calculateLoanTestParameters() {
    return Stream.of(
        Arguments.of(20000, 5, 60, 377.42, 2645.52),
        Arguments.of(1000, 5, 10, 102.31, 23.06),
        Arguments.of(30000, 8.4, 72, 531.88, 8294.97),
        Arguments.of(20000, 5, 2, 10062.54, 125.08)
    );
  }
}
