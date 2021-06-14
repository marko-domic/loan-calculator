package com.leanpay.loancalculator.model;

import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanCalculation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  private Double amount;

  @NotNull
  private Double annualInterestPercent;

  @NotNull
  private Integer numberOfMonths;

  @NotNull
  private Double monthlyPayment;

  @NotNull
  private Double totalInterestPaid;

  @NotNull
  @OneToMany(mappedBy = "calculation")
  @EqualsAndHashCode.Exclude
  private List<Payment> payments;
}
