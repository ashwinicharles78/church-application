import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-edit-this.',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './edit-member.component.html',
  styleUrl: './edit-member.component.css'
})
export class EditMemberComponent implements OnInit {
  personForm!: FormGroup;
  persons: any;
  personId: any;

  constructor(private formBuilder: FormBuilder, private http: HttpClient, private route: ActivatedRoute) { }

  ngOnInit(): void {
    // let response = this.http.get("http://localhost:8080/all-members");
    // response.subscribe((data)=> this.persons = data)
    // Initialize the form with empty values or with provided data
    
    this.route.params.subscribe(params => {
      this.personId = params['id'];
      // console.log(this.personId);
    });
    
    let response = this.http.get("http://localhost:8080/member-id/"+this.personId);
    response.subscribe((data)=> this.persons = data);

    this.personForm = this.formBuilder.group({
      title:              [this.persons.title || "", Validators.required],
      lastName:           [this.persons.lastName || "", Validators.required],
      middleName:         [this.persons.middleName || ""],
      firstName:          [this.persons.firstName || "", Validators.required],
      fatherName:         [this.persons.fatherName  || "", Validators.required],
      spouseName:         [this.persons.spouseName || ""],
      gender:             [this.persons.gender || "", Validators.required],
      dob:                [this.persons.dob || "", Validators.required],
      age:                [this.persons.age || "", Validators.required],
      maritalStatus:      [this.persons.maritalStatus || "", Validators.required],
      dateOfMarriage:     [this.persons.dateOfMarriage || ""],
      yearsOfMarriage:    [this.persons.yearsOfMarriage || ""],
      address:            [this.persons.address || "", Validators.required],
      contact:            [this.persons.contact || "", Validators.required],
      Email:              [this.persons.email || ""],
      Baptised:           [this.persons.baptised || "", Validators.required],
      baptisedDate:       [this.persons.baptisedDate || "" ],
      Confirmed:          [this.persons.confirmed || "", Validators.required],
      ConfirmationDate:   [this.persons.confirmationDate || ""],
      fullMember:         [this.persons.fullMember || "", Validators.required],
      NonResidentMember:  [this.persons.nonResidentMember || ""],
      preparatoryMember:  [this.persons.preparatoryMembe || ""],
      selfDependent:      [this.persons.selfDependent || ""],
      status:             [this.persons.status || "", Validators.required],
      inactiveReason:     [this.persons.inactiveReason || ""],
      inactiveSince:      [this.persons.inactiveSince || ""]
    });

    // console.log(this.persons);
  }

  onSubmit(): void {
    // Handle form submission - Update the data with form values
    console.log(this.personForm.value);
    // You can then update the corresponding item in the persons array with the updated form values.
    let response = this.http.put("http://localhost:8080/member-id/"+this.personId, this.personForm.value);
    response.subscribe((data)=> this.persons = data);
    console.log(response);
  }
}