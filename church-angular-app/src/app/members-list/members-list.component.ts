import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { RouterLink, Router } from '@angular/router';

@Component({
  selector: 'app-members-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './members-list.component.html',
  styleUrl: './members-list.component.css'
})
export class MembersListComponent {

  members: any;

  constructor(private http:HttpClient, private router: Router) {
  }

  ngOnInit() {
    let response = this.http.get("http://localhost:8080/all-members");
    response.subscribe((data)=> this.members = data);
  }

  editMember(memberId: any) {
    this.router.navigate(['/edit', memberId]);
  }

}
