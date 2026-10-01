package com.example.CentralMethodistChurch.Service.Impl;

import com.example.CentralMethodistChurch.Entity.PaymentTransactionEntry;
import com.example.CentralMethodistChurch.Entity.PledgeOpeningBalance;
import com.example.CentralMethodistChurch.Entity.PledgeRateChange;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public final class PledgeBalanceCalculator {

    private PledgeBalanceCalculator() {}

    public static Balance calculate(
            LocalDate pledgeStartDate,
            PledgeOpeningBalance openingBalance,
            List<PledgeRateChange> rateChanges,
            List<PaymentTransactionEntry> payments,
            LocalDate asOfDate) {
        LocalDate baselineDate = openingBalance == null
                ? pledgeStartDate
                : openingBalance.getAsOfDate();
        long due = openingBalance == null ? 0L : openingBalance.getOpeningDue();
        long credit = openingBalance == null ? 0L : openingBalance.getOpeningCredit();

        if (credit >= due) {
            credit -= due;
            due = 0L;
        } else {
            due -= credit;
            credit = 0L;
        }

        List<PaymentTransactionEntry> orderedPayments = payments.stream()
                .filter(payment -> payment.getDate() != null
                        && payment.getDate().isAfter(baselineDate)
                        && !payment.getDate().isAfter(asOfDate)
                        && payment.getAmount() > 0L)
                .sorted(Comparator.comparing(PaymentTransactionEntry::getDate))
                .toList();
        int paymentIndex = 0;

        for (int month = 1; ; month++) {
            LocalDate dueDate = pledgeStartDate.plusMonths(month);
            if (dueDate.isAfter(asOfDate)) break;

            while (paymentIndex < orderedPayments.size()
                    && orderedPayments.get(paymentIndex).getDate().isBefore(dueDate)) {
                long[] balance = applyPayment(due, credit, orderedPayments.get(paymentIndex++).getAmount());
                due = balance[0];
                credit = balance[1];
            }

            if (dueDate.isAfter(baselineDate)) {
                long monthlyAmount = rateChanges.stream()
                        .filter(rate -> rate.getEffectiveDate() != null
                                && !rate.getEffectiveDate().isAfter(dueDate))
                        .max(Comparator.comparing(PledgeRateChange::getEffectiveDate)
                                .thenComparing(PledgeRateChange::getId, Comparator.nullsFirst(Long::compareTo)))
                        .map(PledgeRateChange::getMonthlyAmount)
                        .orElse(0L);

                if (credit >= monthlyAmount) {
                    credit -= monthlyAmount;
                } else {
                    due = Math.addExact(due, monthlyAmount - credit);
                    credit = 0L;
                }
            }

            while (paymentIndex < orderedPayments.size()
                    && orderedPayments.get(paymentIndex).getDate().equals(dueDate)) {
                long[] balance = applyPayment(due, credit, orderedPayments.get(paymentIndex++).getAmount());
                due = balance[0];
                credit = balance[1];
            }
        }

        while (paymentIndex < orderedPayments.size()) {
            long[] balance = applyPayment(due, credit, orderedPayments.get(paymentIndex++).getAmount());
            due = balance[0];
            credit = balance[1];
        }

        return new Balance(due, credit);
    }

    private static long[] applyPayment(long due, long credit, long amount) {
        if (amount >= due) {
            return new long[]{0L, Math.addExact(credit, amount - due)};
        }
        return new long[]{due - amount, credit};
    }

    public record Balance(long due, long credit) {}
}