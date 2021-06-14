package com.leanpay.loancalculator.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanCalculationDto {

  BigDecimal monthlyPayment;
  BigDecimal totalInterestPaid;
}
