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
@Table(name = "pledge_rate_change")
@Getter
@Setter
@NoArgsConstructor
public class PledgeRateChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_id", nullable = false, length = 255)
    private String familyId;

    @Column(name = "monthly_amount", nullable = false)
    private long monthlyAmount;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;
}