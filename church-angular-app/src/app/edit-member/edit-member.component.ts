import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-edit-member',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './edit-member.component.html',
  styleUrl: './edit-member.component.css'
})
export class EditMemberComponent implements OnInit {
  personForm!: FormGroup;
  persons: any; // This should contain the provided data

  constructor(private formBuilder: FormBuilder, private http: HttpClient) { }

  ngOnInit(): void {
    let response = this.http.get("http://localhost:8080/all-members");
    response.subscribe((data)=> this.persons = data)
    // Initialize the form with empty values or with provided data
    this.personForm = this.formBuilder.group({
      Title: ['', Validators.required],
      lastName: ['', Validators.required],
      middleName: [''],
      firstName: ['', Validators.required],
      fatherName: ['', Validators.required],
      spouseName: [''],
      gender: ['', Validators.required],
      dob: ['', Validators.required],
      age: ['', Validators.required],
      maritalStatus: ['', Validators.required],
      dateOfMarriage: [''],
      yearsOfMarriage: [''],
      Address: ['', Validators.required],
      contact: ['', Validators.required],
      Email: [''],
      Baptised: ['', Validators.required],
      baptisedDate: [''],
      Confirmed: ['', Validators.required],
      ConfirmationDate: [''],
      fullMember: ['', Validators.required],
      NonResidentMember: [''],
      preparatoryMember: [''],
      selfDependent: [''],
      status: ['', Validators.required],
      inactiveReason: [''],
      inactiveSince: ['']
    });
  }

  onSubmit(): void {
    // Handle form submission - Update the data with form values
    console.log(this.personForm.value);
    // You can then update the corresponding item in the persons array with the updated form values.
  }
}