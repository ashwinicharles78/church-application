import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SubscriptionService } from '../subscription.service';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-subscription-edit',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, CommonModule],
  templateUrl: './subscription-edit.component.html',
  styleUrl: './subscription-edit.component.css'
})
export class SubscriptionEditComponent implements OnInit {
  subscriptionForm: FormGroup;
  subscriptionId: string | null = null;
  // Inside your component class
  paymentTransactionEntries: any[] = [];

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
        console.log(data);
        this.paymentTransactionEntries = data.paymentTransactionEntries;
      });
  }

  onUpdate() {
    if (this.subscriptionForm.valid) {
      this.http.put(`http://localhost:8080/subscription/${this.subscriptionId}`, this.subscriptionForm.value)
        .subscribe(() => alert('Subscription Updated Successfully'));
        window.location.reload();
    }
  }

  goToInvoice() {
  const id = this.subscriptionId;
  // This URL now points to the @Controller we just created
  window.open(`http://localhost:8080/invoice/${id}`, '_blank');
}


  // Helper method to open the Thymeleaf invoice in a new browser tab
  openInvoice() {
    // Uses the GET /invoice/{id} endpoint from InvoiceController
    const invoiceUrl = `http://localhost:8080/invoice/${this.subscriptionId}`;
    window.open(invoiceUrl, '_blank');
  }

  // Update your existing deductPledge method
  deductPledge() {
    // Assuming you have a method in your service that calls the GET /subscription/pledge/{id} endpoint
    this.http.post(`http://localhost:8080/subscription/pledge/${this.subscriptionId}`, this.subscriptionForm.value).subscribe({
      next: (updatedSubscription : any) => {
        console.log('Pledge submitted successfully');
        
        // 1. Refresh your local form/data so the UI updates
        this.subscriptionForm.patchValue(updatedSubscription);
        this.paymentTransactionEntries = updatedSubscription.paymentTransactionEntries;
        
        // 2. Automatically pop open the invoice template in a new tab
        this.openInvoice();
      },
      error: (err) => console.error('Error submitting pledge', err)
    });
  }
}