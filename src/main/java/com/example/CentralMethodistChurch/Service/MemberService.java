package com.example.CentralMethodistChurch.Service;

import com.example.CentralMethodistChurch.Entity.FamilyMember;
import org.springframework.context.annotation.Bean;

import java.util.List;

public interface MemberService {
    List<FamilyMember> fetchAllMembers();
    FamilyMember fetchById(String id);
    void deleteMemberbyId(String Id);
    FamilyMember saveMember(FamilyMember member);
    List<FamilyMember> saveAllMembers(List<FamilyMember> members);
}
