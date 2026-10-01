package com.example.CentralMethodistChurch.Repository;

import com.example.CentralMethodistChurch.Entity.PledgeOpeningBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PledgeOpeningBalanceRepository extends JpaRepository<PledgeOpeningBalance, Long> {
    Optional<PledgeOpeningBalance> findFirstByFamilyIdOrderByIdAsc(String familyId);
}