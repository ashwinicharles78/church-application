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
import java.util.Set;
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
        memberRepository.saveAll(allMembers); // Persist sanitised basic fields first

        // Group active members by their declared familyId
        Map<String, List<FamilyMember>> byFamily = allMembers.stream()
                .filter(m -> m.getFamilyId() != null && !m.getFamilyId().trim().isEmpty())
                .collect(Collectors.groupingBy(m -> m.getFamilyId().trim()));

        // 1. Process active families (creates new ones and updates existing)
        byFamily.forEach(this::processFamily);

        // 2. Clean up completely orphaned families
        // If a family previously existed but now has 0 members mapped to its ID, remove it
        Set<String> activeFamilyIds = byFamily.keySet();
        List<FamilySubscriptions> existingFamilies = familyTreeRepository.findAll();

        for (FamilySubscriptions existingFamily : existingFamilies) {
            if (!activeFamilyIds.contains(existingFamily.getFamilyId())) {
                existingFamily.getMembers().clear(); // Sever all bidirectional links
                familyTreeRepository.delete(existingFamily);
            }
        }
    }

    private void processFamily(String familyId, List<FamilyMember> currentMembers) {
        if (currentMembers == null || currentMembers.isEmpty()) return;

        // 1. Fetch existing parent record, or create and MANAGE a clean new one
        FamilySubscriptions family = familyTreeRepository.findById(familyId).orElse(null);

        if (family == null) {
            FamilySubscriptions newFamily = new FamilySubscriptions();
            newFamily.setFamilyId(familyId);

            // 🚨 CRITICAL FIX: Because FamilySubscriptions uses a manually assigned ID (String),
            // repository.save() performs a merge() and returns a NEW managed instance.
            // We MUST reassign our variable to capture this managed instance before linking children.
            family = familyTreeRepository.save(newFamily);
        }

        // 2. Re-evaluate and dynamically update the Head Member
        FamilyMember head = findHead(currentMembers);
        if (head != null) {
            family.setHeadMemberId(String.valueOf(head.getMembershipId()));
        } else {
            family.setHeadMemberId(null);
        }

        // 3. Safely sync the bidirectional relationships
        Set<FamilyMember> existingFamilySet = family.getMembers();

        Set<Long> incomingMemberIds = currentMembers.stream()
                .map(FamilyMember::getMembershipId)
                .collect(Collectors.toSet());

        // A. Remove members that left this family (Orphan cleanup)
        existingFamilySet.removeIf(existingMember -> {
            boolean isLeaving = !incomingMemberIds.contains(existingMember.getMembershipId());
            if (isLeaving) {
                existingMember.setFamilySubscription(null); // Sever child-to-parent tie
            }
            return isLeaving;
        });

        // B. Add new members or enforce existing links
        for (FamilyMember incomingMember : currentMembers) {
            boolean alreadyInFamily = existingFamilySet.stream()
                    .anyMatch(m -> m.getMembershipId() == incomingMember.getMembershipId());

            if (!alreadyInFamily) {
                family.addMember(incomingMember); // Bidirectional helper correctly assigns parent
            } else {
                // Enforce that the incoming member points to the managed parent reference
                incomingMember.setFamilySubscription(family);
            }
        }

        // 4. Save parent to cascade the final updates
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