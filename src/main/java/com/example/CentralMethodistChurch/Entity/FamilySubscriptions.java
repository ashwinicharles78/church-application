package com.example.CentralMethodistChurch.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class FamilySubscriptions {
    @Id
    private String familyId;
    @OneToMany
    private List<FamilyMember> members;
}
