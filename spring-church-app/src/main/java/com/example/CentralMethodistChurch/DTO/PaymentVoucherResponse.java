package com.example.CentralMethodistChurch.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PaymentVoucherResponse(
        String serialNumber,
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
        long netAmount,
        LocalDate paymentDate,
        String receiptMethod,
        String receiptReferenceNumber,
        LocalDate receiptDate,
        Long receiptAmount,
        String paidBy,
        LocalDateTime createdAt) {}