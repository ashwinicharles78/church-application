import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe, NgFor, NgIf } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-preview-member-component',
  standalone: true,
  imports: [NgFor, NgIf],
  templateUrl: './preview-member-component.component.html',
  styleUrl: './preview-member-component.component.css'
})

export class PreviewMemberComponent implements OnInit {
  addPersonForm!: FormGroup;
  loading = true;
  personId: any;
  memberData: any;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {
}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadMemberData(id);
    }
  }

  loadMemberData(id: string) {
     this.personId = id;
     this.http.get("http://localhost:8080/member-id/" + id)
       .subscribe(data => this.memberData = data);
   }

  goBack() {
    this.router.navigate(['/members']);
  }

  printPage() {
    window.print();
  }
}