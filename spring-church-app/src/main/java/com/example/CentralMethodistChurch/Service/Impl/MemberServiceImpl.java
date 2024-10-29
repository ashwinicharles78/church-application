/**
 * @author Ashwini Charles on 3/3/2024
 * @project CentralMethodistChurch
 */
package com.example.CentralMethodistChurch.Service.Impl;

import com.example.CentralMethodistChurch.DTO.Events;
import com.example.CentralMethodistChurch.Entity.FamilyMember;
import com.example.CentralMethodistChurch.Enums.EventType;
import com.example.CentralMethodistChurch.Repository.MemberRepository;
import com.example.CentralMethodistChurch.Service.FamilyPopulator;
import com.example.CentralMethodistChurch.Service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class MemberServiceImpl implements MemberService {

    private static final String SPACE = " ";
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
                if (memberOriginal.getTitle()==null || !memberOriginal.getTitle().equals(familyMember.getTitle())) {
                    memberOriginal.setTitle(familyMember.getTitle());
                }
                if (memberOriginal.getLastName()==null || !memberOriginal.getLastName().equals(familyMember.getLastName())) {
                    memberOriginal.setLastName(familyMember.getLastName());
                }
                if (memberOriginal.getMiddleName()==null || !memberOriginal.getMiddleName().equals(familyMember.getMiddleName())) {
                    memberOriginal.setMiddleName(familyMember.getMiddleName());
                }
                if (memberOriginal.getFirstName()==null || !memberOriginal.getFirstName().equals(familyMember.getFirstName())) {
                    memberOriginal.setFirstName(familyMember.getFirstName());
                }
                if (memberOriginal.getFatherName()==null || !memberOriginal.getFatherName().equals(familyMember.getFatherName())) {
                    memberOriginal.setFatherName(familyMember.getFatherName());
                }
                if (memberOriginal.getFatherId()==null || !memberOriginal.getFatherId().equals(familyMember.getFatherId())) {
                    memberOriginal.setFatherId(familyMember.getFatherId());
                }
                if (memberOriginal.getMotherName()==null || !memberOriginal.getMotherName().equals(familyMember.getMotherName())) {
                    memberOriginal.setMotherName(familyMember.getMotherName());
                }
                if (memberOriginal.getMotherId()==null || !memberOriginal.getMotherId().equals(familyMember.getMotherId())) {
                    memberOriginal.setMotherId(familyMember.getMotherId());
                }
                if (memberOriginal.getSpouseName()==null || !memberOriginal.getSpouseName().equals(familyMember.getSpouseName())) {
                    memberOriginal.setSpouseName(familyMember.getSpouseName());
                }
                if (memberOriginal.getSpouseId()==null || !memberOriginal.getSpouseId().equals(familyMember.getSpouseId())) {
                    memberOriginal.setSpouseId(familyMember.getSpouseId());
                }
                if (memberOriginal.getGender()==null ||!memberOriginal.getGender().equals(familyMember.getGender())) {
                    memberOriginal.setGender(familyMember.getGender());
                }
                if (null == memberOriginal.getDob() ||!memberOriginal.getDob().equals(familyMember.getDob())) {
                    memberOriginal.setDob(familyMember.getDob());
                }
                if (null == memberOriginal.getAge() ||!memberOriginal.getAge().equals(familyMember.getAge())) {
                    memberOriginal.setAge(familyMember.getAge());
                }
                if (null == memberOriginal.getAddress() ||!memberOriginal.getAddress().equals(familyMember.getAddress())) {
                    memberOriginal.setAddress(familyMember.getAddress());
                }
                if (null == memberOriginal.getMaritalStatus() ||!memberOriginal.getMaritalStatus().equals(familyMember.getMaritalStatus())) {
                    memberOriginal.setMaritalStatus(familyMember.getMaritalStatus());
                }
                if (null == memberOriginal.getDateOfMarriage() ||!memberOriginal.getDateOfMarriage().equals(familyMember.getDateOfMarriage())) {
                    memberOriginal.setDateOfMarriage(familyMember.getDateOfMarriage());
                }
                if (null == memberOriginal.getYearsOfMarriage() ||!memberOriginal.getYearsOfMarriage().equals(familyMember.getYearsOfMarriage())) {
                    memberOriginal.setYearsOfMarriage(familyMember.getYearsOfMarriage());
                }
                if (null == memberOriginal.getContact()||!memberOriginal.getContact().equals(familyMember.getContact())) {
                    memberOriginal.setContact(familyMember.getContact());
                }
                if (null == memberOriginal.getEmail() ||!memberOriginal.getEmail().equals(familyMember.getEmail())) {
                    memberOriginal.setEmail(familyMember.getEmail());
                }
                if (null == memberOriginal.getBaptisedDate() ||!memberOriginal.getBaptisedDate().equals(familyMember.getBaptisedDate())) {
                    memberOriginal.setBaptisedDate(familyMember.getBaptisedDate());
                }
                if (null == memberOriginal.getConfirmed() ||!memberOriginal.getConfirmed().equals(familyMember.getConfirmed())) {
                    memberOriginal.setConfirmed(familyMember.getConfirmed());
                }
                if (null == memberOriginal.getConfirmationDate() ||!memberOriginal.getConfirmationDate().equals(familyMember.getConfirmationDate())) {
                    memberOriginal.setConfirmationDate(familyMember.getConfirmationDate());
                }
                if (null == memberOriginal.getFullMember() ||!memberOriginal.getFullMember().equals(familyMember.getFullMember())) {
                    memberOriginal.setFullMember(familyMember.getFullMember());
                }
                if (null == memberOriginal.getNonResidentMember() ||!memberOriginal.getNonResidentMember().equals(familyMember.getNonResidentMember())) {
                    memberOriginal.setNonResidentMember(familyMember.getNonResidentMember());
                }
                if (null == memberOriginal.getPreparatoryMember() ||!memberOriginal.getPreparatoryMember().equals(familyMember.getPreparatoryMember())) {
                    memberOriginal.setPreparatoryMember(familyMember.getPreparatoryMember());
                }
                if (null == memberOriginal.getSelfDependent() ||!memberOriginal.getSelfDependent().equals(familyMember.getSelfDependent())) {
                    memberOriginal.setSelfDependent(familyMember.getSelfDependent());
                }
                if (null == memberOriginal.getPledgeNumber() || !memberOriginal.getPledgeNumber().equals(familyMember.getPledgeNumber())) {
                    memberOriginal.setPledgeNumber(familyMember.getPledgeNumber());
                }
                if (null == memberOriginal.getPledgeAmount() || !memberOriginal.getPledgeAmount().equals(familyMember.getPledgeAmount())) {
                    memberOriginal.setPledgeAmount(familyMember.getPledgeAmount());
                }
                if (null == memberOriginal.getStatus() || !memberOriginal.getStatus().equals(familyMember.getStatus())) {
                    memberOriginal.setStatus(familyMember.getStatus());
                }
                if (null == memberOriginal.getInactiveReason() || !memberOriginal.getInactiveReason().equals(familyMember.getInactiveReason())) {
                    memberOriginal.setInactiveReason(familyMember.getInactiveReason());
                }
                if (null == memberOriginal.getInactiveSince() || !memberOriginal.getInactiveSince().equals(familyMember.getInactiveSince())) {
                    memberOriginal.setInactiveSince(familyMember.getInactiveSince());
                }
                return memberRepository.save(memberOriginal);
            }
        }
        return null;
    }

    /**
     * @return
     */
    @Override
    public List<Events> getEvents() {
        List<FamilyMember> memebers = memberRepository.findAll();
        List<Events> events = new ArrayList<>();
        for(FamilyMember familyMember: memebers){
            if(isWithinSevenDays(familyMember.getDob())){
                events.add(new Events(EventType.Birthday, familyMember.getDob(), List.of(familyMember.getFirstName()+familyMember.getLastName())));
            }

            if(familyMember.getDateOfMarriage()!=null && isWithinSevenDays(familyMember.getDateOfMarriage())){
                FamilyMember spouse = this.fetchById(familyMember.getSpouseId());
                events.add(new Events(EventType.Anniversary, familyMember.getDob(), List.of(familyMember.getTitle() + SPACE + familyMember.getFirstName()+ SPACE + familyMember.getLastName(),familyMember.getTitle()+ SPACE + spouse.getFirstName() + SPACE + spouse.getLastName())));
            }

        }
        return events;
    }

    public static boolean isWithinSevenDays(LocalDate dob) {
        // Calculate the difference in days between the birthday and DOB
        long daysDifference = ChronoUnit.DAYS.between(LocalDate.now(), dob.withYear(LocalDate.now().getYear()));
        // Check if the difference is less than or equal to 7
        return daysDifference <= 7 && daysDifference >= 0;
    }
}
