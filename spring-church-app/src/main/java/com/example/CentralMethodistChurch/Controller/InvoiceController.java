package com.example.CentralMethodistChurch.Controller;

import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;
import com.example.CentralMethodistChurch.Entity.PaymentTransactionEntry;
import com.example.CentralMethodistChurch.DTO.PledgeAccountResponse;
import com.example.CentralMethodistChurch.DTO.PledgePaymentRequest;
import com.example.CentralMethodistChurch.DTO.PledgeResetRequest;
import com.example.CentralMethodistChurch.DTO.PledgeSetupRequest;
import com.example.CentralMethodistChurch.DTO.PledgeTermsRequest;
import com.example.CentralMethodistChurch.Service.FamilyPledge;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class InvoiceController {

    private final FamilyPledge familyPledge;

    public InvoiceController(FamilyPledge familyPledge) {
        this.familyPledge = familyPledge;
    }

    @GetMapping("invoice/{id}")
    public ModelAndView getInvoice(@PathVariable String id,
                                   @RequestParam(defaultValue = "false") boolean printCopies) {
        FamilySubscriptions subscription = familyPledge.fetchForInvoice(id);

        ModelAndView mav = new ModelAndView("invoice"); // template name
        mav.addObject("subscription", subscription);
        mav.addObject("printCopies", printCopies);
        return mav;
    }

    @GetMapping("invoice/{id}/transaction/{transactionId}")
    public ModelAndView getTransactionInvoice(@PathVariable String id, @PathVariable String transactionId,
                                              @RequestParam(defaultValue = "false") boolean printCopies) {
        FamilySubscriptions subscription = familyPledge.fetchForInvoice(id);
        PaymentTransactionEntry transaction = familyPledge.fetchTransaction(id, transactionId);
        ModelAndView mav = new ModelAndView("invoice");
        mav.addObject("subscription", subscription);
        mav.addObject("transaction", transaction);
        mav.addObject("printCopies", printCopies);
        return mav;
    }

    @GetMapping("subscription/{id}")
    public PledgeAccountResponse getSubscription(@PathVariable String id) {
        return familyPledge.getAccount(id);
    }

    @PostMapping("subscription/{id}/setup")
    public PledgeAccountResponse setupSubscription(@PathVariable String id, @RequestBody PledgeSetupRequest request) {
        return familyPledge.setupAccount(id, request);
    }

    @PutMapping("subscription/{id}/terms")
    public PledgeAccountResponse updateTerms(@PathVariable String id, @RequestBody PledgeTermsRequest request) {
        return familyPledge.changeTerms(id, request);
    }

    @PostMapping("subscription/{id}/payments")
    public PledgeAccountResponse recordPayment(@PathVariable String id, @RequestBody PledgePaymentRequest request) {
        return familyPledge.recordPayment(id, request);
    }

    @PostMapping("subscription/{id}/reset")
    public PledgeAccountResponse resetSubscription(@PathVariable String id, @RequestBody PledgeResetRequest request) {
        return familyPledge.resetAccount(id, request);
    }

    @PostMapping("subscription/pledge/{id}")
    public PledgeAccountResponse legacyPayment(@PathVariable String id, @RequestBody FamilySubscriptions request) {
        if (request.getPledgeCredit() <= 0L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment amount must be positive.");
        }
        return familyPledge.recordPayment(id, new PledgePaymentRequest(request.getPledgeCredit(), java.time.LocalDate.now()));
    }

    @PutMapping("/subscription/{id}")
    public PledgeAccountResponse legacyTermsUpdate(@PathVariable String id, @RequestBody FamilySubscriptions request) {
        return familyPledge.changeTerms(id, new PledgeTermsRequest(request.getPledgeAmount(), java.time.LocalDate.now()));
    }
}
