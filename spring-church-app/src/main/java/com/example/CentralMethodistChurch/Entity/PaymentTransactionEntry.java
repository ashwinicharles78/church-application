package com.example.CentralMethodistChurch.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PaymentTransactionEntry {

    // FIX 2: Correct ID generation for a String (generates a UUID)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String transaction_id;

    private long amount;
    private String name;
    private LocalDate date;

    // FIX 1: Add the missing relationship back to FamilySubscriptions
    // The variable name "member" matches your mappedBy = "member" in FamilySubscriptions
    @ManyToOne
    @JoinColumn(name = "family_id")
    @JsonIgnore
    private FamilySubscriptions member;
}