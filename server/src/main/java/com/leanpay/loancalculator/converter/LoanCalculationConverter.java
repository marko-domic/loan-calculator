package com.leanpay.loancalculator.converter;

import com.leanpay.loancalculator.dto.AmortizationScheduleCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationInputDto;
import com.leanpay.loancalculator.dto.PaymentDto;
import com.leanpay.loancalculator.model.LoanCalculation;
import com.leanpay.loancalculator.model.Payment;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class LoanCalculationConverter {

  public LoanCalculation toModel(@NotNull LoanCalculationInputDto loanCalculationInput,
      @NotNull AmortizationScheduleCalculationDto amortizationScheduleCalculation) {

    final LoanCalculation loanCalculation = LoanCalculation.builder()
        .amount(loanCalculationInput.getAmount().doubleValue())
        .annualInterestPercent(loanCalculationInput.getAnnualInterestPercent().doubleValue())
        .numberOfMonths(loanCalculationInput.getNumberOfMonths())
        .monthlyPayment(amortizationScheduleCalculation.getMonthlyPayment().doubleValue())
        .createdDate(Instant.now())
        .totalInterestPaid(amortizationScheduleCalculation.getTotalInterestPaid().doubleValue())
        .build();
    loanCalculation.setPayments(
        getLoanPayments(amortizationScheduleCalculation.getPayments(), loanCalculation));

    return loanCalculation;
  }

  public Payment toModel(PaymentDto paymentDto, LoanCalculation loanCalculation) {
    return Payment.builder()
        .calculation(loanCalculation)
        .paymentOrder(paymentDto.getPaymentOrder())
        .paymentAmount(paymentDto.getPaymentAmount().doubleValue())
        .principalAmount(paymentDto.getPrincipalAmount().doubleValue())
        .interestAmount(paymentDto.getInterestAmount().doubleValue())
        .balanceOwed(paymentDto.getBalanceOwed().doubleValue())
        .build();
  }

  private List<Payment> getLoanPayments(List<PaymentDto> paymentDtoList,
      LoanCalculation loanCalculation) {

    if (paymentDtoList == null || paymentDtoList.isEmpty()) {
      return Collections.emptyList();
    }

    final List<Payment> payments = new ArrayList<>(paymentDtoList.size());
    paymentDtoList.forEach(paymentDto -> payments.add(toModel(paymentDto, loanCalculation)));

    return payments;
  }
}
