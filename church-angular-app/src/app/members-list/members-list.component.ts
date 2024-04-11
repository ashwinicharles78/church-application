import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';

@Component({
  selector: 'app-members-list',
  standalone: true,
  imports: [],
  templateUrl: './members-list.component.html',
  styleUrl: './members-list.component.css'
})
export class MembersListComponent {
  members: any;

  constructor(private http:HttpClient) {
  }

  ngOnInit() {
    let response = this.http.get("http://localhost:8080/all-members");
    response.subscribe((data)=> this.members = data)
  }

}
