package com.example.CentralMethodistChurch.Service;

import com.example.CentralMethodistChurch.DTO.PledgeAccountResponse;
import com.example.CentralMethodistChurch.DTO.PledgePaymentRequest;
import com.example.CentralMethodistChurch.DTO.PledgeResetRequest;
import com.example.CentralMethodistChurch.DTO.PledgeSetupRequest;
import com.example.CentralMethodistChurch.DTO.PledgeTermsRequest;
import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;
import com.example.CentralMethodistChurch.Entity.PaymentTransactionEntry;

public interface FamilyPledge {
    PledgeAccountResponse getAccount(String familyId);
    PledgeAccountResponse setupAccount(String familyId, PledgeSetupRequest request);
    PledgeAccountResponse changeTerms(String familyId, PledgeTermsRequest request);
    PledgeAccountResponse recordPayment(String familyId, PledgePaymentRequest request);
    PledgeAccountResponse resetAccount(String familyId, PledgeResetRequest request);
    FamilySubscriptions fetchForInvoice(String familyId);
    PaymentTransactionEntry fetchTransaction(String familyId, String transactionId);
}
