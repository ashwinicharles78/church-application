package com.example.CentralMethodistChurch.Controller;

import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;
import com.example.CentralMethodistChurch.Entity.PaymentTransactionEntry;
import com.example.CentralMethodistChurch.Service.FamilyPledge;
import com.example.CentralMethodistChurch.Service.PaymentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class InvoiceController {

    @Autowired
    FamilyPledge familyPledge;

    @Autowired
    PaymentTransactionService paymentTransactionService;

    @GetMapping("invoice/{id}")
    public ModelAndView getInvoice(@PathVariable String id) {
        FamilySubscriptions subscription = familyPledge.fetchById(id);

        ModelAndView mav = new ModelAndView("invoice"); // template name
        mav.addObject("subscription", subscription);
        return mav;
    }

    @GetMapping("subscription/{id}")
    public FamilySubscriptions getSubscription(@PathVariable String id) {
        return familyPledge.fetchById(id);
    }

    @PostMapping("subscription/pledge/{id}")
    public FamilySubscriptions deductPledge(@PathVariable String id, @RequestBody FamilySubscriptions updatedDetails) {
        FamilySubscriptions subscriptions = familyPledge.fetchById(id);
        PaymentTransactionEntry entry = paymentTransactionService.createTransactionForPledge(subscriptions, updatedDetails.getPledgeCredit());
        familyPledge.calculatePledge(subscriptions, updatedDetails.getPledgeCredit());
        return familyPledge.updateSubscriptionManual(subscriptions.getFamilyId(), subscriptions, entry);
    }

    @PutMapping("/subscription/{id}")
    public FamilySubscriptions updateSubscription(@PathVariable String id, @RequestBody FamilySubscriptions updatedDetails) {
        return familyPledge.updateSubscriptionManual(id, updatedDetails, null);
    }
}
