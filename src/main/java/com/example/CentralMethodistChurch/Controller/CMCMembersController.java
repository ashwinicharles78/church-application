/**
 * @author Ashwini Charles on 3/3/2024
 * @project CentralMethodistChurch
 */

package com.example.CentralMethodistChurch.Controller;

import com.example.CentralMethodistChurch.Entity.FamilyMember;
import com.example.CentralMethodistChurch.Service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
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
}
