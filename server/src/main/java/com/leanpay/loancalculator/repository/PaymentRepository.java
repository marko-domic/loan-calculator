package com.leanpay.loancalculator.repository;

import com.leanpay.loancalculator.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

}
