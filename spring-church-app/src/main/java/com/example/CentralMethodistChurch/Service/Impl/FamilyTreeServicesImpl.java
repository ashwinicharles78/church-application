package com.example.CentralMethodistChurch.Service.Impl;

import com.example.CentralMethodistChurch.Entity.FamilyMember;
import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;
import com.example.CentralMethodistChurch.Repository.FamilyTreeRepository;
import com.example.CentralMethodistChurch.Repository.MemberRepository;
import com.example.CentralMethodistChurch.Service.FamilyTreeServices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ashwini Charles on 4/27/2024
 * @project spring-church-app
 *
 * Key data quirk from the Excel/DB:
 *   - Empty / missing values are stored as "N/A" strings, not null or blank.
 *   - fatherId = "302" means a real link; fatherId = "N/A" or null means no father.
 *   - familyId blank on a child record = that child has not been grouped yet.
 *
 * Example rows:
 *   302  Sanjay   fatherId=N/A  spouseId=N/A  → root father
 *   303  Sunita   spouseId=302                → Sanjay's spouse (same family 52)
 *   304  Ashwini  fatherId=302  motherId=303  → unmarried child, must join family 52
 */
@Service
public class FamilyTreeServicesImpl implements FamilyTreeServices {

    private final FamilyTreeRepository familyTreeRepository;
    private final MemberRepository memberRepository;

    public FamilyTreeServicesImpl(FamilyTreeRepository familyTreeRepository,
                                  MemberRepository memberRepository) {
        this.familyTreeRepository = familyTreeRepository;
        this.memberRepository = memberRepository;
    }

    // -------------------------------------------------------------------------
    // Fetch
    // -------------------------------------------------------------------------

    @Override
    public List<FamilySubscriptions> fetchFamilyTrees() {
        return familyTreeRepository.findAll();
    }

    // -------------------------------------------------------------------------
    // Index
    // -------------------------------------------------------------------------

    /**
     * Groups all members into family units in four passes:
     *
     *  Pass 0 — Data repair:
     *      Normalise "N/A" sentinel values to null so all subsequent
     *      checks can use a single isEmpty() guard.
     *
     *  Pass 1 — Fathers:
     *      Members with no fatherId and a spouseId are root family heads.
     *      Their spouse and all unmarried children (fatherId == head's ID,
     *      no spouseId) are grouped into the same family.
     *
     *  Pass 2 — Single / widowed mothers:
     *      Members with no fatherId and no spouseId are single-parent heads.
     *      Their unmarried children (by motherId or fatherId) join the family.
     *
     *  Pass 3 — Married children:
     *      Any member still unassigned who has a spouseId becomes the head
     *      of their own new family, with their spouse and children.
     */
    @Override
    @Transactional
    public void indexFamilyTrees() {
        List<FamilyMember> allMembers = memberRepository.findAll();

        // Pass 0 — Sanitise "N/A" sentinel values → null
        sanitiseNaValues(allMembers);
        memberRepository.saveAll(allMembers);

        // Pass 1 — Root fathers
        processFathers(allMembers);

        // Pass 2 — Single / widowed mothers
        processMothers(allMembers);

        // Pass 3 — Married children who form their own family
        processMarriedChildren(allMembers);
    }

    // -------------------------------------------------------------------------
    // Pass 0 — Sanitise "N/A" → null
    // -------------------------------------------------------------------------

    /**
     * Converts "N/A" (and common variants) to null on all relevant ID fields
     * so that isEmpty() checks work uniformly throughout the rest of the logic.
     *
     * Fields sanitised: fatherId, motherId, spouseId, familyId (as String).
     */
    private void sanitiseNaValues(List<FamilyMember> allMembers) {
        for (FamilyMember m : allMembers) {
            m.setFatherId(nullifyNa(m.getFatherId()));
            m.setMotherId(nullifyNa(m.getMotherId()));
            m.setSpouseId(nullifyNa(m.getSpouseId()));
        }
    }

    /**
     * Returns null if the value is null, blank, or any case-insensitive
     * variant of "N/A" / "NA". Otherwise returns the trimmed value.
     */
    private String nullifyNa(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        if (trimmed.isEmpty() || trimmed.equalsIgnoreCase("N/A") || trimmed.equalsIgnoreCase("NA")) {
            return null;
        }
        return trimmed;
    }

    // -------------------------------------------------------------------------
    // Pass 1 — Fathers
    // -------------------------------------------------------------------------

