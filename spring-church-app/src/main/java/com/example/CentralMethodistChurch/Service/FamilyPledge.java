package com.example.CentralMethodistChurch.Service;

import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;

public interface FamilyPledge {
    public FamilySubscriptions fetchById(String id);
    public FamilySubscriptions calculatePledge(FamilySubscriptions subscription);
    FamilySubscriptions saveSubscription(FamilySubscriptions subscription);
}
