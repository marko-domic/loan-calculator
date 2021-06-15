package com.leanpay.loancalculator.service;

import com.leanpay.loancalculator.model.LoanCalculation;
import com.leanpay.loancalculator.repository.LoanCalculationRepository;
import com.leanpay.loancalculator.repository.PaymentRepository;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoanCalculationService {

  private final LoanCalculationRepository loanCalculationRepository;
  private final PaymentRepository paymentRepository;

  @Autowired
  public LoanCalculationService(LoanCalculationRepository loanCalculationRepository,
      PaymentRepository paymentRepository) {
    this.loanCalculationRepository = loanCalculationRepository;
    this.paymentRepository = paymentRepository;
  }

  @Transactional
  public void saveLoanCalculation(@NotNull LoanCalculation loanCalculation) {
    final LoanCalculation savedCalculation = loanCalculationRepository
        .saveAndFlush(loanCalculation);
    loanCalculation.getPayments().forEach(paymentRepository::saveAndFlush);
    log.info("Loan calculation with id '{}' and {} payments persisted to repository",
        savedCalculation.getId(), savedCalculation.getPayments().size());
  }
}
