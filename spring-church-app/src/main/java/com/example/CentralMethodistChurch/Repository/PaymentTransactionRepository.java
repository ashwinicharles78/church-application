package com.example.CentralMethodistChurch.Repository;

import com.example.CentralMethodistChurch.Entity.PaymentTransactionEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransactionEntry, String> {
}
