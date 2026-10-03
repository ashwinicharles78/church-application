package com.example.CentralMethodistChurch.Repository;

import com.example.CentralMethodistChurch.Entity.PaymentVoucherSerialCounter;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentVoucherSerialCounterRepository extends JpaRepository<PaymentVoucherSerialCounter, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select counter from PaymentVoucherSerialCounter counter where counter.voucherYear = :year")
    Optional<PaymentVoucherSerialCounter> findByYearForUpdate(@Param("year") int year);
}