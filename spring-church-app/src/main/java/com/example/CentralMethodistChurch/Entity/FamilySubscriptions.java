/**
 * @author Ashwini Charles on 3/3/2024
 * @project CentralMethodistChurch
 */
package com.example.CentralMethodistChurch.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class FamilySubscriptions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long familyId;

    @OneToMany
    private List<FamilyMember> members;

    private String headMemberId;

    private long pledgeAmount;

    private long pledgeCredit;

    private long pledgeDue;

    private LocalDate pledgeStartDate;

    private LocalDate lastPledgeDepositDate;

    private long lastPledgeDepositAmount;
}
