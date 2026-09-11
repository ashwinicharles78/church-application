import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-edit-member',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './edit-member.component.html',
  styleUrl: './edit-member.component.css'
})
export class EditMemberComponent implements OnInit {
  personForm!: FormGroup;
  persons: any;
  personId: any;

  constructor(
    private formBuilder: FormBuilder, 
    private http: HttpClient, 
    private route: ActivatedRoute,  
    private router: Router
  ) { }

  ngOnInit(): void {
    // 1. Get the ID from the route first
    this.route.params.subscribe(params => {
      this.personId = params['id'];
    });

    // 2. Initialize the form IMMEDIATELY with empty/default values.
    // This prevents the "Cannot read properties of undefined (reading 'title')" 
    // and "NG01052: formGroup expects a FormGroup instance" errors.
    this.initForm();

    // 3. Setup watchers for age and marriage calculations
    this.setupFormWatchers();

    // 4. Fetch the member data and populate the form
    this.fetchMemberData();
  }

  private initForm(): void {
    this.personForm = this.formBuilder.group({
      title:              [""],
      lastName:           ["", Validators.required],
      middleName:         [""],
      firstName:          ["", Validators.required],
      familyId:           [""],
      fatherName:         ["", Validators.required],
      fatherId:           [""],
      motherName:         ["", Validators.required],
      motherId:           [""],
      spouseName:         [""],
      spouseId:           [""],
      gender:             ["", Validators.required],
      dob:                ["", Validators.required],
      age:                ["", Validators.required],
      maritalStatus:      ["", Validators.required],
      dateOfMarriage:     [""],
      yearsOfMarriage:    [""],
      address:            ["", Validators.required],
      contact:            ["", Validators.required],
      Email:              [""],
      cmcmembershipId:     [""],
      dateOfDemise:       [""],
      pledgeNumber:       ["", Validators.required],
      Baptised:           ["", Validators.required],
      baptisedDate:       [""],
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

  private fetchMemberData(): void {
    this.http.get("http://localhost:8080/member-id/" + this.personId)
      .subscribe((data: any) => {
        this.persons = data;
        // patchValue safely fills the form with the API response
        this.personForm.patchValue({
          ...data,
          Email: data.email // Matches the 'Email' control name to 'email' property from API
        });
      });
  }

  onDateInput(event: Event, controlName: string): void {
    const input = event.target as HTMLInputElement;
    let v = (input.value || '').trim();
    v = v.replace(/[^0-9A-Za-z-]/g, '');

    if (v.length === 2 && !v.endsWith('-')) v += '-';
    if (v.length === 6 && v[2] === '-' && v[5] !== '-') v += '-';

    if (v.length === 11 && this.isValidDateFormat(v)) {
      const [dd, mmmRaw, yyyy] = v.split('-');
      const mmm = mmmRaw.charAt(0).toUpperCase() + mmmRaw.slice(1).toLowerCase();
      const normalized = dd.padStart(2, '0') + '-' + mmm + '-' + yyyy;
      this.personForm.get(controlName)?.setValue(normalized, { emitEvent: true });
      return;
    }
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
    if (d.getFullYear() !== year || d.getMonth() !== monthMap[mon] || d.getDate() !== day) return null;
    return d;
  }

  private isValidDateFormat(value: string): boolean {
    return /^(\d{2})-([A-Za-z]{3})-(\d{4})$/.test(value);
  }

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
    if (this.personForm.valid) {
      this.http.put("http://localhost:8080/member-id/" + this.personId, this.personForm.value)
        .subscribe((data) => {
          this.persons = data;
          alert("successfully updated ");
          this.router.navigate(['/members']);
        });
    } else {
      alert("Please fill all required fields correctly.");
    }
  }

  onCancel(): void {
    this.router.navigate(['/members']);
  }
}