package com.example.CentralMethodistChurch.Service.Impl;

import com.example.CentralMethodistChurch.Entity.PaymentTransactionEntry;
import com.example.CentralMethodistChurch.Entity.PledgeOpeningBalance;
import com.example.CentralMethodistChurch.Entity.PledgeRateChange;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PledgeBalanceCalculatorTest {

    @Test
    void accruesWholeMonthlyAnniversariesFromStartDate() {
        var rate = rate(100, LocalDate.of(2026, 1, 15));

        var beforeAnniversary = PledgeBalanceCalculator.calculate(
                LocalDate.of(2026, 1, 15), null, List.of(rate), List.of(), LocalDate.of(2026, 4, 14));
        var onAnniversary = PledgeBalanceCalculator.calculate(
                LocalDate.of(2026, 1, 15), null, List.of(rate), List.of(), LocalDate.of(2026, 4, 15));

        assertEquals(new PledgeBalanceCalculator.Balance(200, 0), beforeAnniversary);
        assertEquals(new PledgeBalanceCalculator.Balance(300, 0), onAnniversary);
    }

    @Test
    void appliesRateChangesOnTheirEffectiveBillingAnniversary() {
        var rates = List.of(
                rate(100, LocalDate.of(2026, 1, 15)),
                rate(150, LocalDate.of(2026, 3, 15)));

        var balance = PledgeBalanceCalculator.calculate(
                LocalDate.of(2026, 1, 15), null, rates, List.of(), LocalDate.of(2026, 4, 15));

        assertEquals(new PledgeBalanceCalculator.Balance(400, 0), balance);
    }

    @Test
    void appliesOpeningBalancesAndPaymentsWithoutAllowingNegativeBalances() {
        var opening = opening(250, 50, LocalDate.of(2026, 1, 15));
        var payment = payment(300, LocalDate.of(2026, 2, 15));
        var rate = rate(100, LocalDate.of(2026, 1, 15));

        var balance = PledgeBalanceCalculator.calculate(
                LocalDate.of(2026, 1, 15), opening, List.of(rate), List.of(payment), LocalDate.of(2026, 2, 15));

        assertEquals(new PledgeBalanceCalculator.Balance(0, 0), balance);
    }

    @Test
    void carriesOverpaymentAsCreditAndAppliesItToFutureDues() {
        var opening = opening(100, 0, LocalDate.of(2026, 1, 15));
        var payment = payment(250, LocalDate.of(2026, 1, 20));
        var rate = rate(100, LocalDate.of(2026, 1, 15));

        var afterFirstMonth = PledgeBalanceCalculator.calculate(
                LocalDate.of(2026, 1, 15), opening, List.of(rate), List.of(payment), LocalDate.of(2026, 2, 15));
        var afterSecondMonth = PledgeBalanceCalculator.calculate(
                LocalDate.of(2026, 1, 15), opening, List.of(rate), List.of(payment), LocalDate.of(2026, 3, 15));

        assertEquals(new PledgeBalanceCalculator.Balance(0, 50), afterFirstMonth);
        assertEquals(new PledgeBalanceCalculator.Balance(50, 0), afterSecondMonth);
    }

    private PledgeRateChange rate(long amount, LocalDate effectiveDate) {
        var rate = new PledgeRateChange();
        rate.setMonthlyAmount(amount);
        rate.setEffectiveDate(effectiveDate);
        return rate;
    }

    private PledgeOpeningBalance opening(long due, long credit, LocalDate asOfDate) {
        var opening = new PledgeOpeningBalance();
        opening.setOpeningDue(due);
        opening.setOpeningCredit(credit);
        opening.setAsOfDate(asOfDate);
        return opening;
    }

    private PaymentTransactionEntry payment(long amount, LocalDate date) {
        var payment = new PaymentTransactionEntry();
        payment.setAmount(amount);
        payment.setDate(date);
        return payment;
    }
}