    /**
     * A "father" is a member who:
     *   - has no familyId yet (unassigned)
     *   - has no fatherId (is a generation root, not someone else's child)
     *   - has a spouseId (is married — the family head)
     *
     * Groups: father + spouse + all unmarried children (fatherId == father's membershipId).
     *
     * NOTE: Sanjay (302) qualifies here because after Pass 0 his fatherId is
     * null (was "N/A") and his spouseId is null — wait, Sanjay has spouseId
     * null but Sunita (303) carries spouseId = "302". So Sanjay is picked up
     * in Pass 2 (single head) and Sunita is pulled in via assignSpouseWhoPointsToHead().
     *
     * For members where BOTH carry each other's IDs, this pass still works
     * because we look up spouseId in both directions.
     */
    private void processFathers(List<FamilyMember> allMembers) {
        List<FamilyMember> fathers = allMembers.stream()
                .filter(m -> m.getFamilyId() == null)
                .filter(m -> isBlank(m.getFatherId()))            // no father = generation root
                .filter(m -> !isBlank(m.getSpouseId()))           // has spouseId = married head
                .toList();

        for (FamilyMember father : fathers) {
            if (father.getFamilyId() != null) continue;

            List<FamilyMember> familyMembers = new ArrayList<>();
            FamilySubscriptions family = createFamilyFor(father, familyMembers);

            assignSpouseById(father, allMembers, family, familyMembers);
            assignUnmarriedChildren(father, allMembers, family, familyMembers);

            persistFamily(family, familyMembers);
        }
    }

    // -------------------------------------------------------------------------
    // Pass 2 — Single / widowed mothers (and fathers whose spouse field is null
    //          but another member points TO them via spouseId)
    // -------------------------------------------------------------------------

    /**
     * Targets members who:
     *   - have no familyId yet
     *   - have no fatherId (generation root)
     *   - have no spouseId themselves
     *
     * This covers:
     *   a) Single / widowed mothers.
     *   b) Fathers whose own spouseId field is null but whose spouse record
     *      carries spouseId pointing back to them (e.g. Sanjay 302 / Sunita 303).
     *
     * For case (b), assignSpouseWhoPointsToHead() scans for a member whose
     * spouseId matches the head's membershipId.
     */
    private void processMothers(List<FamilyMember> allMembers) {
        List<FamilyMember> singleHeads = allMembers.stream()
                .filter(m -> m.getFamilyId() == null)
                .filter(m -> isBlank(m.getFatherId()))
                .filter(m -> isBlank(m.getSpouseId()))
                .toList();

        for (FamilyMember head : singleHeads) {
            if (head.getFamilyId() != null) continue;

            List<FamilyMember> familyMembers = new ArrayList<>();
            FamilySubscriptions family = createFamilyFor(head, familyMembers);

            // Pull in a spouse who points back to this head (e.g. Sunita → Sanjay)
            assignSpouseWhoPointsToHead(head, allMembers, family, familyMembers);

            // Pull in all unmarried children by fatherId or motherId
            assignUnmarriedChildren(head, allMembers, family, familyMembers);

            persistFamily(family, familyMembers);
        }
    }

    // -------------------------------------------------------------------------
    // Pass 3 — Married children become new family heads
    // -------------------------------------------------------------------------

    /**
     * Any member still without a familyId who has a spouseId is a married
     * child. They become the head of their own new family, with their spouse
     * and their own unmarried children grouped alongside them.
     */
    private void processMarriedChildren(List<FamilyMember> allMembers) {
        List<FamilyMember> marriedChildren = allMembers.stream()
                .filter(m -> m.getFamilyId() == null)
                .filter(m -> !isBlank(m.getSpouseId()))
                .toList();

        for (FamilyMember head : marriedChildren) {
            if (head.getFamilyId() != null) continue;

            List<FamilyMember> familyMembers = new ArrayList<>();
            FamilySubscriptions family = createFamilyFor(head, familyMembers);

            assignSpouseById(head, allMembers, family, familyMembers);
            assignSpouseWhoPointsToHead(head, allMembers, family, familyMembers);
            assignUnmarriedChildren(head, allMembers, family, familyMembers);

            persistFamily(family, familyMembers);
        }
    }

    // -------------------------------------------------------------------------
    // Child & spouse assignment
    // -------------------------------------------------------------------------

