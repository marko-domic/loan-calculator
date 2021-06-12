package com.leanpay.loancalculator.dto;

import java.math.BigDecimal;
import lombok.Value;

@Value
public class PaymentDto {

  Integer paymentOrder;
  BigDecimal paymentAmount;
  BigDecimal principalAmount;
  BigDecimal interestAmount;
  BigDecimal balanceOwed;
}
