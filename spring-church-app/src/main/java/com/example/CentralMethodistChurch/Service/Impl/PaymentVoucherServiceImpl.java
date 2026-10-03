package com.example.CentralMethodistChurch.Service.Impl;

import com.example.CentralMethodistChurch.DTO.PaymentVoucherPageResponse;
import com.example.CentralMethodistChurch.DTO.PaymentVoucherRequest;
import com.example.CentralMethodistChurch.DTO.PaymentVoucherResponse;
import com.example.CentralMethodistChurch.Entity.PaymentVoucher;
import com.example.CentralMethodistChurch.Entity.PaymentVoucherSerialCounter;
import com.example.CentralMethodistChurch.Repository.PaymentVoucherRepository;
import com.example.CentralMethodistChurch.Repository.PaymentVoucherSerialCounterRepository;
import com.example.CentralMethodistChurch.Service.PaymentVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentVoucherServiceImpl implements PaymentVoucherService {
    private static final int MAX_PAGE_SIZE = 100;

    private final PaymentVoucherRepository vouchers;
    private final PaymentVoucherSerialCounterRepository counters;
    private final JdbcTemplate jdbcTemplate;
    private final Clock clock;

    @Autowired
    public PaymentVoucherServiceImpl(
            PaymentVoucherRepository vouchers,
            PaymentVoucherSerialCounterRepository counters,
            JdbcTemplate jdbcTemplate) {
        this(vouchers, counters, jdbcTemplate, Clock.systemDefaultZone());
    }

    PaymentVoucherServiceImpl(
            PaymentVoucherRepository vouchers,
            PaymentVoucherSerialCounterRepository counters,
            JdbcTemplate jdbcTemplate,
            Clock clock) {
        this.vouchers = vouchers;
        this.counters = counters;
        this.jdbcTemplate = jdbcTemplate;
        this.clock = clock;
    }

    @Override
    @Transactional
    public PaymentVoucherResponse create(PaymentVoucherRequest request) {
        validate(request);
        LocalDate today = LocalDate.now(clock);
        int year = today.getYear();
        jdbcTemplate.update("INSERT IGNORE INTO payment_voucher_serial_counter (voucher_year, next_number) VALUES (?, 1)", year);
        PaymentVoucherSerialCounter counter = counters.findByYearForUpdate(year)
                .orElseThrow(() -> new IllegalStateException("Could not allocate a voucher serial number."));
        long serial = counter.getNextNumber();
        counter.setNextNumber(Math.addExact(serial, 1));
        counters.save(counter);

        PaymentVoucher voucher = new PaymentVoucher();
        voucher.setSerialNumber(String.format("PV-%d-%06d", year, serial));
        voucher.setVoucherDate(request.voucherDate());
        voucher.setApprovedAmount(request.approvedAmount());
        voucher.setAmountInWords(clean(request.amountInWords()));
        voucher.setPurpose(clean(request.purpose()));
        voucher.setPayee(clean(request.payee()));
        voucher.setBillNumber(cleanNullable(request.billNumber()));
        voucher.setBillDate(request.billDate());
        voucher.setPaymentMethod(normalizeMethod(request.paymentMethod()));
        voucher.setPaymentReferenceNumber(cleanNullable(request.paymentReferenceNumber()));
        voucher.setPaymentReferenceDate(request.paymentReferenceDate());
        voucher.setTotalBillsAmount(request.totalBillsAmount());
        voucher.setAdvanceAmount(request.advanceAmount());
        voucher.setNetAmount(Math.subtractExact(request.totalBillsAmount(), request.advanceAmount()));
        voucher.setPaymentDate(request.paymentDate());
        voucher.setReceiptMethod(cleanNullable(request.receiptMethod()));
        voucher.setReceiptReferenceNumber(cleanNullable(request.receiptReferenceNumber()));
        voucher.setReceiptDate(request.receiptDate());
        voucher.setReceiptAmount(request.receiptAmount());
        voucher.setPaidBy(cleanNullable(request.paidBy()));

        try {
            return toResponse(vouchers.saveAndFlush(voucher));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Voucher serial number already exists.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentVoucherResponse findBySerialNumber(String serialNumber) {
        return vouchers.findBySerialNumber(normalizeSerial(serialNumber))
                .map(this::toResponse)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment voucher not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentVoucherResponse> latest() {
        return vouchers.findTop3ByOrderByCreatedAtDescIdDesc().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentVoucherPageResponse list(int page, int size) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page must be nonnegative and size must be between 1 and 100.");
        }
        var result = vouchers.findAllByOrderByCreatedAtDescIdDesc(
                org.springframework.data.domain.PageRequest.of(page, size));
        return new PaymentVoucherPageResponse(
                result.getContent().stream().map(this::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    private void validate(PaymentVoucherRequest request) {
        if (request == null || request.voucherDate() == null || request.paymentDate() == null
                || blank(request.amountInWords()) || blank(request.purpose()) || blank(request.payee())
                || blank(request.paymentMethod()) || request.approvedAmount() < 0
                || request.totalBillsAmount() < 0 || request.advanceAmount() < 0
                || request.receiptAmount() != null && request.receiptAmount() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Provide the voucher date, amount in words, purpose, payee, payment method, and nonnegative amounts.");
        }
        if (!request.paymentMethod().equalsIgnoreCase("CASH") && !request.paymentMethod().equalsIgnoreCase("CHEQUE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment method must be CASH or CHEQUE.");
        }
        if (request.receiptMethod() != null && !request.receiptMethod().isBlank()
                && !request.receiptMethod().equalsIgnoreCase("CASH")
                && !request.receiptMethod().equalsIgnoreCase("CHEQUE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receipt method must be CASH or CHEQUE.");
        }
        if (request.advanceAmount() > request.totalBillsAmount()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Advance cannot exceed total amount of bills.");
        }
        if (request.voucherDate().isAfter(LocalDate.now(clock)) || request.paymentDate().isAfter(LocalDate.now(clock))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voucher and payment dates cannot be in the future.");
        }
        if (request.receiptDate() != null && request.receiptDate().isAfter(LocalDate.now(clock))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receipt date cannot be in the future.");
        }
    }

    private PaymentVoucherResponse toResponse(PaymentVoucher voucher) {
        return new PaymentVoucherResponse(voucher.getSerialNumber(), voucher.getVoucherDate(), voucher.getApprovedAmount(),
                voucher.getAmountInWords(), voucher.getPurpose(), voucher.getPayee(), voucher.getBillNumber(),
                voucher.getBillDate(), voucher.getPaymentMethod(), voucher.getPaymentReferenceNumber(),
                voucher.getPaymentReferenceDate(), voucher.getTotalBillsAmount(), voucher.getAdvanceAmount(),
                voucher.getNetAmount(), voucher.getPaymentDate(), voucher.getReceiptMethod(),
                voucher.getReceiptReferenceNumber(), voucher.getReceiptDate(), voucher.getReceiptAmount(),
                voucher.getPaidBy(), voucher.getCreatedAt());
    }

    private String normalizeSerial(String serialNumber) {
        if (serialNumber == null || serialNumber.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Serial number is required.");
        }
        return serialNumber.trim().toUpperCase();
    }

    private String normalizeMethod(String method) { return method.trim().toUpperCase(); }
    private boolean blank(String value) { return value == null || value.isBlank(); }
    private String clean(String value) { return value.trim(); }
    private String cleanNullable(String value) { return blank(value) ? null : value.trim(); }
}