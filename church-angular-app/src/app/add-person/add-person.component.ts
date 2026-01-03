// add-person.component.ts
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-add-person',
  standalone: true, 
  imports: [ReactiveFormsModule],
  templateUrl: './add-person.component.html'
})
export class AddPersonComponent implements OnInit {

  addPersonForm!: FormGroup;
  persons: any;

  constructor(private fb: FormBuilder, private http: HttpClient, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.addPersonForm = this.fb.group({
      title:              ["", Validators.required],
      lastName:           ["", Validators.required],
      middleName:         [""],
      firstName:          ["", Validators.required],
      fatherName:         ["", Validators.required],
      fatherId:           [""],
      motherName:         ["", Validators.required],
      motherId:           [""],
      spouseName:         [""],
      spouseId:           [""],
      gender:             ["", Validators.required],
      dob:                ["", Validators.required],
      age:                ["", Validators.required],
      maritalStatus:      [""],
      dateOfMarriage:     [""],
      yearsOfMarriage:    [""],
      address:            ["", Validators.required],
      contact:            ["", Validators.required],
      Email:              [""],
      Baptised:           ["", Validators.required],
      baptisedDate:       ["" ],
      Confirmed:          ["", Validators.required],
      ConfirmationDate:   [""],
      fullMember:         ["", Validators.required],
      NonResidentMember:  [""],
      preparatoryMember:  [""],
      selfDependent:      [""],
      status:             ["", Validators.required],
      inactiveReason:     [""],
      inactiveSince:      [""]
  });
}

  onSubmit(): void {
    const payload = this.addPersonForm.value;
    let response = this.http.post("http://localhost:8080/member/",this.addPersonForm.value);
    response.subscribe((data: any)=> this.persons = data);
    console.log(response);
  }
}
