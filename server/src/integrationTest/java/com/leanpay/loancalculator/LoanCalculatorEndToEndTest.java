package com.leanpay.loancalculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.leanpay.loancalculator.dto.AmortizationScheduleCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationInputDto;
import com.leanpay.loancalculator.dto.PaymentDto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Tag("integration")
public class LoanCalculatorEndToEndTest {

  @Test
  void testLoanCalculation(@Autowired TestRestTemplate restTemplate) {

    // Create HttpEntity
    final LoanCalculationInputDto loanCalculationInput = LoanCalculationInputDto.builder()
        .amount(BigDecimal.valueOf(20000))
        .annualInterestPercent(BigDecimal.valueOf(5))
        .numberOfMonths(60)
        .build();
    final HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    final HttpEntity<LoanCalculationInputDto> requestEntity = new HttpEntity<>(loanCalculationInput,
        headers);

    final ResponseEntity<LoanCalculationDto> responseEntity = restTemplate
        .exchange("/calculator/loan-calculation", HttpMethod.POST, requestEntity,
            LoanCalculationDto.class);

    // Assertions
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    final LoanCalculationDto loanCalculation = responseEntity.getBody();
    assertNotNull(loanCalculation);
    assertEquals(BigDecimal.valueOf(377.42), loanCalculation.getMonthlyPayment());
    assertEquals(BigDecimal.valueOf(2645.52), loanCalculation.getTotalInterestPaid());
  }

  @Test
  void testLoanCalculationWithAmortizationSchedule(@Autowired TestRestTemplate restTemplate) {

    // Create HttpEntity
    final LoanCalculationInputDto loanCalculationInput = LoanCalculationInputDto.builder()
        .amount(BigDecimal.valueOf(1000))
        .annualInterestPercent(BigDecimal.valueOf(5))
        .numberOfMonths(10)
        .build();
    final HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    final HttpEntity<LoanCalculationInputDto> requestEntity = new HttpEntity<>(loanCalculationInput,
        headers);

    final ResponseEntity<AmortizationScheduleCalculationDto> responseEntity = restTemplate
        .exchange("/calculator/amortization-schedule-calculation", HttpMethod.POST, requestEntity,
            AmortizationScheduleCalculationDto.class);

    // Assertions
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    final AmortizationScheduleCalculationDto amortizationScheduleCalculation = responseEntity
        .getBody();
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
}
