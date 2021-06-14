package com.leanpay.loancalculator.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.leanpay.loancalculator.dto.AmortizationScheduleCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationInputDto;
import com.leanpay.loancalculator.dto.PaymentDto;
import com.leanpay.loancalculator.model.LoanCalculation;
import com.leanpay.loancalculator.model.Payment;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LoanCalculationConverter.class})
public class LoanCalculationConverterTest {

  @Autowired
  private LoanCalculationConverter loanCalculationConverter;

  @Test
  void testLoanCalculationInputDtoToModel() {

    // Given
    final List<PaymentDto> paymentDtoList = new ArrayList<>();
    paymentDtoList.add(createPaymentDto(1, 10062.54, 9979.21, 83.33, 10020.79));
    paymentDtoList.add(createPaymentDto(2, 10062.54, 10020.79, 41.75, 0));
    final LoanCalculationInputDto loanCalculationInput = LoanCalculationInputDto.builder()
        .amount(BigDecimal.valueOf(20000))
        .annualInterestPercent(BigDecimal.valueOf(5))
        .numberOfMonths(2)
        .build();
    final AmortizationScheduleCalculationDto amortizationScheduleCalculation = AmortizationScheduleCalculationDto
        .builder()
        .monthlyPayment(BigDecimal.valueOf(10062.54))
        .totalInterestPaid(BigDecimal.valueOf(125.08))
        .payments(paymentDtoList)
        .build();

    // Action
    final LoanCalculation loanCalculation = loanCalculationConverter
        .toModel(loanCalculationInput, amortizationScheduleCalculation);

    // Assertion
    assertNotNull(loanCalculation);
    assertEquals(Double.valueOf(20000.0), loanCalculation.getAmount());
    assertEquals(Double.valueOf(5), loanCalculation.getAnnualInterestPercent());
    assertEquals(Integer.valueOf(2), loanCalculation.getNumberOfMonths());
    assertEquals(Double.valueOf(10062.54), loanCalculation.getMonthlyPayment());
    assertEquals(Double.valueOf(125.08), loanCalculation.getTotalInterestPaid());
    assertFalse(loanCalculation.getPayments().isEmpty());
    assertEquals(2, loanCalculation.getPayments().size());
    assertPayment(loanCalculation.getPayments().get(0), loanCalculation, 1, 10062.54, 9979.21,
        83.33, 10020.79);
    assertPayment(loanCalculation.getPayments().get(1), loanCalculation, 2, 10062.54, 10020.79,
        41.75, 0);
  }

  @Test
  void testPaymentDtoToModel() {

    // Given
    final PaymentDto paymentDto = createPaymentDto(1, 10062.54, 9979.21, 83.33, 10020.79);
    final LoanCalculation loanCalculation = LoanCalculation.builder()
        .amount(20000.0)
        .annualInterestPercent(5.0)
        .numberOfMonths(2)
        .build();

    // Action
    final Payment payment = loanCalculationConverter.toModel(paymentDto, loanCalculation);

    // Assertion
    assertNotNull(payment);
    assertPayment(payment, loanCalculation, 1, 10062.54, 9979.21, 83.33, 10020.79);
  }

  private PaymentDto createPaymentDto(int paymentOrder, double paymentAmount,
      double principalAmount, double interestAmount, double balanceOwed) {
    return PaymentDto.builder()
        .paymentOrder(paymentOrder)
        .paymentAmount(BigDecimal.valueOf(paymentAmount))
        .principalAmount(BigDecimal.valueOf(principalAmount))
        .interestAmount(BigDecimal.valueOf(interestAmount))
        .balanceOwed(BigDecimal.valueOf(balanceOwed))
        .build();
  }

  private void assertPayment(Payment payment, LoanCalculation loanCalculation, int paymentOrder,
      double paymentAmount, double principalAmount, double interestAmount, double balanceOwed) {
    assertNotNull(payment);
    assertEquals(loanCalculation, payment.getCalculation());
    assertEquals(Integer.valueOf(paymentOrder), payment.getPaymentOrder());
    assertEquals(Double.valueOf(paymentAmount), payment.getPaymentAmount());
    assertEquals(Double.valueOf(principalAmount), payment.getPrincipalAmount());
    assertEquals(Double.valueOf(interestAmount), payment.getInterestAmount());
    assertEquals(Double.valueOf(balanceOwed), payment.getBalanceOwed());
  }
}
