package com.example.CentralMethodistChurch.Entity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class InvoiceRecord {
    @Id
    @GeneratedValue
    private Long id;
    @Lob
    private byte[] pdfData;
    @OneToOne
    private FamilySubscriptions subscription;
    private LocalDate date;
    private long amount;
}