    /**
     * Adds all unmarried children who belong to {@code head}'s family.
     *
     * A child belongs here if:
     *   - their familyId is null (not yet grouped)
     *   - their fatherId OR motherId equals the head's membershipId
     *   - they have no spouseId (unmarried — married children form their own family)
     *
     * Matching by both fatherId and motherId ensures children are found
     * regardless of which parent field was populated in the data entry.
     *
     * Example: Ashwini (304) has fatherId=302 and motherId=303.
     *   When processing Sanjay (302) this filter catches Ashwini via fatherId.
     *   If Sanjay were processed via Pass 2 as mother-head, motherId also works.
     */
    private void assignUnmarriedChildren(FamilyMember head,
                                         List<FamilyMember> allMembers,
                                         FamilySubscriptions family,
                                         List<FamilyMember> familyMembers) {
        String headId = String.valueOf(head.getMembershipId());

        List<FamilyMember> children = allMembers.stream()
                .filter(child -> child.getFamilyId() == null)
                .filter(child -> isBlank(child.getSpouseId()))                 // unmarried only
                .filter(child -> headId.equals(child.getFatherId())            // matched by fatherId
                        || headId.equals(child.getMotherId()))            // OR by motherId
                .toList();

        children.forEach(child -> child.setFamilyId(family.getFamilyId()));
        familyMembers.addAll(children);
    }

    /**
     * Assigns a spouse by looking up the head's own spouseId field
     * (i.e. head.spouseId → spouse.membershipId).
     */
    private void assignSpouseById(FamilyMember head,
                                  List<FamilyMember> allMembers,
                                  FamilySubscriptions family,
                                  List<FamilyMember> familyMembers) {
        if (isBlank(head.getSpouseId())) return;

        allMembers.stream()
                .filter(m -> m.getFamilyId() == null)
                .filter(m -> String.valueOf(m.getMembershipId()).equals(head.getSpouseId()))
                .findFirst()
                .ifPresent(spouse -> {
                    spouse.setFamilyId(family.getFamilyId());
                    familyMembers.add(spouse);
                });
    }

    /**
     * Assigns a spouse who points back to this head via their own spouseId field
     * (i.e. spouse.spouseId == head.membershipId).
     *
     * Handles the pattern where the head's spouseId is null but their partner's
     * record carries the link (e.g. Sanjay 302 has spouseId=null, but
     * Sunita 303 has spouseId="302").
     */
    private void assignSpouseWhoPointsToHead(FamilyMember head,
                                             List<FamilyMember> allMembers,
                                             FamilySubscriptions family,
                                             List<FamilyMember> familyMembers) {
        String headId = String.valueOf(head.getMembershipId());

        allMembers.stream()
                .filter(m -> m.getFamilyId() == null)
                .filter(m -> headId.equals(m.getSpouseId()))
                .findFirst()
                .ifPresent(spouse -> {
                    spouse.setFamilyId(family.getFamilyId());
                    familyMembers.add(spouse);
                });
    }

    // -------------------------------------------------------------------------
    // Shared helpers
    // -------------------------------------------------------------------------

    private FamilySubscriptions createFamilyFor(FamilyMember head,
                                                List<FamilyMember> familyMembers) {
        FamilySubscriptions family = familyTreeRepository.save(new FamilySubscriptions());
        head.setFamilyId(family.getFamilyId());
        familyMembers.add(head);
        return family;
    }

    private void persistFamily(FamilySubscriptions family,
                               List<FamilyMember> familyMembers) {
        family.setMembers(familyMembers);
        familyTreeRepository.save(family);
        memberRepository.saveAll(familyMembers);
    }

    /**
     * Returns true when a field is effectively empty —
     * null, blank, "N/A", or "NA" (case-insensitive).
     * After Pass 0 all N/A values are null, but this guard
     * is kept here as a safety net.
     */
    private boolean isBlank(String value) {
        if (value == null) return true;
        String trimmed = value.trim();
        return trimmed.isEmpty()
                || trimmed.equalsIgnoreCase("N/A")
                || trimmed.equalsIgnoreCase("NA");
    }

    // -------------------------------------------------------------------------
    // Reset
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void truncateFamilyTree() {
        familyTreeRepository.deleteAll();

        List<FamilyMember> members = memberRepository.findAll();
        members.forEach(m -> m.setFamilyId(null));
        memberRepository.saveAll(members);
    }

    // -------------------------------------------------------------------------
    // Pledge calculation
    // -------------------------------------------------------------------------

    @Override
    public long getCurrentYearPledge(final long familyId) {
        return familyTreeRepository.findById(String.valueOf(familyId))
                .map(family -> {
                    LocalDate pledgeStartThisYear = family.getPledgeStartDate()
                            .withYear(LocalDate.now().getYear());
                    long monthsElapsed = ChronoUnit.MONTHS.between(pledgeStartThisYear, LocalDate.now());
                    return monthsElapsed > 0 ? monthsElapsed * family.getPledgeAmount() : 0L;
                })
                .orElse(0L);
    }
}