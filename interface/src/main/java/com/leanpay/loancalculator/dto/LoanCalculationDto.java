package com.leanpay.loancalculator.dto;

import java.math.BigDecimal;
import lombok.Value;

@Value
public class LoanCalculationDto {

  BigDecimal monthlyPayment;
  BigDecimal totalInterestPaid;
}
