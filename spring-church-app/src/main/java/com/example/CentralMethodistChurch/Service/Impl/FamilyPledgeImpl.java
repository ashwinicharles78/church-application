package com.example.CentralMethodistChurch.Service.Impl;

import com.example.CentralMethodistChurch.DTO.PledgeAccountResponse;
import com.example.CentralMethodistChurch.DTO.PledgePaymentRequest;
import com.example.CentralMethodistChurch.DTO.PledgeRateDto;
import com.example.CentralMethodistChurch.DTO.PledgeResetRequest;
import com.example.CentralMethodistChurch.DTO.PledgeSetupRequest;
import com.example.CentralMethodistChurch.DTO.PledgeTermsRequest;
import com.example.CentralMethodistChurch.DTO.PledgeTransactionDto;
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
import com.example.CentralMethodistChurch.Service.FamilyPledge;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class FamilyPledgeImpl implements FamilyPledge {

    private final FamilySubscribtionRepository subscriptions;
    private final MemberRepository members;
    private final PaymentTransactionRepository transactions;
    private final PledgeRateChangeRepository rates;
    private final PledgeOpeningBalanceRepository openingBalances;

    public FamilyPledgeImpl(
            FamilySubscribtionRepository subscriptions,
            MemberRepository members,
            PaymentTransactionRepository transactions,
            PledgeRateChangeRepository rates,
            PledgeOpeningBalanceRepository openingBalances) {
        this.subscriptions = subscriptions;
        this.members = members;
        this.transactions = transactions;
        this.rates = rates;
        this.openingBalances = openingBalances;
    }

    @Override
    @Transactional
    public PledgeAccountResponse getAccount(String familyId) {
        FamilySubscriptions subscription = subscriptions.findById(familyId).orElse(null);
        if (subscription == null) return emptyAccount(familyId);
        if (isConfigured(familyId)) refreshBalance(subscription);
        return toResponse(subscription, true);
    }

    @Override
    @Transactional
    public PledgeAccountResponse setupAccount(String familyId, PledgeSetupRequest request) {
        validateSetup(request);
        if (openingBalances.findFirstByFamilyIdOrderByIdAsc(familyId).isPresent()
                || !rates.findByFamilyIdOrderByEffectiveDateAscIdAsc(familyId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pledge account is already configured.");
        }

        FamilySubscriptions subscription = subscriptions.findById(familyId).orElseGet(() -> {
            FamilySubscriptions created = new FamilySubscriptions();
            created.setFamilyId(familyId);
            return created;
        });

        boolean offline = "OFFLINE".equalsIgnoreCase(request.mode());
        LocalDate startDate = request.pledgeStartDate();
        LocalDate balanceDate = offline ? request.asOfDate() : startDate;
        long openingDue = offline ? request.openingDue() : 0L;
        long openingCredit = offline ? request.openingCredit() : 0L;

        if (offline && balanceDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Opening balance date cannot be before the pledge start date.");
        }
        if (!offline && (request.openingDue() != 0L || request.openingCredit() != 0L)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fresh subscriptions must start with zero opening balances.");
        }

        subscription.setPledgeStartDate(startDate);
        subscription.setPledgeAmount(request.monthlyAmount());
        subscription = subscriptions.save(subscription);
        syncFamilyMembers(familyId, subscription);
        subscription = subscriptions.save(subscription);

        PledgeRateChange initialRate = new PledgeRateChange();
        initialRate.setFamilyId(familyId);
        initialRate.setMonthlyAmount(request.monthlyAmount());
        initialRate.setEffectiveDate(startDate);
        rates.save(initialRate);

        PledgeOpeningBalance opening = new PledgeOpeningBalance();
        opening.setFamilyId(familyId);
        opening.setOpeningDue(openingDue);
        opening.setOpeningCredit(openingCredit);
        opening.setAsOfDate(balanceDate);
        openingBalances.save(opening);

        refreshBalance(subscription);
        return toResponse(subscription, true);
    }

    @Override
    @Transactional
    public PledgeAccountResponse changeTerms(String familyId, PledgeTermsRequest request) {
        FamilySubscriptions subscription = requireConfiguredSubscription(familyId);
        if (request == null || request.monthlyAmount() <= 0L || request.effectiveDate() == null
                || request.effectiveDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Enter a positive monthly amount and an effective date of today or later.");
        }

        LocalDate effectiveDate = nextBillingDate(subscription.getPledgeStartDate(), request.effectiveDate());
        PledgeRateChange rate = new PledgeRateChange();
        rate.setFamilyId(familyId);
        rate.setMonthlyAmount(request.monthlyAmount());
        rate.setEffectiveDate(effectiveDate);
        rates.save(rate);

        refreshBalance(subscription);
        return toResponse(subscription, true);
    }

    @Override
    @Transactional
    public PledgeAccountResponse recordPayment(String familyId, PledgePaymentRequest request) {
        FamilySubscriptions subscription = requireConfiguredSubscription(familyId);
        if (request == null || request.amount() <= 0L || request.paymentDate() == null
                || request.paymentDate().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Payment amount must be positive and payment date cannot be in the future.");
        }

        PledgeOpeningBalance opening = openingBalances.findFirstByFamilyIdOrderByIdAsc(familyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Pledge account has no opening balance."));
        if (request.paymentDate().isBefore(opening.getAsOfDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment date cannot be before the opening balance date.");
        }

        PaymentTransactionEntry transaction = new PaymentTransactionEntry();
        transaction.setAmount(request.amount());
        transaction.setDate(request.paymentDate());
        transaction.setName(familyHeadName(subscription));
        transaction.setMember(subscription);
        transactions.save(transaction);
        subscription.addPaymentTransactionEntry(transaction);

        refreshBalance(subscription);
        return toResponse(subscription, true, transaction.getTransaction_id());
    }

    @Override
    @Transactional
    public PledgeAccountResponse resetAccount(String familyId, PledgeResetRequest request) {
        if (request == null || !("RESET " + familyId).equals(request.confirmation())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Confirmation must exactly match RESET " + familyId + ".");
        }

        FamilySubscriptions subscription = subscriptions.findById(familyId)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found"));
        List<PaymentTransactionEntry> paymentHistory =
                transactions.findByMember_FamilyIdOrderByDateAsc(familyId);
        subscription.getPaymentTransactionEntries().removeAll(paymentHistory);
        transactions.deleteAll(paymentHistory);

        rates.deleteAll(rates.findByFamilyIdOrderByEffectiveDateAscIdAsc(familyId));
        openingBalances.findFirstByFamilyIdOrderByIdAsc(familyId).ifPresent(openingBalances::delete);

        subscription.setPledgeAmount(0L);
        subscription.setPledgeCredit(0L);
        subscription.setPledgeDue(0L);
        subscription.setLastPledgeDue(0L);
        subscription.setPledgeStartDate(null);
        subscription.setLastPledgeDepositDate(null);
        subscription.setLastPledgeDepositAmount(0L);
        subscriptions.save(subscription);

        return toResponse(subscription, true);
    }

    @Override
    @Transactional
    public FamilySubscriptions fetchForInvoice(String familyId) {
        FamilySubscriptions subscription = subscriptions.findById(familyId)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found"));
        if (isConfigured(familyId)) refreshBalance(subscription);
        return subscription;
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentTransactionEntry fetchTransaction(String familyId, String transactionId) {
        PaymentTransactionEntry transaction = transactions.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Payment transaction not found"));
        if (transaction.getMember() == null || !familyId.equals(transaction.getMember().getFamilyId())) {
            throw new EntityNotFoundException("Payment transaction not found");
        }
        return transaction;
    }

    private void validateSetup(PledgeSetupRequest request) {
        if (request == null || request.pledgeStartDate() == null || request.asOfDate() == null
                || request.monthlyAmount() <= 0L || request.openingDue() < 0L || request.openingCredit() < 0L
                || !("FRESH".equalsIgnoreCase(request.mode()) || "OFFLINE".equalsIgnoreCase(request.mode()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Provide a valid setup mode, start date, positive monthly amount, and nonnegative opening balances.");
        }
        if (request.asOfDate().isAfter(LocalDate.now()) || request.pledgeStartDate().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Setup dates cannot be in the future.");
        }
    }

    private FamilySubscriptions requireConfiguredSubscription(String familyId) {
        FamilySubscriptions subscription = subscriptions.findById(familyId)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found"));
        if (!isConfigured(familyId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Set up the pledge account before changing terms or recording payments.");
        }
        return subscription;
    }

    private boolean isConfigured(String familyId) {
        return openingBalances.findFirstByFamilyIdOrderByIdAsc(familyId).isPresent()
                && !rates.findByFamilyIdOrderByEffectiveDateAscIdAsc(familyId).isEmpty();
    }

    private void syncFamilyMembers(String familyId, FamilySubscriptions subscription) {
        List<FamilyMember> familyMembers = members.findAllByFamilyId(familyId);
        familyMembers.forEach(member -> {
            FamilySubscriptions previousFamily = member.getFamilySubscription();
            if (previousFamily != null && previousFamily != subscription) {
                previousFamily.removeMember(member);
            }
            subscription.addMember(member);
        });
        if (subscription.getHeadMemberId() == null && !familyMembers.isEmpty()) {
            subscription.setHeadMemberId(String.valueOf(familyMembers.get(0).getMembershipId()));
        }
    }

    private void refreshBalance(FamilySubscriptions subscription) {
        String familyId = subscription.getFamilyId();
        List<PledgeRateChange> rateHistory = rates.findByFamilyIdOrderByEffectiveDateAscIdAsc(familyId);
        PledgeOpeningBalance opening = openingBalances.findFirstByFamilyIdOrderByIdAsc(familyId).orElse(null);
        List<PaymentTransactionEntry> paymentHistory = transactions.findByMember_FamilyIdOrderByDateAsc(familyId);
        PledgeBalanceCalculator.Balance balance = PledgeBalanceCalculator.calculate(
                subscription.getPledgeStartDate(), opening, rateHistory, paymentHistory, LocalDate.now());

        subscription.setPledgeDue(balance.due());
        subscription.setLastPledgeDue(balance.due());
        subscription.setPledgeCredit(balance.credit());
        rateHistory.stream()
                .filter(rate -> !rate.getEffectiveDate().isAfter(LocalDate.now()))
                .max(Comparator.comparing(PledgeRateChange::getEffectiveDate).thenComparing(PledgeRateChange::getId))
                .ifPresent(rate -> subscription.setPledgeAmount(rate.getMonthlyAmount()));

        Optional<PaymentTransactionEntry> lastPayment = paymentHistory.stream()
                .filter(tx -> tx.getDate() != null && tx.getAmount() > 0L)
            .max(Comparator.comparing(PaymentTransactionEntry::getDate));
        subscription.setLastPledgeDepositDate(lastPayment.map(PaymentTransactionEntry::getDate).orElse(null));
        subscription.setLastPledgeDepositAmount(lastPayment.map(PaymentTransactionEntry::getAmount).orElse(0L));
        subscriptions.save(subscription);
    }

    private LocalDate nextBillingDate(LocalDate startDate, LocalDate effectiveDate) {
        if (startDate == null || !effectiveDate.isAfter(startDate)) return startDate;
        for (int month = 1; ; month++) {
            LocalDate dueDate = startDate.plusMonths(month);
            if (!dueDate.isBefore(effectiveDate)) return dueDate;
        }
    }

    private String familyHeadName(FamilySubscriptions subscription) {
        return subscription.getMembers().stream()
                .filter(member -> String.valueOf(member.getMembershipId()).equals(subscription.getHeadMemberId()))
                .findFirst()
                .or(() -> subscription.getMembers().stream().findFirst())
                .map(member -> (member.getFirstName() + " " + member.getLastName()).trim())
                .orElse("Family " + subscription.getFamilyId());
    }

    private PledgeAccountResponse toResponse(FamilySubscriptions subscription, boolean exists) {
        return toResponse(subscription, exists, null);
        }

        private PledgeAccountResponse toResponse(
            FamilySubscriptions subscription,
            boolean exists,
            String recordedTransactionId) {
        String familyId = subscription.getFamilyId();
        List<PledgeRateChange> rateHistory = rates.findByFamilyIdOrderByEffectiveDateAscIdAsc(familyId);
        Optional<PledgeOpeningBalance> opening = openingBalances.findFirstByFamilyIdOrderByIdAsc(familyId);
        List<PaymentTransactionEntry> paymentHistory = transactions.findByMember_FamilyIdOrderByDateAsc(familyId);
        List<PledgeRateDto> rateDtos = rateHistory.stream()
                .map(rate -> new PledgeRateDto(rate.getMonthlyAmount(), rate.getEffectiveDate()))
                .toList();
        List<PledgeTransactionDto> transactionDtos = paymentHistory.stream()
                .sorted(Comparator.comparing(PaymentTransactionEntry::getDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(tx -> new PledgeTransactionDto(tx.getTransaction_id(), tx.getName(), tx.getDate(), tx.getAmount()))
                .toList();

        return new PledgeAccountResponse(
                familyId,
                exists,
                opening.isPresent() && !rateHistory.isEmpty(),
                subscription.getPledgeAmount(),
                subscription.getPledgeStartDate(),
                subscription.getPledgeDue(),
                subscription.getPledgeCredit(),
                subscription.getLastPledgeDepositDate(),
                subscription.getLastPledgeDepositAmount(),
                recordedTransactionId,
                rateDtos,
                transactionDtos);
    }

    private PledgeAccountResponse emptyAccount(String familyId) {
        return new PledgeAccountResponse(familyId, false, false, 0L, null, 0L, 0L,
            null, 0L, null, List.of(), List.of());
    }
}
