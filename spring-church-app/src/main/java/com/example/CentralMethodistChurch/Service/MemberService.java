/**
 * @author Ashwini Charles on 3/3/2024
 * @project CentralMethodistChurch
 */
package com.example.CentralMethodistChurch.Service;

import com.example.CentralMethodistChurch.DTO.Events;
import com.example.CentralMethodistChurch.Entity.FamilyMember;

import java.util.List;

public interface MemberService {
    /**
     *
     * @return
     */
    List<FamilyMember> fetchAllMembers();

    /**
     *
     * @param id
     * @return
     */
    /**
     *
     * @param id
     * @return
     */
    FamilyMember fetchById(String id);

    /**
     *
     * @param Id
     */
    void deleteMemberbyId(String Id);

    /**
     *
     * @param member
     * @return
     */
    FamilyMember saveMember(FamilyMember member);

    /**
     *
     * @param members
     * @return
     */
    List<FamilyMember> saveAllMembers(List<FamilyMember> members);

    /**
     *
     * @param id
     * @param familyMember
     * @return
     */
    FamilyMember updateMember(String id, FamilyMember familyMember);

    List<Events> getEvents();
}
