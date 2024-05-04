package com.example.CentralMethodistChurch.Service.Impl;

import com.example.CentralMethodistChurch.Entity.FamilyMember;
import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;
import com.example.CentralMethodistChurch.Repository.FamilyTreeRepository;
import com.example.CentralMethodistChurch.Repository.MemberRepository;
import com.example.CentralMethodistChurch.Service.FamilyTreeServices;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ashwini Charles on 4/27/2024
 * @project spring-church-app
 */

@Service
//@Transactional
public class FamilyTreeServicesImpl implements FamilyTreeServices {

    @Autowired
    private FamilyTreeRepository familyTreeRepository;

    @Autowired
    private MemberRepository memberRepository;

    /**
     * @return
     */
    @Override
    public List<FamilySubscriptions> fetchFamilyTrees() {
        return familyTreeRepository.findAll();
    }

    @Override
    public String indexFamilyTrees() {
        List<FamilyMember> members = memberRepository.findAll();
        for(FamilyMember member: members) {
            if(null == member.getFamilyId() && member.getFatherId() != null) {
                List<FamilyMember> listOfMembers = new ArrayList<>(List.of(member));
                FamilySubscriptions family = new FamilySubscriptions();
                family.setHeadMemberId(String.valueOf(member.getMembershipId()));
                family = familyTreeRepository.save(family);
                member.setFamilyId(family.getFamilyId());
                //Married member
                if(null != member.getSpouseId()) {
                    Optional<FamilyMember> spouse = members.stream()
                            .filter(mb -> String.valueOf(mb.getMembershipId()).equals(member.getSpouseId()))
                            .filter(mb -> mb.getFamilyId() == null)
                            .findFirst();
                    if(spouse.isPresent() && spouse.get().getFamilyId() == null) {
                        spouse.get().setFamilyId(family.getFamilyId());
                        listOfMembers.add(spouse.get());
                        memberRepository.save(spouse.get());
                    }
                    this.LookforChildren(member, members, family, listOfMembers);
                }

                //Member Single
                if(null == member.getSpouseId() && null == member.getFatherId()) {
                    this.LookforChildren(member, members, family, listOfMembers);
                }
                familyTreeRepository.save(family);
            }
        }

        return null;
    }

    private void LookforChildren(FamilyMember member, List<FamilyMember> members, FamilySubscriptions family, List<FamilyMember> listOfMembers) {
        List<FamilyMember> children = members.stream()
                .filter(child -> child.getFatherId() != null)
                .filter(child -> child.getFatherId().equals(String.valueOf(member.getMembershipId())))
                .filter(child -> child.getFamilyId() == null)
                .toList();

        if(!children.isEmpty()) {
            children.forEach(child -> child.setFamilyId(family.getFamilyId()));
            listOfMembers.addAll(children);
            memberRepository.saveAll(children);
        }
        family.setMembers(listOfMembers);
//        familyTreeRepository.save(family);
    }

    @Override
    public void truncateFamilyTree() {
        List<FamilySubscriptions> families = familyTreeRepository.findAll();
        familyTreeRepository.deleteAll(families);
        List<FamilyMember> members = memberRepository.findAll();
        members.forEach(member -> member.setFamilyId(null));
        memberRepository.saveAll(members);
    }


}
