package com.example.CentralMethodistChurch.Service.Impl;

import com.example.CentralMethodistChurch.Entity.FamilyMember;
import com.example.CentralMethodistChurch.Repository.MemberRepository;
import com.example.CentralMethodistChurch.Service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public List<FamilyMember> fetchAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    public FamilyMember fetchById(String id) {
        Optional<FamilyMember> member = memberRepository.findById(id);
        return member.orElse(null);
    }

    @Override
    public void deleteMemberbyId(String id) {
        memberRepository.deleteById(id);
    }

    @Override
    public FamilyMember saveMember (FamilyMember member) {
        return memberRepository.save(member);
    }
    @Override
    public List<FamilyMember> saveAllMembers(List<FamilyMember> members) {
        return memberRepository.saveAll(members);
    }
}
