// add-person.component.ts
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-person',
  standalone: true, 
  imports: [ReactiveFormsModule, FormsModule],
  templateUrl: './add-person.component.html'
})
export class AddPersonComponent implements OnInit {

  addPersonForm!: FormGroup;
  dobDisplay = '';
  dateOfMarriageDisplay = '';
  baptisedDateDisplay = '';
  confirmationDateDisplay = '';
  persons: any;

  constructor(private fb: FormBuilder, private http: HttpClient,  private router: Router) { }

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
      age:                [{ value: '', disabled: true }],
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
  this.setupFormWatchers();
}

  /**
   * Watch form changes for auto-calculations
   */
  // private setupFormWatchers(): void {
  //   // Auto age from DOB
  //   this.addPersonForm.get('dob')?.valueChanges.subscribe(dob => {
  //     if (dob) this.addPersonForm.patchValue({ age: this.calculateAge(dob) });
  //   });

  //   // Auto years married from marriage date
  //   this.addPersonForm.get('dateOfMarriage')?.valueChanges.subscribe(marriageDate => {
  //     if (marriageDate) {
  //       const years = this.calculateYearsMarried(marriageDate);
  //       this.addPersonForm.patchValue({ yearsOfMarriage: years });
  //     }
  //   });
  // }

  // // ===== DOB HANDLER =====
  // onDobInput(event: any): void {
  //   let value = event.target.value.trim().toUpperCase();
  //   value = this.autoFormatDate(value);
  //   this.dobDisplay = value;

  //   if (value.length === 11 && this.isValidDateFormat(value)) {
  //     const isoDate = this.ddMmmYyyyToIso(value);
  //     this.addPersonForm.patchValue({ dob: isoDate });
  //   } else {
  //     this.addPersonForm.patchValue({ dob: '' });
  //   }
  // }

  // // ===== MARRIAGE DATE HANDLER =====
  // onMarriageDateInput(event: any): void {
  //   let value = event.target.value.trim().toUpperCase();
  //   value = this.autoFormatDate(value);
  //   this.dateOfMarriageDisplay = value;

  //   if (value.length === 11 && this.isValidDateFormat(value)) {
  //     const isoDate = this.ddMmmYyyyToIso(value);
  //     this.addPersonForm.patchValue({ dateOfMarriage: isoDate });
  //   } else {
  //     this.addPersonForm.patchValue({ dateOfMarriage: '' });
  //   }
  // }

  // // ===== BAPTISED DATE HANDLER =====
  // onBaptisedDateInput(event: any): void {
  //   let value = event.target.value.trim().toUpperCase();
  //   value = this.autoFormatDate(value);
  //   this.baptisedDateDisplay = value;

  //   if (value.length === 11 && this.isValidDateFormat(value)) {
  //     const isoDate = this.ddMmmYyyyToIso(value);
  //     this.addPersonForm.patchValue({ baptisedDate: isoDate });
  //   } else {
  //     this.addPersonForm.patchValue({ baptisedDate: '' });
  //   }
  // }

  // // ===== CONFIRMATION DATE HANDLER =====
  // onConfirmationDateInput(event: any): void {
  //   let value = event.target.value.trim().toUpperCase();
  //   value = this.autoFormatDate(value);
  //   this.confirmationDateDisplay = value;

  //   if (value.length === 11 && this.isValidDateFormat(value)) {
  //     const isoDate = this.ddMmmYyyyToIso(value);
  //     this.addPersonForm.patchValue({ ConfirmationDate: isoDate });
  //   } else {
  //     this.addPersonForm.patchValue({ ConfirmationDate: '' });
  //   }
  // }

  // ===== SHARED DATE UTILITIES =====
  private autoFormatDate(value: string): string {
    if (value.length === 2) return value + '-';
    if (value.length === 5) return value + '-';
    return value;
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

    this.addPersonForm.get(controlName)?.setValue(normalized, { emitEvent: true });
    return;
  }

  // as user types
  this.addPersonForm.get(controlName)?.setValue(v, { emitEvent: true });
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
  this.addPersonForm.get('dob')?.valueChanges.subscribe((dobStr: string) => {
    const dob = this.parseDdMmmYyyy(dobStr);
    this.addPersonForm.patchValue({ age: dob ? this.calculateAgeFromDate(dob) : '' }, { emitEvent: false });
  });

  this.addPersonForm.get('dateOfMarriage')?.valueChanges.subscribe((mStr: string) => {
    const marriage = this.parseDdMmmYyyy(mStr);
    this.addPersonForm.patchValue(
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

  // private calculateYearsMarried(isoDate: string): number {
  //   const today = new Date();
  //   const marriage = new Date(isoDate);
  //   let years = today.getFullYear() - marriage.getFullYear();
  //   const monthDiff = today.getMonth() - marriage.getMonth();
  //   if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < marriage.getDate())) years--;
  //   return years;
  // }
  onSubmit(): void {
    const payload = this.addPersonForm.value;
    let response = this.http.post("http://localhost:8080/member",this.addPersonForm.value);
    response.subscribe({
  next: (member) => {
    this.router.navigate(['/members']); // Success 2xx
    alert("Member added successfully");
    this.router.navigate(['/members'])
  },
  error: (error) => {
    console.log('Error status:', error.status);
    console.log('Error message:', error.error?.message || error.message);
    
    if (error.status === 400) {
      alert('Bad request - check your data');
    } else if (error.status === 500) {
      alert('Server error');
    } else {
      alert('Failed to save: ' + error.message);
    }
  }
});
    console.log(response);
  }
}
