package com.example.CentralMethodistChurch.Controller;

import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;
import com.example.CentralMethodistChurch.Service.FamilyPledge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class InvoiceController {

    @Autowired
    FamilyPledge familyPledge;

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

    @GetMapping("subscription/pledge/{id}")
    public FamilySubscriptions deductPledge(@PathVariable String id) {
        FamilySubscriptions subscriptions = familyPledge.fetchById(id);
        familyPledge.calculatePledge(subscriptions);
        return familyPledge.saveSubscription(subscriptions);
    }

    @PutMapping("/subscription/{id}")
    public FamilySubscriptions updateSubscription(@PathVariable String id, @RequestBody FamilySubscriptions updatedDetails) {
        FamilySubscriptions subscription = familyPledge.fetchById(id);

        // Mapping form values to the Entity
        subscription.setPledgeAmount(updatedDetails.getPledgeAmount());
        subscription.setPledgeCredit(updatedDetails.getPledgeCredit());
        subscription.setLastPledgeDue(updatedDetails.getLastPledgeDue());
        subscription.setPledgeStartDate(updatedDetails.getPledgeStartDate());
        subscription.setLastPledgeDepositDate(updatedDetails.getLastPledgeDepositDate());
        subscription.setLastPledgeDepositAmount(updatedDetails.getLastPledgeDepositAmount());

        return familyPledge.saveSubscription(subscription);
    }
}
