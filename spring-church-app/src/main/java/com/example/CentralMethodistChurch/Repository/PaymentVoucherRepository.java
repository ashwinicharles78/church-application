package com.example.CentralMethodistChurch.Repository;

import com.example.CentralMethodistChurch.Entity.PaymentVoucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentVoucherRepository extends JpaRepository<PaymentVoucher, Long> {
    Optional<PaymentVoucher> findBySerialNumber(String serialNumber);

    List<PaymentVoucher> findTop3ByOrderByCreatedAtDescIdDesc();

    Page<PaymentVoucher> findAllByOrderByCreatedAtDescIdDesc(Pageable pageable);
}