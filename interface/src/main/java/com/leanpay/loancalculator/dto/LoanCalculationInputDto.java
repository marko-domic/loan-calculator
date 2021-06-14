package com.leanpay.loancalculator.dto;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanCalculationInputDto {

  @NotNull
  BigDecimal amount;

  @NotNull
  BigDecimal annualInterestPercent;

  @NotNull
  Integer numberOfMonths;
}
