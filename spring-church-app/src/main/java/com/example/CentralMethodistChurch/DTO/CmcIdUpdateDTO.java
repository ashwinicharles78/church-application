package com.example.CentralMethodistChurch.DTO;

// Create a new DTO: CmcIdUpdateDTO.java
public class CmcIdUpdateDTO {
    private Long membershipId;
    private String cmcMembershipId;

    public Long getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(Long membershipId) {
        this.membershipId = membershipId;
    }

    public String getCmcMembershipId() {
        return cmcMembershipId;
    }

    public void setCmcMembershipId(String cmcMembershipId) {
        this.cmcMembershipId = cmcMembershipId;
    }
    // Getters and Setters
}
