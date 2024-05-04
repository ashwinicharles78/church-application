package com.example.CentralMethodistChurch.Repository;

import com.example.CentralMethodistChurch.Entity.FamilyMember;
import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Ashwini Charles on 4/27/2024
 * @project spring-church-app
 */
@Repository
public interface FamilyTreeRepository extends JpaRepository<FamilySubscriptions, String> {

}
