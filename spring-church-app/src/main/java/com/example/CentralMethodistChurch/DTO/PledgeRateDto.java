package com.example.CentralMethodistChurch.DTO;

import java.time.LocalDate;

public record PledgeRateDto(long monthlyAmount, LocalDate effectiveDate) {}