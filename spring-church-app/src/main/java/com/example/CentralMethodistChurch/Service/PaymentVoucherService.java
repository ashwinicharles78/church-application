package com.example.CentralMethodistChurch.Service;

import com.example.CentralMethodistChurch.DTO.PaymentVoucherPageResponse;
import com.example.CentralMethodistChurch.DTO.PaymentVoucherRequest;
import com.example.CentralMethodistChurch.DTO.PaymentVoucherResponse;

import java.util.List;

public interface PaymentVoucherService {
    PaymentVoucherResponse create(PaymentVoucherRequest request);
    PaymentVoucherResponse findBySerialNumber(String serialNumber);
    List<PaymentVoucherResponse> latest();
    PaymentVoucherPageResponse list(int page, int size);
}