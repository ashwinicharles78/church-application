import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from 'express';
import { NgFor } from '@angular/common';
import { Member } from '../memeber';

@Component({
  selector: 'app-family-tree',
  standalone: true,
  imports: [NgFor],
  templateUrl: './family-tree.component.html',
  styleUrl: './family-tree.component.css'
})
export class FamilyTreeComponent implements OnInit {
  families: any;

  constructor(private http:HttpClient) { }

  ngOnInit(){
    let response = this.http.get("http://localhost:8080/members-tree");
    response.subscribe((data)=> this.families = data);
  }

  getMemberName(memberId: string, members: Member[]): string {
    const member = members.find(m => m.membershipId.toString() === memberId);
    return member ? member.firstName + ' ' + member.lastName : 'N/A';
  }
}
