/**
 * @author Ashwini Charles on 3/3/2024
 * @project CentralMethodistChurch
 */

package com.example.CentralMethodistChurch.Repository;

import com.example.CentralMethodistChurch.Entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<FamilyMember, String> {

}
