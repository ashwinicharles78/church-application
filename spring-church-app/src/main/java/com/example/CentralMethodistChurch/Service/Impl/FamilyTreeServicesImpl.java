package com.example.CentralMethodistChurch.Service.Impl;

import com.example.CentralMethodistChurch.Entity.FamilyMember;
import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;
import com.example.CentralMethodistChurch.Enums.Gender;
import com.example.CentralMethodistChurch.Enums.MaritialStatus;
import com.example.CentralMethodistChurch.Repository.FamilyTreeRepository;
import com.example.CentralMethodistChurch.Repository.MemberRepository;
import com.example.CentralMethodistChurch.Service.FamilyTreeServices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FamilyTreeServicesImpl implements FamilyTreeServices {

    private final FamilyTreeRepository familyTreeRepository;
    private final MemberRepository memberRepository;

    public FamilyTreeServicesImpl(FamilyTreeRepository familyTreeRepository,
                                  MemberRepository memberRepository) {
        this.familyTreeRepository = familyTreeRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public List<FamilySubscriptions> fetchFamilyTrees() {
        return familyTreeRepository.findAll();
    }

    @Override
    @Transactional
    public void indexFamilyTrees() {
        List<FamilyMember> allMembers = memberRepository.findAll();
        sanitiseNaValues(allMembers);
        memberRepository.saveAll(allMembers);

        Map<String, List<FamilyMember>> byFamily = allMembers.stream()
                .filter(m -> m.getFamilyId() != null && !m.getFamilyId().trim().isEmpty())
                .collect(Collectors.groupingBy(m -> m.getFamilyId().trim()));

        byFamily.forEach(this::processFamily);
    }

    private void processFamily(String familyId, List<FamilyMember> members) {
        if (members == null || members.isEmpty()) return;

        // 1. Fetch or create the parent record
        FamilySubscriptions family = familyTreeRepository
                .findById(familyId)
                .orElseGet(() -> {
                    FamilySubscriptions fs = new FamilySubscriptions();
                    fs.setFamilyId(familyId);
                    return familyTreeRepository.save(fs);
                });

        // 2. Identify the head for metadata
        FamilyMember head = findHead(members);
        if (head != null && family.getHeadMemberId() == null) {
            family.setHeadMemberId(String.valueOf(head.getMembershipId()));
        }

        // 3. Link members using the bidirectional helper method
        // This ensures the Set<FamilyMember> is updated AND the member's reference is set
        members.stream()
                .filter(m -> m.getFamilyId() != null && m.getFamilyId().trim().equals(familyId))
                .forEach(family::addMember);

        // 4. Save the parent ONLY
        // Hibernate's persistence context detects the updated Set and syncs the FKs
        familyTreeRepository.save(family);
    }

    private FamilyMember findHead(List<FamilyMember> members) {
        if (members == null || members.isEmpty()) return null;

        FamilyMember head = members.stream()
                .filter(m -> Gender.Male == m.getGender() && MaritialStatus.Married == m.getMaritalStatus())
                .findFirst().orElse(null);

        if (head == null) {
            head = members.stream()
                    .filter(m -> MaritialStatus.Married == m.getMaritalStatus())
                    .findFirst().orElse(null);
        }

        if (head == null) {
            head = members.stream()
                    .filter(m -> isBlank(m.getSpouseId()))
                    .findFirst().orElse(null);
        }

        return head != null ? head : members.get(0);
    }

    private void sanitiseNaValues(List<FamilyMember> allMembers) {
        for (FamilyMember m : allMembers) {
            m.setFatherId(nullifyNa(m.getFatherId()));
            m.setMotherId(nullifyNa(m.getMotherId()));
            m.setSpouseId(nullifyNa(m.getSpouseId()));
        }
    }

    private String nullifyNa(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return (trimmed.isEmpty() || trimmed.equalsIgnoreCase("N/A") || trimmed.equalsIgnoreCase("NA")) ? null : trimmed;
    }

    private boolean isBlank(String value) {
        if (value == null) return true;
        String trimmed = value.trim();
        return trimmed.isEmpty() || trimmed.equalsIgnoreCase("N/A") || trimmed.equalsIgnoreCase("NA");
    }

    @Override
    @Transactional
    public void truncateFamilyTree() {
        familyTreeRepository.deleteAll();
    }

    @Override
    public long getCurrentYearPledge(final long familyId) {
        return familyTreeRepository.findById(String.valueOf(familyId))
                .map(family -> {
                    LocalDate pledgeStartThisYear = family.getPledgeStartDate().withYear(LocalDate.now().getYear());
                    long monthsElapsed = ChronoUnit.MONTHS.between(pledgeStartThisYear, LocalDate.now());
                    return monthsElapsed > 0 ? monthsElapsed * family.getPledgeAmount() : 0L;
                })
                .orElse(0L);
    }
}