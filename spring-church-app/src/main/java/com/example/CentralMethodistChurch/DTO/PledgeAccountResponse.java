package com.example.CentralMethodistChurch.DTO;

import java.time.LocalDate;
import java.util.List;

public record PledgeAccountResponse(
        String familyId,
        boolean exists,
        boolean configured,
        long monthlyAmount,
        LocalDate pledgeStartDate,
        long pledgeDue,
        long pledgeCredit,
        LocalDate lastDepositDate,
        long lastDepositAmount,
        String recordedTransactionId,
        List<PledgeRateDto> rateHistory,
        List<PledgeTransactionDto> transactions) {}