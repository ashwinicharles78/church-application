import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';

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

  constructor(private formBuilder: FormBuilder, private http: HttpClient, private route: ActivatedRoute,  private router: Router) { }

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
      familyId:          [this.persons.familyId || ""],
      fatherName:         [this.persons.fatherName  || "", Validators.required],
      fatherId:         [this.persons.fatherId  || "", Validators.required],
      motherName:         [this.persons.motherName  || "", Validators.required],
      motherId:         [this.persons.motherId  || "", Validators.required],
      spouseName:         [this.persons.spouseName || ""],
      spouseId:           [this.persons.spouseId || ""],
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

  
onDateInput(event: Event, controlName: string): void {
  const input = event.target as HTMLInputElement;
  let v = (input.value || '').trim();

  // allow only digits, letters, dash
  v = v.replace(/[^0-9A-Za-z-]/g, '');

  // auto insert dash after dd
  if (v.length === 2 && !v.endsWith('-')) v += '-';

  // auto insert dash after dd-MMM (2 digits + '-' + 3 letters)
  if (v.length === 6 && v[2] === '-' && v[5] !== '-') v += '-';

  // When complete: normalize to dd-Mmm-yyyy
  if (v.length === 11 && this.isValidDateFormat(v)) {
    const [dd, mmmRaw, yyyy] = v.split('-');

    const mmm =
      mmmRaw.charAt(0).toUpperCase() +
      mmmRaw.slice(1).toLowerCase(); // 👈 Jan, Feb, May

    const normalized =
      dd.padStart(2, '0') + '-' +
      mmm + '-' +
      yyyy;

    this.personForm.get(controlName)?.setValue(normalized, { emitEvent: true });
    return;
  }

  // as user types
  this.personForm.get(controlName)?.setValue(v, { emitEvent: true });
}

private parseDdMmmYyyy(value: string): Date | null {
  const m = value.match(/^(\d{2})-([A-Za-z]{3})-(\d{4})$/);
  if (!m) return null;

  const day = parseInt(m[1], 10);
  const mon = m[2].toLowerCase();
  const year = parseInt(m[3], 10);

  const monthMap: Record<string, number> = {
    jan: 0, feb: 1, mar: 2, apr: 3, may: 4, jun: 5,
    jul: 6, aug: 7, sep: 8, oct: 9, nov: 10, dec: 11
  };

  if (monthMap[mon] === undefined) return null;

  const d = new Date(year, monthMap[mon], day);

  // reject invalid dates like 31-Feb-2020
  if (d.getFullYear() !== year || d.getMonth() !== monthMap[mon] || d.getDate() !== day) return null;

  return d;
}



private isValidDateFormat(value: string): boolean {
  // dd-Mmm-yyyy; accept Jan/JAN/jan while typing final
  return /^(\d{2})-([A-Za-z]{3})-(\d{4})$/.test(value);
}


// private ddMmmYyyyToIso(v: string): string {
//   const m = v.match(/^(\d{2})-(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)-(\d{4})$/);
//   if (!m) return '';

//   const day = parseInt(m[1], 10);
//   const mon = m[2];
//   const year = parseInt(m[3], 10);

//   const monthMap: Record<string, number> = {
//     JAN: 0, FEB: 1, MAR: 2, APR: 3, MAY: 4, JUN: 5,
//     JUL: 6, AUG: 7, SEP: 8, OCT: 9, NOV: 10, DEC: 11
//   };

//   const date = new Date(year, monthMap[mon], day);

//   // reject invalid dates like 31-FEB-2020
//   if (date.getFullYear() !== year || date.getMonth() !== monthMap[mon] || date.getDate() !== day) {
//     return '';
//   }

//   return date.toISOString().slice(0, 10); // yyyy-MM-dd
// }
private setupFormWatchers(): void {
  this.personForm.get('dob')?.valueChanges.subscribe((dobStr: string) => {
    const dob = this.parseDdMmmYyyy(dobStr);
    this.personForm.patchValue({ age: dob ? this.calculateAgeFromDate(dob) : '' }, { emitEvent: false });
  });

  this.personForm.get('dateOfMarriage')?.valueChanges.subscribe((mStr: string) => {
    const marriage = this.parseDdMmmYyyy(mStr);
    this.personForm.patchValue(
      { yearsOfMarriage: marriage ? this.calculateAgeFromDate(marriage) : '' },
      { emitEvent: false }
    );
  });
}

private calculateAgeFromDate(date: Date): number {
  const today = new Date();
  let years = today.getFullYear() - date.getFullYear();
  const monthDiff = today.getMonth() - date.getMonth();
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < date.getDate())) years--;
  return years;
}


  onSubmit(): void {
    // Handle form submission - Update the data with form values
    console.log(this.personForm.value);
    // You can then update the corresponding item in the persons array with the updated form values.
    let response = this.http.put("http://localhost:8080/member-id/"+this.personId, this.personForm.value);
    response.subscribe((data)=> this.persons = data);
    console.log(response);
    alert("successfully updated ");
    this.router.navigate(['/members'])
  }
}