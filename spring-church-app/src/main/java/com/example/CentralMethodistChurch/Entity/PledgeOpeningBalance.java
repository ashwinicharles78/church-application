package com.example.CentralMethodistChurch.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "pledge_opening_balance")
@Getter
@Setter
@NoArgsConstructor
public class PledgeOpeningBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_id", nullable = false, length = 255)
    private String familyId;

    @Column(name = "opening_due", nullable = false)
    private long openingDue;

    @Column(name = "opening_credit", nullable = false)
    private long openingCredit;

    @Column(name = "as_of_date", nullable = false)
    private LocalDate asOfDate;
}