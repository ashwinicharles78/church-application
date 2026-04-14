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
  editedMembers = new Map<number, string>(); 
  isSaving = false;


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

  // Triggered whenever a user types in the input box
  onFamilyIdInput(memberId: number, event: any) {
    const newValue = event.target.value;
    this.editedMembers.set(memberId, newValue);
  }

  // Triggered by the "Save All" button
  saveAllChanges() {
    if (this.editedMembers.size === 0) return; // Nothing to save

    this.isSaving = true;

    // Convert the Map into an Array of objects for the backend
    const payload = Array.from(this.editedMembers, ([membershipId, familyId]) => ({
      membershipId,
      familyId
    }));

    this.bulkUpdateFamilyIds(payload).subscribe({
      next: (res) => {
        alert('All changes saved successfully!');
        this.editedMembers.clear(); // Clear the tracking map
        this.isSaving = false;
      },
      error: (err) => {
        console.error('Error saving changes', err);
        alert('Failed to save changes.');
        this.isSaving = false;
      }
    });
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
}
