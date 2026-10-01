import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

interface PledgeRate {
  monthlyAmount: number;
  effectiveDate: string;
}

interface PledgeTransaction {
  transactionId: string;
  name: string;
  date: string;
  amount: number;
}

interface PledgeAccount {
  familyId: string;
  exists: boolean;
  configured: boolean;
  monthlyAmount: number;
  pledgeStartDate: string | null;
  pledgeDue: number;
  pledgeCredit: number;
  lastDepositDate: string | null;
  lastDepositAmount: number;
  recordedTransactionId: string | null;
  rateHistory: PledgeRate[];
  transactions: PledgeTransaction[];
}

@Component({
  selector: 'app-subscription-edit',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './subscription-edit.component.html',
  styleUrl: './subscription-edit.component.css'
})
export class SubscriptionEditComponent implements OnInit {
  private readonly apiUrl = 'http://localhost:8080';
  familyId = '';
  account: PledgeAccount | null = null;
  loading = true;
  saving = false;
  errorMessage = '';
  successMessage = '';

  setupForm = this.fb.group({
    mode: ['FRESH', Validators.required],
    monthlyAmount: [0, [Validators.required, Validators.min(1)]],
    pledgeStartDate: [this.today(), Validators.required],
    openingDue: [0, [Validators.required, Validators.min(0)]],
    openingCredit: [0, [Validators.required, Validators.min(0)]],
    asOfDate: [this.today(), Validators.required]
  });

  termsForm = this.fb.group({
    monthlyAmount: [0, [Validators.required, Validators.min(1)]],
    effectiveDate: [this.today(), Validators.required]
  });

  paymentForm = this.fb.group({
    amount: [0, [Validators.required, Validators.min(1)]],
    paymentDate: [this.today(), Validators.required]
  });

  constructor(private fb: FormBuilder, private route: ActivatedRoute, private http: HttpClient) {}

  ngOnInit(): void {
    this.familyId = this.route.snapshot.paramMap.get('id') ?? '';
    this.loadAccount();
  }

  get setupMode(): string {
    return this.setupForm.controls.mode.value ?? 'FRESH';
  }

  selectSetupMode(mode: 'FRESH' | 'OFFLINE'): void {
    this.setupForm.controls.mode.setValue(mode);
    if (mode === 'FRESH') {
      this.setupForm.patchValue({ openingDue: 0, openingCredit: 0 });
    }
  }

  loadAccount(): void {
    this.loading = true;
    this.errorMessage = '';
    this.http.get<PledgeAccount>(`${this.apiUrl}/subscription/${this.familyId}`).subscribe({
      next: account => this.applyAccount(account),
      error: error => {
        this.loading = false;
        this.errorMessage = error.error?.message ?? 'Could not load the pledge account.';
      }
    });
  }

  submitSetup(): void {
    if (this.setupForm.invalid || this.saving) return;
    this.submitRequest(this.http.post<PledgeAccount>(
      `${this.apiUrl}/subscription/${this.familyId}/setup`, this.setupForm.getRawValue()
    ), 'Pledge account set up successfully.');
  }

  changeTerms(): void {
    if (this.termsForm.invalid || this.saving) return;
    this.submitRequest(this.http.put<PledgeAccount>(
      `${this.apiUrl}/subscription/${this.familyId}/terms`, this.termsForm.getRawValue()
    ), 'Pledge rate change scheduled.');
  }

  recordPayment(): void {
    if (this.paymentForm.invalid || this.saving) return;
    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.http.post<PledgeAccount>(
      `${this.apiUrl}/subscription/${this.familyId}/payments`, this.paymentForm.getRawValue()
    ).subscribe({
      next: account => {
        this.applyAccount(account);
        this.successMessage = 'Payment recorded.';
        this.paymentForm.patchValue({ amount: 0, paymentDate: this.today() });
        if (account.recordedTransactionId) this.openInvoice(account.recordedTransactionId);
      },
      error: error => {
        this.saving = false;
        this.errorMessage = error.error?.message ?? 'Could not record the payment.';
      }
    });
  }

  resetAccount(): void {
    const confirmation = window.prompt(
      `This permanently deletes all pledge payments and rate history for ${this.familyId}. Type RESET ${this.familyId} to continue.`
    );
    if (confirmation !== `RESET ${this.familyId}`) return;

    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.http.post<PledgeAccount>(`${this.apiUrl}/subscription/${this.familyId}/reset`, { confirmation })
      .subscribe({
        next: account => {
          this.applyAccount(account);
          this.setupForm.patchValue({
            mode: 'FRESH',
            monthlyAmount: 0,
            pledgeStartDate: this.today(),
            openingDue: 0,
            openingCredit: 0,
            asOfDate: this.today()
          });
          this.successMessage = 'Pledge account reset. You can now set it up again.';
        },
        error: error => {
          this.saving = false;
          this.errorMessage = error.error?.message ?? 'Could not reset the pledge account.';
        }
      });
  }

  openInvoice(transactionId?: string): void {
    const path = transactionId
      ? `/invoice/${this.familyId}/transaction/${encodeURIComponent(transactionId)}`
      : `/invoice/${this.familyId}`;
    window.open(`${this.apiUrl}${path}`, '_blank', 'noopener');
  }

  private submitRequest(request: import('rxjs').Observable<PledgeAccount>, message: string): void {
    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';
    request.subscribe({
      next: account => {
        this.applyAccount(account);
        this.successMessage = message;
      },
      error: error => {
        this.saving = false;
        this.errorMessage = error.error?.message ?? 'Could not save pledge account changes.';
      }
    });
  }

  private applyAccount(account: PledgeAccount): void {
    this.account = account;
    this.loading = false;
    this.saving = false;
    this.setupForm.patchValue({
      mode: account.exists ? 'OFFLINE' : 'FRESH',
      monthlyAmount: account.monthlyAmount || 0,
      pledgeStartDate: account.pledgeStartDate ?? this.today(),
      openingDue: account.pledgeDue || 0,
      openingCredit: account.pledgeCredit || 0,
      asOfDate: this.today()
    });
    this.termsForm.patchValue({ monthlyAmount: account.monthlyAmount || 0, effectiveDate: this.today() });
    this.paymentForm.patchValue({ amount: 0, paymentDate: this.today() });
  }

  private today(): string {
    const date = new Date();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${date.getFullYear()}-${month}-${day}`;
  }
}
