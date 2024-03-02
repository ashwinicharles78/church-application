package com.example.CentralMethodistChurch.Repository;

import com.example.CentralMethodistChurch.Entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<FamilyMember, String> {

}
