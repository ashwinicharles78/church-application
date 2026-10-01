package com.example.CentralMethodistChurch.Repository;

import com.example.CentralMethodistChurch.Entity.PaymentTransactionEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransactionEntry, String> {
	List<PaymentTransactionEntry> findByMember_FamilyIdOrderByDateAsc(String familyId);
}
