package com.example.CentralMethodistChurch.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
public class FamilySubscriptions {

    @Id
    private String familyId;

    // 1. Changed List to Set to prevent duplicate references in memory
    // 2. mappedBy corresponds to the 'familySubscription' field in FamilyMember
    @OneToMany(mappedBy = "familySubscription", orphanRemoval = true)
    @JsonManagedReference("subscription-members")
    private Set<FamilyMember> members = new HashSet<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<PaymentTransactionEntry> paymentTransactionEntries;
    private String headMemberId;
    private long pledgeAmount;
    private long pledgeCredit;
    private long pledgeDue;
    private long lastPledgeDue;
    private LocalDate pledgeStartDate;
    private LocalDate lastPledgeDepositDate;
    private long lastPledgeDepositAmount;

    /**
     * Helper method to maintain bidirectional relationship integrity.
     * This prevents inconsistencies that lead to duplicate entries.
     */
    public void addMember(FamilyMember member) {
        this.members.add(member);
        member.setFamilySubscription(this);
    }

    /**
     * Helper method to safely remove members.
     */
    public void removeMember(FamilyMember member) {
        this.members.remove(member);
        member.setFamilySubscription(null);
    }
}