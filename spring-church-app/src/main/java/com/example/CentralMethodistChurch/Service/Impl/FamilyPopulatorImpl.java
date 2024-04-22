package com.example.CentralMethodistChurch.Service.Impl;


import com.example.CentralMethodistChurch.Entity.FamilyMember;
import com.example.CentralMethodistChurch.Service.FamilyPopulator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * @author Ashwini Charles on 4/22/2024
 * @project CentralMethodistChurch
 */

@Service
public class FamilyPopulatorImpl implements FamilyPopulator {

    /**
     * @param familyMember
     */
    @Override
    public void populateFamily(FamilyMember familyMember) {
        if(null != familyMember.getDob())
            familyMember.setAge(String.valueOf(ChronoUnit.YEARS.between(familyMember.getDob(), LocalDate.now())));
        if(null != familyMember.getDateOfMarriage())
            familyMember.setYearsOfMarriage(String.valueOf(ChronoUnit.YEARS.between(familyMember.getDateOfMarriage(), LocalDate.now())));
    }
}
