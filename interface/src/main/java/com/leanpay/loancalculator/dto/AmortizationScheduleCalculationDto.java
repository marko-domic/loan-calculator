package com.leanpay.loancalculator.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmortizationScheduleCalculationDto {

  BigDecimal monthlyPayment;
  BigDecimal totalInterestPaid;
  List<PaymentDto> payments;
}
