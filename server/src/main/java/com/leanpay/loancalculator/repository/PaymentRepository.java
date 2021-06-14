package com.leanpay.loancalculator.repository;

import com.leanpay.loancalculator.model.LoanCalculation;
import com.leanpay.loancalculator.model.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

  List<Payment> findByCalculationOrderByPaymentOrder(LoanCalculation calculation);
}
