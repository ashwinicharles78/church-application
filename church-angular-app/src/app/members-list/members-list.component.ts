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
}
