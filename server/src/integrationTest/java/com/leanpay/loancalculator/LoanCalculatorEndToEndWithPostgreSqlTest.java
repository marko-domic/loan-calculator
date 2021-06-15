package com.leanpay.loancalculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.leanpay.loancalculator.dto.AmortizationScheduleCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationInputDto;
import com.leanpay.loancalculator.dto.PaymentDto;
import com.leanpay.loancalculator.model.LoanCalculation;
import com.leanpay.loancalculator.model.Payment;
import com.leanpay.loancalculator.repository.LoanCalculationRepository;
import com.leanpay.loancalculator.repository.PaymentRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Tag("integration")
@TestPropertySource(properties = {"spring.flyway.locations=classpath:postgresql/db/migration"})
public class LoanCalculatorEndToEndWithPostgreSqlTest {

  private static PostgreSQLContainer<?> postgreSql;

  @Autowired
  private LoanCalculationRepository loanCalculationRepository;

  @Autowired
  private PaymentRepository paymentRepository;

  @BeforeAll
  public static void setup() {

    // setup database
    postgreSql = new PostgreSQLContainer<>("postgres:11")
        .withLogConsumer(new Slf4jLogConsumer(log));
    postgreSql.start();
    createSchema();
  }

  @AfterAll
  public static void stopServices() {

    // stop MySql
    if (postgreSql != null) {
      postgreSql.close();
      postgreSql = null;
    }
  }

  @DynamicPropertySource
  public static void dynamicPropertySource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgreSql::getJdbcUrl);
    registry.add("spring.datasource.username", postgreSql::getUsername);
    registry.add("spring.datasource.password", postgreSql::getPassword);
  }

  private static void createSchema() {
    final Flyway flyway = Flyway.configure()
        .dataSource(postgreSql.getJdbcUrl(), postgreSql.getUsername(), postgreSql.getPassword())
        .locations("postgresql/db/migration")
        .load();
    flyway.migrate();
  }

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

    final LoanCalculationDto loanCalculationDto = responseEntity.getBody();
    assertNotNull(loanCalculationDto);
    assertEquals(BigDecimal.valueOf(377.42), loanCalculationDto.getMonthlyPayment());
    assertEquals(BigDecimal.valueOf(2645.52), loanCalculationDto.getTotalInterestPaid());

    final LoanCalculation loanCalculation = loanCalculationRepository
        .findFirstByAmountAndAndAnnualInterestPercentAndNumberOfMonths(20000, 5, 60);
    assertNotNull(loanCalculation);
    assertEquals(Double.valueOf(20000), loanCalculation.getAmount());
    assertEquals(Double.valueOf(5), loanCalculation.getAnnualInterestPercent());
    assertEquals(Integer.valueOf(60), loanCalculation.getNumberOfMonths());
    assertEquals(Double.valueOf(377.42), loanCalculation.getMonthlyPayment());
    assertEquals(Double.valueOf(2645.52), loanCalculation.getTotalInterestPaid());

    final List<Payment> payments = paymentRepository
        .findByCalculationOrderByPaymentOrder(loanCalculation);
    assertNotNull(payments);
    assertFalse(payments.isEmpty());
    assertEquals(60, payments.size());
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

    final LoanCalculation loanCalculation = loanCalculationRepository
        .findFirstByAmountAndAndAnnualInterestPercentAndNumberOfMonths(1000, 5, 10);
    assertNotNull(loanCalculation);
    assertEquals(Double.valueOf(1000), loanCalculation.getAmount());
    assertEquals(Double.valueOf(5), loanCalculation.getAnnualInterestPercent());
    assertEquals(Integer.valueOf(10), loanCalculation.getNumberOfMonths());
    assertEquals(Double.valueOf(102.31), loanCalculation.getMonthlyPayment());
    assertEquals(Double.valueOf(23.06), loanCalculation.getTotalInterestPaid());

    final List<Payment> payments = paymentRepository
        .findByCalculationOrderByPaymentOrder(loanCalculation);
    assertNotNull(payments);
    assertFalse(payments.isEmpty());
    assertEquals(10, payments.size());

    final List<PaymentDto> paymentDtoList = amortizationScheduleCalculation.getPayments();
    validatePayment(paymentDtoList.get(0), payments.get(0), 1, 102.31, 98.14, 4.17, 901.86);
    validatePayment(paymentDtoList.get(1), payments.get(1), 2, 102.31, 98.55, 3.76, 803.31);
    validatePayment(paymentDtoList.get(2), payments.get(2), 3, 102.31, 98.96, 3.35, 704.35);
    validatePayment(paymentDtoList.get(3), payments.get(3), 4, 102.31, 99.38, 2.93, 604.97);
    validatePayment(paymentDtoList.get(4), payments.get(4), 5, 102.31, 99.79, 2.52, 505.18);
    validatePayment(paymentDtoList.get(5), payments.get(5), 6, 102.31, 100.21, 2.10, 404.97);
    validatePayment(paymentDtoList.get(6), payments.get(6), 7, 102.31, 100.62, 1.69, 304.35);
    validatePayment(paymentDtoList.get(7), payments.get(7), 8, 102.31, 101.04, 1.27, 203.31);
    validatePayment(paymentDtoList.get(8), payments.get(8), 9, 102.31, 101.46, 0.85, 101.85);
    validatePayment(paymentDtoList.get(9), payments.get(9), 10, 102.27, 101.85, 0.42, 0);
  }

  private void validatePayment(PaymentDto paymentDto, Payment payment, int expectedOrder,
      double expectedPaymentAmount, double expectedPrincipalAmount, double expectedInterestAmount,
      double expectedBalanceOwed) {
    validatePaymentDto(paymentDto, expectedOrder, expectedPaymentAmount, expectedPrincipalAmount,
        expectedInterestAmount, expectedBalanceOwed);
    validatePaymentModel(payment, expectedOrder, expectedPaymentAmount, expectedPrincipalAmount,
        expectedInterestAmount, expectedBalanceOwed);
  }

  private void validatePaymentDto(PaymentDto paymentDto, int expectedOrder,
      double expectedPaymentAmount, double expectedPrincipalAmount, double expectedInterestAmount,
      double expectedBalanceOwed) {
    assertEquals(expectedOrder, paymentDto.getPaymentOrder());
    assertEquals(BigDecimal.valueOf(expectedPaymentAmount).setScale(2, RoundingMode.HALF_EVEN),
        paymentDto.getPaymentAmount());
    assertEquals(BigDecimal.valueOf(expectedPrincipalAmount).setScale(2, RoundingMode.HALF_EVEN),
        paymentDto.getPrincipalAmount());
    assertEquals(BigDecimal.valueOf(expectedInterestAmount).setScale(2, RoundingMode.HALF_EVEN),
        paymentDto.getInterestAmount());
    assertEquals(BigDecimal.valueOf(expectedBalanceOwed).setScale(2, RoundingMode.HALF_EVEN),
        paymentDto.getBalanceOwed());
  }

  private void validatePaymentModel(Payment payment, int expectedOrder,
      double expectedPaymentAmount, double expectedPrincipalAmount, double expectedInterestAmount,
      double expectedBalanceOwed) {
    assertEquals(expectedOrder, payment.getPaymentOrder());
    assertEquals(Double.valueOf(expectedPaymentAmount), payment.getPaymentAmount());
    assertEquals(Double.valueOf(expectedPrincipalAmount), payment.getPrincipalAmount());
    assertEquals(Double.valueOf(expectedInterestAmount), payment.getInterestAmount());
    assertEquals(Double.valueOf(expectedBalanceOwed), payment.getBalanceOwed());
  }
}
