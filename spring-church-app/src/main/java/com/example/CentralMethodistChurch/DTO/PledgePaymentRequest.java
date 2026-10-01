package com.example.CentralMethodistChurch.DTO;

import java.time.LocalDate;

public record PledgePaymentRequest(long amount, LocalDate paymentDate) {}