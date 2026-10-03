package com.example.CentralMethodistChurch.DTO;

import java.util.List;

public record PaymentVoucherPageResponse(
        List<PaymentVoucherResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {}