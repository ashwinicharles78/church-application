/**
 * @author Ashwini Charles on 3/3/2024
 * @project CentralMethodistChurch
 */

package com.example.CentralMethodistChurch.Controller;

import com.example.CentralMethodistChurch.DTO.Events;
import com.example.CentralMethodistChurch.DTO.FamilyIdData;
import com.example.CentralMethodistChurch.Entity.FamilyMember;
import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;
import com.example.CentralMethodistChurch.Repository.MemberRepository;
import com.example.CentralMethodistChurch.Service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CMCMembersController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping(path = "/all-members")
    private List<FamilyMember> fetchAllMembers() {
        return memberService.fetchAllMembers();
    }

    @GetMapping(path = "/member-id/{id}")
    private FamilyMember fetchById(@PathVariable("id") String id) {
        return memberService.fetchById(id);
    }

    @PostMapping(path = "/members")
    public List<FamilyMember> saveAllMembers(@RequestBody List<FamilyMember> members) {
        return memberService.saveAllMembers(members);
    }
    @PostMapping(path = "/member")
    public FamilyMember saveMember(@RequestBody FamilyMember member) {
        memberService.saveMember(member);
        if(fetchById(String.valueOf(member.getMembershipId())) != null) {
            FamilySubscriptions sub = memberService.fetchById(String.valueOf(member.getMembershipId())).getFamilySubscription();
            if(sub != null)
                sub.addMember(member);
        }
        return memberService.saveMember(member);
    }

    @PutMapping(path = "/member-id/{id}")
    private FamilyMember editMember(@PathVariable("id") String id, @RequestBody FamilyMember familyMember) {
        return memberService.updateMember(id, familyMember);
    }

    @GetMapping(path = "/events")
    private List<Events> getUpcomingEvents() {
        return memberService.getEvents();
    }

    @DeleteMapping(path = "/truncate-members")
    private void truncateFamilyTree() {
        memberService.truncateMembers();
    }

    @DeleteMapping(path = "/member-id/{id}")
    private void deleteMember(@PathVariable("id") String id) {
        memberService.deleteMemberbyId(id);
    }

    @PutMapping(path = "/bulk-update-family-ids")
    public ResponseEntity<String> bulkUpdateFamilyIds(@RequestBody List<FamilyIdData> updates) {

        List<FamilyMember> membersToUpdate = new ArrayList<>();

        // Fetch the specific members, update their family ID, and add to list
        for (FamilyIdData update : updates) {
            memberRepository.findById(String.valueOf(update.getMembershipId())).ifPresent(member -> {
                member.setFamilyId(update.getFamilyId());
                membersToUpdate.add(member);
            });
        }

        // Save all changes to the database in one single batch query!
        memberRepository.saveAll(membersToUpdate);

        return ResponseEntity.ok("Successfully updated " + membersToUpdate.size() + " members.");
    }
}
