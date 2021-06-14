package com.leanpay.loancalculator.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "calculation_id", referencedColumnName = "id")
  @EqualsAndHashCode.Exclude
  private LoanCalculation calculation;

  @NotNull
  private Integer paymentOrder;

  @NotNull
  private Double paymentAmount;

  @NotNull
  private Double principalAmount;

  @NotNull
  private Double interestAmount;

  @NotNull
  private Double balanceOwed;
}
