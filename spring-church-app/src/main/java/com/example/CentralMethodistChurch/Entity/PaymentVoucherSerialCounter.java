package com.example.CentralMethodistChurch.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payment_voucher_serial_counter")
@Getter
@Setter
@NoArgsConstructor
public class PaymentVoucherSerialCounter {

    @Id
    @Column(name = "voucher_year")
    private Integer voucherYear;

    @Column(name = "next_number", nullable = false)
    private long nextNumber;
}