package com.example.CentralMethodistChurch.Service.Impl;

import com.example.CentralMethodistChurch.DTO.PaymentVoucherRequest;
import com.example.CentralMethodistChurch.Entity.PaymentVoucher;
import com.example.CentralMethodistChurch.Entity.PaymentVoucherSerialCounter;
import com.example.CentralMethodistChurch.Repository.PaymentVoucherRepository;
import com.example.CentralMethodistChurch.Repository.PaymentVoucherSerialCounterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentVoucherServiceImplTest {
    private final PaymentVoucherRepository vouchers = mock(PaymentVoucherRepository.class);
    private final PaymentVoucherSerialCounterRepository counters = mock(PaymentVoucherSerialCounterRepository.class);
    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-01T10:00:00Z"), ZoneId.of("Asia/Kolkata"));
    private final PaymentVoucherServiceImpl service = new PaymentVoucherServiceImpl(vouchers, counters, jdbcTemplate, clock);

    @Test
    void createsVoucherWithYearlySerialAndCalculatedNetAmount() {
        PaymentVoucherSerialCounter counter = new PaymentVoucherSerialCounter();
        counter.setVoucherYear(2026);
        counter.setNextNumber(7);
        when(counters.findByYearForUpdate(2026)).thenReturn(Optional.of(counter));
        when(vouchers.saveAndFlush(any(PaymentVoucher.class))).thenAnswer(invocation -> {
            PaymentVoucher voucher = invocation.getArgument(0);
            voucher.setCreatedAt(LocalDateTime.now(clock));
            return voucher;
        });

        var created = service.create(validRequest());

        assertEquals("PV-2026-000007", created.serialNumber());
        assertEquals(850L, created.netAmount());
        assertEquals("CHEQUE", created.paymentMethod());
        verify(jdbcTemplate).update(
                "INSERT IGNORE INTO payment_voucher_serial_counter (voucher_year, next_number) VALUES (?, 1)", 2026);
        verify(counters).save(counter);
    }

    @Test
    void rejectsAdvanceGreaterThanBillTotalBeforeAllocatingSerial() {
        PaymentVoucherRequest invalid = new PaymentVoucherRequest(
                LocalDate.of(2026, 9, 30), 1000, "One thousand", "Repairs", "Supplier",
                null, null, "CASH", null, null, 100, 101, LocalDate.of(2026, 9, 30),
                null, null, null, null, null);

        assertThrows(ResponseStatusException.class, () -> service.create(invalid));

        verify(jdbcTemplate, never()).update(any(String.class), any(Object[].class));
        verify(vouchers, never()).saveAndFlush(any());
    }

    private PaymentVoucherRequest validRequest() {
        return new PaymentVoucherRequest(
                LocalDate.of(2026, 9, 30), 1000, "One thousand rupees", "Office repairs", "Local supplier",
                "B-12", LocalDate.of(2026, 9, 29), "cheque", "CH-108", LocalDate.of(2026, 9, 30),
                1200, 350, LocalDate.of(2026, 9, 30), "CASH", null, null, 850L, "Church office");
    }
}