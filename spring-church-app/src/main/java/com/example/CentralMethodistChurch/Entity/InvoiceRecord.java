package com.example.CentralMethodistChurch.Entity;
import jakarta.persistence.*;

@Entity
public class InvoiceRecord {
    @Id
    @GeneratedValue
    private Long id;
    @Lob
    private byte[] pdfData;
    @OneToOne
    private FamilySubscriptions subscription;
}