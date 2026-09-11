import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { Member } from '../memeber';

@Component({
  selector: 'app-members-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './members-list.component.html',
  styleUrl: './members-list.component.css'
})
export class MembersListComponent {

  members: Member[] = [];
  memberToDelete: any;
  isDeleting: boolean | undefined;
  editedFamilyIds = new Map<number, string>();
  editedCmcMembershipIds = new Map<number, string>();
  familyIdErrors = new Map<number, string>();
  cmcMembershipIdErrors = new Map<number, string>();
  isSavingFamilyIds = false;
  isSavingCmcMembershipIds = false;
  familyIdSaveMessage = '';
  cmcMembershipIdSaveMessage = '';


  constructor(private http:HttpClient, private router: Router) {
  }

ngOnInit() {
  this.http.get<Member[]>("http://localhost:8080/all-members")
    .subscribe((data) => this.members = data);
}

  editMember(memberId: any) {
    this.router.navigate(['/edit', memberId]);
  }

  addMember() {
    this.router.navigate(['/add']);
  }

  previewMember(memberId: any) {
    this.router.navigate(['/preview', memberId]);
  }
 // ─────────────────────────────────────────────────────────────
  // Delete flow
  // ─────────────────────────────────────────────────────────────

  /** Step 1 — open the confirmation modal */
  confirmDelete(member: Member): void {
    this.memberToDelete = member;
  }

  onFamilyIdInput(memberId: number, event: Event): void {
    const newValue = (event.target as HTMLInputElement).value;
    const member = this.members.find(item => item.membershipId === memberId);
    const originalValue = String(member?.familyId ?? '');

    if (newValue === originalValue) {
      this.editedFamilyIds.delete(memberId);
    } else {
      this.editedFamilyIds.set(memberId, newValue);
    }

    this.familyIdSaveMessage = '';
    this.familyIdErrors.delete(memberId);
  }

  onCmcMembershipIdInput(memberId: number, event: Event): void {
    const newValue = (event.target as HTMLInputElement).value;
    const member = this.members.find(item => item.membershipId === memberId);
    const originalValue = String(member?.cmcmembershipId ?? '');

    if (newValue === originalValue) {
      this.editedCmcMembershipIds.delete(memberId);
    } else {
      this.editedCmcMembershipIds.set(memberId, newValue);
    }

    this.cmcMembershipIdSaveMessage = '';
    this.cmcMembershipIdErrors.delete(memberId);
  }

  saveFamilyIds(): void {
    if (this.isSavingFamilyIds || this.editedFamilyIds.size === 0) return;

    if (!this.validateFamilyIds()) return;

    const changes = new Map(this.editedFamilyIds);
    const payload = Array.from(changes, ([membershipId, familyId]) => ({
      membershipId,
      familyId
    }));

    this.isSavingFamilyIds = true;
    this.familyIdSaveMessage = '';
    this.bulkUpdateFamilyIds(payload).subscribe({
      next: (res) => {
        changes.forEach((familyId, membershipId) => {
          if (this.editedFamilyIds.get(membershipId) === familyId) {
            const member = this.members.find(item => item.membershipId === membershipId);
            if (member) member.familyId = familyId || null;
            this.editedFamilyIds.delete(membershipId);
          }
        });
        this.familyIdSaveMessage = 'Family IDs saved.';
        this.isSavingFamilyIds = false;
      },
      error: (err) => {
        console.error('Error saving family IDs', err);
        this.familyIdSaveMessage = 'Could not save Family IDs. Try again.';
        this.isSavingFamilyIds = false;
      }
    });
  }

  saveCmcMembershipIds(): void {
    if (this.isSavingCmcMembershipIds || this.editedCmcMembershipIds.size === 0) return;

    if (!this.validateCmcMembershipIds()) return;

    const changes = new Map(this.editedCmcMembershipIds);
    const payload = Array.from(changes, ([membershipId, CMCMembershipId]) => ({
      membershipId,
      cmcMembershipId: CMCMembershipId
    }));

    this.isSavingCmcMembershipIds = true;
    this.cmcMembershipIdSaveMessage = '';
    this.bulkUpdateCmcIds(payload).subscribe({
      next: (res) => {
        changes.forEach((CMCMembershipId, membershipId) => {
          if (this.editedCmcMembershipIds.get(membershipId) === CMCMembershipId) {
            const member = this.members.find(item => item.membershipId === membershipId);
            if (member) member.cmcmembershipId = CMCMembershipId || null;
            this.editedCmcMembershipIds.delete(membershipId);
          }
        });
        this.cmcMembershipIdSaveMessage = 'CMC Membership IDs saved.';
        this.isSavingCmcMembershipIds = false;
      },
      error: (err) => {
        console.error('Error saving CMC Membership IDs', err);
        this.cmcMembershipIdSaveMessage = 'Could not save CMC Membership IDs. Try again.';
        this.isSavingCmcMembershipIds = false;
      }
    });
  }

  private validateFamilyIds(): boolean {
    this.familyIdErrors.clear();
    return true;
  }

  private validateCmcMembershipIds(): boolean {
    let valid = true;
    this.cmcMembershipIdErrors.clear();
    this.editedCmcMembershipIds.forEach((cmcMembershipId, membershipId) => {
      if (cmcMembershipId.trim() === '') return;
      if (cmcMembershipId.trim().length > 100) {
        this.cmcMembershipIdErrors.set(membershipId, 'CMC Membership ID is too long.');
        valid = false;
      }
    });
    return valid;
  }
  /** Step 2 — user confirmed: call DELETE /member-id/{id} */
  deleteMember(): void {
    if (!this.memberToDelete || this.isDeleting) return;

    this.isDeleting = true;
    const id = this.memberToDelete.membershipId;

    this.http.delete("http://localhost:8080/member-id/"+id).subscribe({
      next: () => {
        // Remove from local list — no need to re-fetch
        this.members = this.members.filter((m:Member) => m.membershipId !== id);
        this.resetDeleteState();
      },
      error: (err) => {
        console.error('Failed to delete member', err);
        this.isDeleting = false;
        // TODO: surface an error toast/snackbar to the user
      },
    });
  }

  /** Cancel or close the modal without deleting */
  cancelDelete(): void {
    if (this.isDeleting) return; // block dismiss while request is in-flight
    this.resetDeleteState();
  }

  private resetDeleteState(): void {
    this.memberToDelete = null;
    this.isDeleting = false;
  }

  // In your Angular Service
  bulkUpdateFamilyIds(updates: {membershipId: number, familyId: string}[]) {
    return this.http.put("http://localhost:8080/bulk-update-family-ids", updates, { responseType: 'text' });
  }

  bulkUpdateCmcIds(updates: {membershipId: number, cmcMembershipId: string}[]) {
    return this.http.post("http://localhost:8080/bulk-update-cmc-ids", updates, { responseType: 'text' });
}
}
