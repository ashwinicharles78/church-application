/**
 * @author Ashwini Charles on 3/3/2024
 * @project CentralMethodistChurch
 */

package com.example.CentralMethodistChurch.Controller;

import com.example.CentralMethodistChurch.DTO.Events;
import com.example.CentralMethodistChurch.Entity.FamilyMember;
import com.example.CentralMethodistChurch.Service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class CMCMembersController {

    @Autowired
    private MemberService memberService;

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

    @PutMapping(path = "/member-id/{id}")
    private FamilyMember editMember(@PathVariable("id") String id, @RequestBody FamilyMember familyMember) {
        return memberService.updateMember(id, familyMember);
    }

    @GetMapping(path = "/events")
    private List<Events> getUpcomingEvents() {
        return memberService.getEvents();
    }
}
