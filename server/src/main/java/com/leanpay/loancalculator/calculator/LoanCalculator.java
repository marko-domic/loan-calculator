package com.leanpay.loancalculator.calculator;

import com.leanpay.loancalculator.converter.LoanCalculationConverter;
import com.leanpay.loancalculator.dto.AmortizationScheduleCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationDto;
import com.leanpay.loancalculator.dto.LoanCalculationInputDto;
import com.leanpay.loancalculator.dto.PaymentDto;
import com.leanpay.loancalculator.service.LoanCalculationService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoanCalculator {

  private static final BigDecimal HUNDRED_PERCENT = BigDecimal.valueOf(100);
  private static final BigDecimal NUMBER_OF_MONTHS_PER_RATE = BigDecimal.valueOf(12);

  private final int calculationScale;
  private final int displayScale;
  private final LoanCalculationService loanCalculationService;
  private final LoanCalculationConverter loanCalculationConverter;

  @Autowired
  public LoanCalculator(@Value("${calculator.rounding.calculation.scale}") int calculationScale,
      @Value("${calculator.rounding.display.scale}") int displayScale,
      LoanCalculationService loanCalculationService,
      LoanCalculationConverter loanCalculationConverter) {
    this.calculationScale = calculationScale;
    this.displayScale = displayScale;
    this.loanCalculationService = loanCalculationService;
    this.loanCalculationConverter = loanCalculationConverter;
  }

  public LoanCalculationDto calculateLoan(LoanCalculationInputDto loanCalculationInput) {
    final AmortizationScheduleCalculationDto loanCalculation = calculateLoanDetails(
        loanCalculationInput);
    loanCalculationService.saveLoanCalculation(
        loanCalculationConverter.toModel(loanCalculationInput, loanCalculation));
    return new LoanCalculationDto(loanCalculation.getMonthlyPayment(),
        loanCalculation.getTotalInterestPaid());
  }

  public AmortizationScheduleCalculationDto calculateLoanWithAmortizationSchedule(
      LoanCalculationInputDto loanCalculationInput) {
    final AmortizationScheduleCalculationDto loanCalculation = calculateLoanDetails(
        loanCalculationInput);
    loanCalculationService.saveLoanCalculation(
        loanCalculationConverter.toModel(loanCalculationInput, loanCalculation));
    return loanCalculation;
  }

  private AmortizationScheduleCalculationDto calculateLoanDetails(
      LoanCalculationInputDto loanCalculationInputDto) {

    final BigDecimal amount = loanCalculationInputDto.getAmount();
    final BigDecimal interestRate = loanCalculationInputDto.getAnnualInterestPercent();
    final int numberOfMonths = loanCalculationInputDto.getNumberOfMonths();
    final List<PaymentDto> payments = new ArrayList<>(numberOfMonths);

    final BigDecimal monthlyPayment = BigDecimal.ZERO.compareTo(interestRate) == 0 ?
        amount.divide(BigDecimal.valueOf(numberOfMonths), displayScale, RoundingMode.HALF_DOWN) :
        calculateMonthlyPayment(loanCalculationInputDto);
    log.info("Monthly payment calculated: {}", monthlyPayment);

    IntStream.range(0, numberOfMonths).forEach(monthIndex -> payments.add(
        calculatePaymentForSpecificMonth(monthIndex, payments, loanCalculationInputDto,
            monthlyPayment)));

    final BigDecimal totalInterest = calculateTotalInterest(payments);
    log.info("Total interest calculated: {}", totalInterest);

    return new AmortizationScheduleCalculationDto(monthlyPayment, totalInterest, payments);
  }

  private BigDecimal calculateMonthlyPayment(LoanCalculationInputDto loanCalculationInputDto) {

    final int numberOfMonths = loanCalculationInputDto.getNumberOfMonths();
    final BigDecimal loanAmount = loanCalculationInputDto.getAmount();
    final BigDecimal monthlyRate = calculateMonthlyInterestRate(
        loanCalculationInputDto.getAnnualInterestPercent());
    final BigDecimal updatedMonthlyRate = BigDecimal.ONE.add(monthlyRate).pow(numberOfMonths);

    final BigDecimal numerator = loanAmount.multiply(monthlyRate).multiply(updatedMonthlyRate);
    final BigDecimal denominator = updatedMonthlyRate.subtract(BigDecimal.ONE);

    return numerator.divide(denominator, displayScale, RoundingMode.HALF_DOWN);
  }

  private PaymentDto calculatePaymentForSpecificMonth(int monthIndex, List<PaymentDto> payments,
      LoanCalculationInputDto loanCalculationInputDto, BigDecimal monthlyPayment) {
    final BigDecimal monthlyRate = calculateMonthlyInterestRate(
        loanCalculationInputDto.getAnnualInterestPercent());

    final PaymentDto previousPayment = monthIndex > 0 ? payments.get(monthIndex - 1) : null;
    final BigDecimal previousBalance =
        previousPayment != null ? previousPayment.getBalanceOwed()
            : loanCalculationInputDto.getAmount();
    final BigDecimal interestAmount = previousBalance.multiply(monthlyRate)
        .setScale(displayScale, RoundingMode.HALF_EVEN);
    final BigDecimal principalAmount =
        monthIndex < loanCalculationInputDto.getNumberOfMonths() - 1 ? monthlyPayment
            .subtract(interestAmount).setScale(displayScale, RoundingMode.HALF_EVEN)
            : previousBalance;
    final BigDecimal payment = principalAmount.add(interestAmount)
        .setScale(displayScale, RoundingMode.HALF_EVEN);
    final BigDecimal newBalance = previousBalance.subtract(principalAmount)
        .setScale(displayScale, RoundingMode.HALF_EVEN);
    return new PaymentDto(monthIndex + 1, payment, principalAmount, interestAmount, newBalance);
  }

  private BigDecimal calculateTotalInterest(List<PaymentDto> payments) {
    return payments.stream().map(PaymentDto::getInterestAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(displayScale, RoundingMode.HALF_EVEN);
  }

  private BigDecimal calculateMonthlyInterestRate(BigDecimal interestPercent) {
    final BigDecimal interestRate = interestPercent
        .divide(HUNDRED_PERCENT, calculationScale, RoundingMode.HALF_EVEN);
    return interestRate.divide(NUMBER_OF_MONTHS_PER_RATE, calculationScale, RoundingMode.HALF_EVEN);
  }
}
