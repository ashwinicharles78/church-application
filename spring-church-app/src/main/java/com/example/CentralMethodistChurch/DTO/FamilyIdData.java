package com.example.CentralMethodistChurch.DTO;

public class FamilyIdData {
    // Create this in your DTO folder (e.g., FamilyIdUpdateDTO.java)

        private long membershipId;
        private String familyId;

        // Getters and Setters
        public long getMembershipId() { return membershipId; }
        public void setMembershipId(long membershipId) { this.membershipId = membershipId; }
        public String getFamilyId() { return familyId; }
        public void setFamilyId(String familyId) { this.familyId = familyId; }

}
