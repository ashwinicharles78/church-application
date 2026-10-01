package com.example.CentralMethodistChurch.DTO;

import java.time.LocalDate;

public record PledgeTermsRequest(long monthlyAmount, LocalDate effectiveDate) {}