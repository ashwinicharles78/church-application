package com.example.CentralMethodistChurch.Service;

import com.example.CentralMethodistChurch.Entity.FamilySubscriptions;

import java.util.List;

/**
 * @author Ashwini Charles on 4/27/2024
 * @project spring-church-app
 */
public interface FamilyTreeServices {
    /**
     *
     * @return
     */
    List<FamilySubscriptions> fetchFamilyTrees();

    void indexFamilyTrees();

    void truncateFamilyTree();

    long getCurrentYearPledge(final long familyId);
}
