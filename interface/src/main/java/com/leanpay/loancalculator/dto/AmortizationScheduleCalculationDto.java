package com.leanpay.loancalculator.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Value;

@Value
public class AmortizationScheduleCalculationDto {

  BigDecimal monthlyPayment;
  BigDecimal totalInterestPaid;
  List<PaymentDto> payments;
}
