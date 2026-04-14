package com.example.CentralMethodistChurch.Service.Impl;

import com.example.CentralMethodistChurch.Entity.FamilyMember;
import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;
import com.example.CentralMethodistChurch.Entity.PaymentTransactionEntry;
import com.example.CentralMethodistChurch.Repository.FamilySubscribtionRepository;
import com.example.CentralMethodistChurch.Service.FamilyPledge;
import com.example.CentralMethodistChurch.Service.PaymentTransactionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
public class FamilyPledgeImpl implements FamilyPledge {

    @Autowired
    FamilySubscribtionRepository familySubscribtionRepository;

    @Autowired
    PaymentTransactionService paymentTransactionService;

    @Override
    public void calculatePledge(FamilySubscriptions subscription, Long credit) {

        // ── Step 1: Recalculate pledgeDue from scratch ONLY when slate is clean ──
        // Conditions: a prior deposit exists, nothing is owed, and no credit is pending
        if (subscription.getLastPledgeDepositDate() != null
                && subscription.getPledgeDue() <= 0L
                && credit <= 0L) {

            long months = calculateMonthsUntilNow(subscription.getLastPledgeDepositDate());
            long freshDue = subscription.getPledgeAmount() * months;

            subscription.setLastPledgeDue(freshDue);
            subscription.setPledgeDue(freshDue);
            subscription.setLastPledgeDepositDate(LocalDate.now()); // anchor for next cycle
        }

        // ── Step 2: Apply credit if any exists (runs independently of Step 1) ──
        // This handles credit that arrived in the same cycle as a fresh calculation
        // or credit carried in from a previous cycle
        if (credit > 0L) {

            long due    = subscription.getPledgeDue();

            if (credit >= due) {
                // Credit fully covers the due amount — carry leftover forward
                long leftoverCredit = credit - due;
                subscription.setPledgeDue(0L);
                subscription.setLastPledgeDue(0L);
                subscription.setPledgeCredit(leftoverCredit); // 0 if exact, positive if excess
            } else {
                // Credit partially covers — subtract and zero out credit
                long remaining = due - credit;
                subscription.setPledgeDue(remaining);
                subscription.setLastPledgeDue(remaining);
                subscription.setPledgeCredit(0L);             // fully consumed
            }

            subscription.setLastPledgeDepositDate(LocalDate.now());
        }
    }

    public static long calculateMonthsUntilNow(LocalDate dateString) {

        try {

            // 3. Get the current date
            LocalDate now = LocalDate.now();

            // 4. Calculate total months elapsed
            return ChronoUnit.MONTHS.between(dateString, now);

        } catch (DateTimeParseException e) {
            // Handle cases where the input string format is incorrect
            System.err.println("Invalid date format: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public FamilySubscriptions fetchById(String id) {
        return familySubscribtionRepository.findById(id).isPresent() ? familySubscribtionRepository.findById(id).get() : null;
    }

    @Override
    public FamilySubscriptions saveSubscription(FamilySubscriptions subscription) {
        return familySubscribtionRepository.save(subscription);
    }

    @Transactional
    @Override
    public FamilySubscriptions updateSubscriptionManual(String id, FamilySubscriptions incomingData, PaymentTransactionEntry entry) {
        // 1. Fetch the persistent entity from DB
        FamilySubscriptions existingSub = familySubscribtionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found"));
        incomingData.setMembers(existingSub.getMembers());

        // 2. Update basic fields
        existingSub.setPledgeAmount(incomingData.getPledgeAmount());
        existingSub.setPledgeCredit(incomingData.getPledgeCredit());
        existingSub.setLastPledgeDue(incomingData.getLastPledgeDue());
        existingSub.setPledgeStartDate(incomingData.getPledgeStartDate());
        existingSub.setLastPledgeDepositDate(incomingData.getLastPledgeDepositDate());
        existingSub.setLastPledgeDepositAmount(incomingData.getLastPledgeDepositAmount());
        if(Objects.isNull(entry)) calculatePledge(existingSub, incomingData.getPledgeCredit());

        if(Objects.nonNull(entry)){
            existingSub.addPaymentTransactionEntry(entry);
        }

        // 3. Sync the Members Collection manually
        // We modify the EXISTING set rather than replacing it
        if (incomingData.getMembers() != null) {
            // Remove members that are no longer in the incoming list
            existingSub.getMembers().removeIf(existingMember ->
                    incomingData.getMembers().stream()
                            .noneMatch(incomingMember -> incomingMember.getMembershipId() == existingMember.getMembershipId())
            );

            // Add or Update members from the incoming list
            for (FamilyMember incomingMember : incomingData.getMembers()) {
                boolean exists = existingSub.getMembers().stream()
                        .anyMatch(m -> m.getMembershipId() == incomingMember.getMembershipId());

                if (!exists) {
                    // This is a new member being added to the subscription
                    incomingMember.setFamilySubscription(existingSub);
                    existingSub.getMembers().add(incomingMember);
                }
            }
        } else {
            existingSub.getMembers().clear();
        }

        return familySubscribtionRepository.save(existingSub);
    }
}