package com.example.CentralMethodistChurch.Repository;

import com.example.CentralMethodistChurch.Entity.PledgeRateChange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PledgeRateChangeRepository extends JpaRepository<PledgeRateChange, Long> {
    List<PledgeRateChange> findByFamilyIdOrderByEffectiveDateAscIdAsc(String familyId);
}