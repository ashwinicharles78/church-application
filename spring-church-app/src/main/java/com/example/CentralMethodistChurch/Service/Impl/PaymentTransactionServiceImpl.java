package com.example.CentralMethodistChurch.Service.Impl;

import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;
import com.example.CentralMethodistChurch.Entity.PaymentTransactionEntry;
import com.example.CentralMethodistChurch.Repository.PaymentTransactionRepository;
import com.example.CentralMethodistChurch.Service.PaymentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
public class PaymentTransactionServiceImpl implements PaymentTransactionService {

    @Autowired
    PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public PaymentTransactionEntry createTransactionForPledge(FamilySubscriptions familySubscriptions, Long credit) {
        if (credit <= 0) {
            PaymentTransactionEntry paymentTransactionEntry = new PaymentTransactionEntry();
            paymentTransactionEntry.setAmount(credit);
            paymentTransactionEntry.setName(familySubscriptions.getMembers().stream()
                    .filter(member -> String.valueOf(member.getMembershipId()).equals(familySubscriptions.getHeadMemberId()))
                    .map(familyMember -> familyMember.getFirstName() + " " + familyMember.getLastName()).collect(Collectors.joining())
            );
            paymentTransactionEntry.setMember(familySubscriptions);
            paymentTransactionEntry.setDate(LocalDate.now());
            return paymentTransactionRepository.save(paymentTransactionEntry);
        }
        return null;
    }
}
