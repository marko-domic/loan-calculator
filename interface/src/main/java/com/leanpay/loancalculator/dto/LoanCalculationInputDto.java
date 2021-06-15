package com.leanpay.loancalculator.dto;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanCalculationInputDto {

  @NotNull(message = "Loan amount is mandatory")
  @DecimalMin(value = "0.0", inclusive = false, message = "Loan amount should be greater than zero")
  private BigDecimal amount;

  @NotNull(message = "Interest rate in percent is mandatory")
  @DecimalMin(value = "0.0", message = "Interest rate should be zero or greater than zero")
  @DecimalMax(value = "100.0", message = "Interest rate should be 100 or less")
  private BigDecimal annualInterestPercent;

  @NotNull(message = "Number of months is mandatory")
  @Positive(message = "Loan amount should be positive integer")
  private Integer numberOfMonths;
}
