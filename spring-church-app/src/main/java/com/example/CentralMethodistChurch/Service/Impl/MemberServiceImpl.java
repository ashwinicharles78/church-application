/**
 * @author Ashwini Charles on 3/3/2024
 * @project CentralMethodistChurch
 */
package com.example.CentralMethodistChurch.Service.Impl;

import com.example.CentralMethodistChurch.Entity.FamilyMember;
import com.example.CentralMethodistChurch.Repository.MemberRepository;
import com.example.CentralMethodistChurch.Service.FamilyPopulator;
import com.example.CentralMethodistChurch.Service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FamilyPopulator populator;

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
        members.forEach(member -> populator.populateFamily(member));
        return memberRepository.saveAll(members);
    }

    /**
     * @param id
     * @param familyMember
     * @return
     */
    @Override
    public FamilyMember updateMember(String id, FamilyMember familyMember) {
        FamilyMember memberOriginal = memberRepository.findById(id).orElse(null);
        if(Objects.nonNull(memberOriginal)) {
            if(memberOriginal.equals(familyMember))
                return memberRepository.save(familyMember);
            else {
                if (!memberOriginal.getTitle().equals(familyMember.getTitle())) {
                    memberOriginal.setTitle(familyMember.getTitle());
                }
                if (!memberOriginal.getLastName().equals(familyMember.getLastName())) {
                    memberOriginal.setLastName(familyMember.getLastName());
                }
                if (!memberOriginal.getMiddleName().equals(familyMember.getMiddleName())) {
                    memberOriginal.setMiddleName(familyMember.getMiddleName());
                }
                if (!memberOriginal.getFirstName().equals(familyMember.getFirstName())) {
                    memberOriginal.setFirstName(familyMember.getFirstName());
                }
                if (!memberOriginal.getFatherName().equals(familyMember.getFatherName())) {
                    memberOriginal.setFatherName(familyMember.getFatherName());
                }
                if (!memberOriginal.getFatherId().equals(familyMember.getFatherId())) {
                    memberOriginal.setFatherId(familyMember.getFatherId());
                }
                if (!memberOriginal.getMotherName().equals(familyMember.getMotherName())) {
                    memberOriginal.setMotherName(familyMember.getMotherName());
                }
                if (!memberOriginal.getMotherId().equals(familyMember.getMotherId())) {
                    memberOriginal.setMotherId(familyMember.getMotherId());
                }
                if (!memberOriginal.getSpouseName().equals(familyMember.getSpouseName())) {
                    memberOriginal.setSpouseName(familyMember.getSpouseName());
                }
                if (!memberOriginal.getSpouseId().equals(familyMember.getSpouseId())) {
                    memberOriginal.setSpouseId(familyMember.getSpouseId());
                }
                if (!memberOriginal.getGender().equals(familyMember.getGender())) {
                    memberOriginal.setGender(familyMember.getGender());
                }
                if (!memberOriginal.getDob().equals(familyMember.getDob())) {
                    memberOriginal.setDob(familyMember.getDob());
                }
                if (!memberOriginal.getAge().equals(familyMember.getAge())) {
                    memberOriginal.setAge(familyMember.getAge());
                }
                if (!memberOriginal.getMaritalStatus().equals(familyMember.getMaritalStatus())) {
                    memberOriginal.setMaritalStatus(familyMember.getMaritalStatus());
                }
                if (!memberOriginal.getDateOfMarriage().equals(familyMember.getDateOfMarriage())) {
                    memberOriginal.setDateOfMarriage(familyMember.getDateOfMarriage());
                }
                if (!memberOriginal.getYearsOfMarriage().equals(familyMember.getYearsOfMarriage())) {
                    memberOriginal.setYearsOfMarriage(familyMember.getYearsOfMarriage());
                }
                if (!memberOriginal.getContact().equals(familyMember.getContact())) {
                    memberOriginal.setContact(familyMember.getContact());
                }
                if (!memberOriginal.getEmail().equals(familyMember.getEmail())) {
                    memberOriginal.setEmail(familyMember.getEmail());
                }
                if (!memberOriginal.getBaptised().equals(familyMember.getBaptised())) {
                    memberOriginal.setBaptised(familyMember.getBaptised());
                }
                if (!memberOriginal.getBaptisedDate().equals(familyMember.getBaptisedDate())) {
                    memberOriginal.setBaptisedDate(familyMember.getBaptisedDate());
                }
                if (!memberOriginal.getConfirmed().equals(familyMember.getConfirmed())) {
                    memberOriginal.setConfirmed(familyMember.getConfirmed());
                }
                if (!memberOriginal.getConfirmationDate().equals(familyMember.getConfirmationDate())) {
                    memberOriginal.setConfirmationDate(familyMember.getConfirmationDate());
                }
                if (!memberOriginal.getFullMember().equals(familyMember.getFullMember())) {
                    memberOriginal.setFullMember(familyMember.getFullMember());
                }
                if (!memberOriginal.getNonResidentMember().equals(familyMember.getNonResidentMember())) {
                    memberOriginal.setNonResidentMember(familyMember.getNonResidentMember());
                }
                if (!memberOriginal.getPreparatoryMember().equals(familyMember.getPreparatoryMember())) {
                    memberOriginal.setPreparatoryMember(familyMember.getPreparatoryMember());
                }
                if (!memberOriginal.getSelfDependent().equals(familyMember.getSelfDependent())) {
                    memberOriginal.setSelfDependent(familyMember.getSelfDependent());
                }
                if (!memberOriginal.getPledgeNumber().equals(familyMember.getPledgeNumber())) {
                    memberOriginal.setPledgeNumber(familyMember.getPledgeNumber());
                }
                if (!memberOriginal.getPledgeAmount().equals(familyMember.getPledgeAmount())) {
                    memberOriginal.setPledgeAmount(familyMember.getPledgeAmount());
                }
                if (!memberOriginal.getStatus().equals(familyMember.getStatus())) {
                    memberOriginal.setStatus(familyMember.getStatus());
                }
                if (!memberOriginal.getInactiveReason().equals(familyMember.getInactiveReason())) {
                    memberOriginal.setInactiveReason(familyMember.getInactiveReason());
                }
                if (!memberOriginal.getInactiveSince().equals(familyMember.getInactiveSince())) {
                    memberOriginal.setInactiveSince(familyMember.getInactiveSince());
                }
                return memberRepository.save(memberOriginal);
            }
        }
        return null;
    }
}
