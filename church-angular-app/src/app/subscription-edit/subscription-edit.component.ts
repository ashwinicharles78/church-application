import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SubscriptionService } from '../subscription.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-subscription-edit',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule],
  templateUrl: './subscription-edit.component.html',
  styleUrl: './subscription-edit.component.css'
})
export class SubscriptionEditComponent implements OnInit {
  subscriptionForm: FormGroup;
  subscriptionId: string | null = null;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {
    // Initializing form with attributes from your code base
    this.subscriptionForm = this.fb.group({
      familyId: [''],
      pledgeAmount: [0, Validators.required],
      pledgeCredit: [0],
      pledgeDue: [0],
      lastPledgeDue: [0],
      pledgeStartDate: [''],
      lastPledgeDepositDate: [''],
      lastPledgeDepositAmount: [0]
    });
  }

  ngOnInit(): void {
    this.subscriptionId = this.route.snapshot.paramMap.get('id');
    if (this.subscriptionId) {
      this.loadSubscriptionData();
    }
  }

  loadSubscriptionData() {
    this.http.get(`http://localhost:8080/subscription/${this.subscriptionId}`)
      .subscribe((data: any) => {
        this.subscriptionForm.patchValue(data);
      });
  }

  onUpdate() {
    if (this.subscriptionForm.valid) {
      this.http.put(`http://localhost:8080/subscription/${this.subscriptionId}`, this.subscriptionForm.value)
        .subscribe(() => alert('Subscription Updated Successfully'));
    }
  }

  goToInvoice() {
  const id = this.subscriptionId;
  // This URL now points to the @Controller we just created
  window.open(`http://localhost:8080/invoice/${id}`, '_blank');
}
  
  deductPledge() {
    if (this.subscriptionForm.valid) {
      console.log("print")
      this.http.get(`http://localhost:8080/subscription/pledge/${this.subscriptionId}`)
        .subscribe(() => alert('Pledge successfull'));
    }
  }
}