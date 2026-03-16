package com.example.CentralMethodistChurch.Service.Impl;

import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;
import com.example.CentralMethodistChurch.Repository.FamilySubscribtionRepository;
import com.example.CentralMethodistChurch.Service.FamilyPledge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Service
public class FamilyPledgeImpl implements FamilyPledge {

    @Autowired
    FamilySubscribtionRepository familySubscribtionRepository;

    @Override
    public FamilySubscriptions calculatePledge(FamilySubscriptions subscription) {
        long amountDue = 0;
        if (subscription.getLastPledgeDepositDate() != null && subscription.getLastPledgeDue() <= 0L) {
            amountDue = subscription.getPledgeAmount() * calculateMonthsUntilNow(subscription.getLastPledgeDepositDate());
        } else {
            amountDue = subscription.getPledgeDue() + subscription.getLastPledgeDue();
        }
        subscription.setPledgeAmount(amountDue);
        return subscription;
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

    public FamilySubscriptions fetchById(String id) {
        return familySubscribtionRepository.findById(id).isPresent() ? familySubscribtionRepository.findById(id).get() : null;
    }
}