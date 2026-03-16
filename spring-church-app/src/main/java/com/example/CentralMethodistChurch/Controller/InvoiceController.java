package com.example.CentralMethodistChurch.Controller;

import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;
import com.example.CentralMethodistChurch.Service.FamilyPledge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvoiceController {

    @Autowired
    FamilyPledge familyPledge;

    @GetMapping("/invoice/{id}")
    public String showInvoice(@PathVariable Long id, Model model) {

        return "";
    }

    @GetMapping("subscription/{id}")
    public FamilySubscriptions getSubscription(@PathVariable String id) {
        return familyPledge.fetchById(id);
    }
}
