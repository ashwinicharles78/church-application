package com.example.CentralMethodistChurch.Controller;

import com.example.CentralMethodistChurch.DTO.PaymentVoucherRequest;
import com.example.CentralMethodistChurch.DTO.PaymentVoucherResponse;
import com.example.CentralMethodistChurch.Service.PaymentVoucherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PaymentVoucherController {
    private final PaymentVoucherService vouchers;

    public PaymentVoucherController(PaymentVoucherService vouchers) {
        this.vouchers = vouchers;
    }

    @PostMapping("/payment-vouchers")
    @ResponseBody
    public ResponseEntity<PaymentVoucherResponse> create(@RequestBody PaymentVoucherRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vouchers.create(request));
    }

    @GetMapping("/payment-vouchers/latest")
    @ResponseBody
    public List<PaymentVoucherResponse> latest() {
        return vouchers.latest();
    }

    @GetMapping("/payment-vouchers/search")
    @ResponseBody
    public PaymentVoucherResponse search(@RequestParam String serialNumber) {
        return vouchers.findBySerialNumber(serialNumber);
    }

    @GetMapping("/payment-vouchers")
    @ResponseBody
    public Object list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       @RequestParam(required = false) String serialNumber) {
        if (serialNumber != null && !serialNumber.isBlank()) return vouchers.findBySerialNumber(serialNumber);
        return vouchers.list(page, size);
    }

    @GetMapping("/payment-vouchers/{serialNumber}/print")
    public String print(@PathVariable String serialNumber, Model model) {
        model.addAttribute("voucher", vouchers.findBySerialNumber(serialNumber));
        return "voucher";
    }
}