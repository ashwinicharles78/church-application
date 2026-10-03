package com.example.CentralMethodistChurch.DTO;

import java.time.LocalDate;

public record PaymentVoucherRequest(
        LocalDate voucherDate,
        long approvedAmount,
        String amountInWords,
        String purpose,
        String payee,
        String billNumber,
        LocalDate billDate,
        String paymentMethod,
        String paymentReferenceNumber,
        LocalDate paymentReferenceDate,
        long totalBillsAmount,
        long advanceAmount,
        LocalDate paymentDate,
        String receiptMethod,
        String receiptReferenceNumber,
        LocalDate receiptDate,
        Long receiptAmount,
        String paidBy) {}