package com.example.CentralMethodistChurch.DTO;

import java.time.LocalDate;

public record PledgeSetupRequest(
        String mode,
        long monthlyAmount,
        LocalDate pledgeStartDate,
        long openingDue,
        long openingCredit,
        LocalDate asOfDate) {}