package com.example.CentralMethodistChurch.Controller;

import com.example.CentralMethodistChurch.Entity.FamilyMember;
import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;
import com.example.CentralMethodistChurch.Service.FamilyTreeServices;
import com.example.CentralMethodistChurch.Service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Ashwini Charles on 4/27/2024
 * @project spring-church-app
 */

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class CMCMembersTreeController {

    @Autowired
    private FamilyTreeServices familyTreeServices;

    @GetMapping(path = "/members-tree")
    private List<FamilySubscriptions> fetchFamilyTree() {
        return familyTreeServices.fetchFamilyTrees();
    }

    @GetMapping(path = "/index-tree")
    private String indexFamilyTree() {

        familyTreeServices.indexFamilyTrees();
        return "Success";
    }

    @DeleteMapping(path = "/truncate-tree")
    private void truncateFamilyTree() {
        familyTreeServices.truncateFamilyTree();
    }
}
