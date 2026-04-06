package com.example.CentralMethodistChurch.Service;

import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;
import com.example.CentralMethodistChurch.Entity.PaymentTransactionEntry;

public interface PaymentTransactionService {

    PaymentTransactionEntry createTransactionForPledge(FamilySubscriptions familySubscriptions);
}
