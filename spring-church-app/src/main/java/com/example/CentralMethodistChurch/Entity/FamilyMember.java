/**
 * @author Ashwini Charles on 3/3/2024
 * @project CentralMethodistChurch
 */
package com.example.CentralMethodistChurch.Entity;

import com.example.CentralMethodistChurch.Enums.Gender;
import com.example.CentralMethodistChurch.Enums.MaritialStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;

@Entity
@Table
@Getter @Setter @NoArgsConstructor
public class FamilyMember {
    @Id
    @UuidGenerator
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
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd-MMM-yyyy")
    private LocalDate dob;
    private String age;
    private MaritialStatus maritalStatus;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd-MMM-yyyy")
    private LocalDate dateOfMarriage;
    private String yearsOfMarriage;
    private String contact;
    private String email;
    private String baptised;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd-MMM-yyyy")
    private LocalDate baptisedDate;
    private String confirmed;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd-MMM-yyyy")
    private LocalDate confirmationDate;
    private String fullMember;
    private String nonResidentMember;
    private String preparatoryMember;
    private String selfDependent;
    private String pledgeNumber;
    private String pledgeAmount;
    private String status;
    private String inactiveReason;
    private String inactiveSince;

}
