package com.example.CentralMethodistChurch.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_voucher")
@Getter
@Setter
@NoArgsConstructor
public class PaymentVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "serial_number", nullable = false, unique = true, length = 32)
    private String serialNumber;

    @Column(name = "voucher_date", nullable = false)
    private LocalDate voucherDate;

    @Column(name = "approved_amount", nullable = false)
    private long approvedAmount;

    @Column(name = "amount_in_words", nullable = false, length = 500)
    private String amountInWords;

    @Column(nullable = false, length = 500)
    private String purpose;

    @Column(nullable = false, length = 255)
    private String payee;

    @Column(name = "bill_number", length = 100)
    private String billNumber;

    @Column(name = "bill_date")
    private LocalDate billDate;

    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod;

    @Column(name = "payment_reference_number", length = 100)
    private String paymentReferenceNumber;

    @Column(name = "payment_reference_date")
    private LocalDate paymentReferenceDate;

    @Column(name = "total_bills_amount", nullable = false)
    private long totalBillsAmount;

    @Column(name = "advance_amount", nullable = false)
    private long advanceAmount;

    @Column(name = "net_amount", nullable = false)
    private long netAmount;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "receipt_method", length = 20)
    private String receiptMethod;

    @Column(name = "receipt_reference_number", length = 100)
    private String receiptReferenceNumber;

    @Column(name = "receipt_date")
    private LocalDate receiptDate;

    @Column(name = "receipt_amount")
    private Long receiptAmount;

    @Column(name = "paid_by", length = 255)
    private String paidBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void setCreatedAt() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}