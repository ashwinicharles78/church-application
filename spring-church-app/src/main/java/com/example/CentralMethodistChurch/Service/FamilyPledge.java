package com.example.CentralMethodistChurch.Service;

import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;
import com.example.CentralMethodistChurch.Entity.PaymentTransactionEntry;
import jakarta.transaction.Transactional;

public interface FamilyPledge {
    public FamilySubscriptions fetchById(String id);
    public void calculatePledge(FamilySubscriptions subscription);
    FamilySubscriptions saveSubscription(FamilySubscriptions subscription);
    @Transactional
    FamilySubscriptions updateSubscriptionManual(String id, FamilySubscriptions incomingData, PaymentTransactionEntry entry);
}
