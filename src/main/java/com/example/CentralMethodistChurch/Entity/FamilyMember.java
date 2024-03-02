/**
 * @author Ashwini Charles on 3/3/2024
 * @project CentralMethodistChurch
 */
package com.example.CentralMethodistChurch.Entity;

import com.example.CentralMethodistChurch.Enums.Gender;
import com.example.CentralMethodistChurch.Enums.MaritialStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table
@Getter @Setter @NoArgsConstructor
public class FamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String membershipId;
    private String title;
    private String lastName;
    private String middleName;
    private String firstName;
    private String fatherName;
    private String fatherId;
    private String motherName;
    private String motherId;
    private String spouseName;
    private String spouseId;
    private Gender gender;
    private LocalDate DOB;
    private Integer age;
    private MaritialStatus maritialStatus;
    private LocalDate dateOfMarriage;
    private Integer yearsOfMarriage;
    private String contact;
    private String email;
    private Boolean baptised;
    private LocalDate baptisedDate;
    private Boolean confirmed;
    private LocalDate confirmationDate;
    private Boolean fullMember;
    private Boolean NonResidentMember;
    private Boolean preparatoryMember;
    private Boolean selfDependent;
    private String pledgeNumber;
    private Double pledgeAmount;
    private Boolean status;
    private String inactiveReason;
    private String inactiveSince;

}
