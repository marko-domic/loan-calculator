package com.leanpay.loancalculator.repository;

import com.leanpay.loancalculator.model.LoanCalculation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanCalculationRepository extends JpaRepository<LoanCalculation, Integer> {

  LoanCalculation findFirstByAmountAndAndAnnualInterestPercentAndNumberOfMonths(double amount,
      double annualInterestPercent, int numberOfMonths);
}
