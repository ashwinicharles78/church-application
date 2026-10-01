package com.example.CentralMethodistChurch.DTO;

import java.time.LocalDate;

public record PledgeTransactionDto(String transactionId, String name, LocalDate date, long amount) {}