package com.example.CentralMethodistChurch.Controller;

import com.example.CentralMethodistChurch.DTO.PaymentVoucherResponse;
import com.example.CentralMethodistChurch.Service.PaymentVoucherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(PaymentVoucherController.class)
class PaymentVoucherControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentVoucherService vouchers;

    @Test
    void rendersStoredVoucherFieldsInPrintableTemplate() throws Exception {
        when(vouchers.findBySerialNumber("PV-2026-000001")).thenReturn(voucher());

        mockMvc.perform(get("/payment-vouchers/PV-2026-000001/print"))
                .andExpect(status().isOk())
                .andExpect(view().name("voucher"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("PV-2026-000001")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Office supplies")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("clearance of bills under referance.")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Signature of Receipent")));
    }

    private PaymentVoucherResponse voucher() {
        return new PaymentVoucherResponse(
                "PV-2026-000001", LocalDate.of(2026, 9, 30), 1000, "One thousand rupees",
                "Office supplies", "Local supplier", "B-12", LocalDate.of(2026, 9, 29),
                "CHEQUE", "CH-108", LocalDate.of(2026, 9, 30), 1200, 350, 850,
                LocalDate.of(2026, 9, 30), "CASH", null, null, 850L, "Church office",
                LocalDateTime.of(2026, 10, 1, 10, 0));
    }
}