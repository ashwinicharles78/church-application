package com.example.CentralMethodistChurch.Service.Impl;

import com.example.CentralMethodistChurch.DTO.PledgeResetRequest;
import com.example.CentralMethodistChurch.Entity.FamilyMember;
import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;
import com.example.CentralMethodistChurch.Entity.PaymentTransactionEntry;
import com.example.CentralMethodistChurch.Entity.PledgeOpeningBalance;
import com.example.CentralMethodistChurch.Entity.PledgeRateChange;
import com.example.CentralMethodistChurch.Repository.FamilySubscribtionRepository;
import com.example.CentralMethodistChurch.Repository.MemberRepository;
import com.example.CentralMethodistChurch.Repository.PaymentTransactionRepository;
import com.example.CentralMethodistChurch.Repository.PledgeOpeningBalanceRepository;
import com.example.CentralMethodistChurch.Repository.PledgeRateChangeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FamilyPledgeResetTest {

    @Mock FamilySubscribtionRepository subscriptions;
    @Mock MemberRepository members;
    @Mock PaymentTransactionRepository transactions;
    @Mock PledgeRateChangeRepository rates;
    @Mock PledgeOpeningBalanceRepository openingBalances;

    @Test
    void resetRequiresExactFamilySpecificConfirmation() {
        FamilyPledgeImpl service = service();

        assertThrows(ResponseStatusException.class,
                () -> service.resetAccount("F-1", new PledgeResetRequest("RESET F-2")));

        verify(subscriptions, never()).findById("F-1");
        verify(transactions, never()).deleteAll(org.mockito.ArgumentMatchers.<PaymentTransactionEntry>anyList());
        verify(rates, never()).deleteAll(org.mockito.ArgumentMatchers.<PledgeRateChange>anyList());
    }

    @Test
    void resetDeletesOnlyThatFamilyLedgerAndKeepsSubscriptionMembers() {
        FamilyPledgeImpl service = service();
        FamilySubscriptions subscription = new FamilySubscriptions();
        subscription.setFamilyId("F-1");
        subscription.setPledgeAmount(500L);
        subscription.setPledgeCredit(100L);
        subscription.setPledgeDue(400L);
    subscription.setLastPledgeDue(400L);
    subscription.setPledgeStartDate(java.time.LocalDate.of(2026, 1, 1));

    FamilyMember member = new FamilyMember();
    member.setFirstName("Ari");
    subscription.addMember(member);

    PaymentTransactionEntry payment = new PaymentTransactionEntry();
    payment.setAmount(100L);
    payment.setMember(subscription);
    subscription.addPaymentTransactionEntry(payment);

    PledgeRateChange rate = new PledgeRateChange();
    PledgeOpeningBalance opening = new PledgeOpeningBalance();
    when(subscriptions.findById("F-1")).thenReturn(Optional.of(subscription));
    when(transactions.findByMember_FamilyIdOrderByDateAsc("F-1"))
        .thenReturn(List.of(payment), List.of());
    when(rates.findByFamilyIdOrderByEffectiveDateAscIdAsc("F-1"))
        .thenReturn(List.of(rate), List.of());
    when(openingBalances.findFirstByFamilyIdOrderByIdAsc("F-1"))
        .thenReturn(Optional.of(opening), Optional.empty());

    var result = service.resetAccount("F-1", new PledgeResetRequest("RESET F-1"));

    assertEquals(0L, subscription.getPledgeAmount());
    assertEquals(0L, subscription.getPledgeCredit());
    assertEquals(0L, subscription.getPledgeDue());
    assertEquals(0L, subscription.getLastPledgeDue());
    assertNull(subscription.getPledgeStartDate());
    assertEquals(1, subscription.getMembers().size());
    assertFalse(result.configured());
    assertEquals("F-1", result.familyId());

    verify(transactions).deleteAll(List.of(payment));
    verify(rates).deleteAll(List.of(rate));
    verify(openingBalances).delete(opening);
    verify(subscriptions).save(subscription);
    }

    private FamilyPledgeImpl service() {
    return new FamilyPledgeImpl(subscriptions, members, transactions, rates, openingBalances);
    }
}
