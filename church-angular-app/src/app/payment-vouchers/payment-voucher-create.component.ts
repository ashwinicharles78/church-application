import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PaymentVoucher, PaymentVoucherRequest } from './payment-voucher.model';
import { PaymentVoucherService } from './payment-voucher.service';

@Component({
  selector: 'app-payment-voucher-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './payment-voucher-create.component.html',
  styleUrl: './payment-voucher-create.component.css'
})
export class PaymentVoucherCreateComponent {
  saving = false;
  errorMessage = '';
  createdVoucher: PaymentVoucher | null = null;

  form = this.fb.nonNullable.group({
    voucherDate: [this.today(), Validators.required],
    approvedAmount: [0, [Validators.required, Validators.min(0)]],
    amountInWords: ['', [Validators.required, Validators.maxLength(500)]],
    purpose: ['', [Validators.required, Validators.maxLength(500)]],
    payee: ['', [Validators.required, Validators.maxLength(255)]],
    billNumber: [''],
    billDate: [''],
    paymentMethod: ['CASH' as 'CASH' | 'CHEQUE', Validators.required],
    paymentReferenceNumber: [''],
    paymentReferenceDate: [''],
    totalBillsAmount: [0, [Validators.required, Validators.min(0)]],
    advanceAmount: [0, [Validators.required, Validators.min(0)]],
    paymentDate: [this.today(), Validators.required],
    receiptMethod: ['' as '' | 'CASH' | 'CHEQUE'],
    receiptReferenceNumber: [''],
    receiptDate: [''],
    receiptAmount: [null as number | null, Validators.min(0)],
    paidBy: ['']
  });

  constructor(private readonly fb: FormBuilder, private readonly voucherService: PaymentVoucherService) {}

  get netAmount(): number {
    return Math.max(0, Number(this.form.controls.totalBillsAmount.value) - Number(this.form.controls.advanceAmount.value));
  }

  submit(): void {
    if (this.form.invalid || this.saving || this.form.controls.advanceAmount.value > this.form.controls.totalBillsAmount.value) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    const request: PaymentVoucherRequest = {
      ...value,
      amountInWords: value.amountInWords.trim(),
      purpose: value.purpose.trim(),
      payee: value.payee.trim(),
      billNumber: this.optional(value.billNumber),
      billDate: this.optional(value.billDate),
      paymentReferenceNumber: this.optional(value.paymentReferenceNumber),
      paymentReferenceDate: this.optional(value.paymentReferenceDate),
      receiptMethod: value.receiptMethod || null,
      receiptReferenceNumber: this.optional(value.receiptReferenceNumber),
      receiptDate: this.optional(value.receiptDate),
      receiptAmount: value.receiptAmount === null ? null : Number(value.receiptAmount),
      paidBy: this.optional(value.paidBy)
    };

    this.saving = true;
    this.errorMessage = '';
    this.voucherService.create(request).subscribe({
      next: voucher => {
        this.createdVoucher = voucher;
        this.saving = false;
      },
      error: error => {
        this.errorMessage = error.error?.message ?? 'Could not save the payment voucher.';
        this.saving = false;
      }
    });
  }

  printVoucher(): void {
    if (this.createdVoucher) window.open(this.voucherService.printUrl(this.createdVoucher.serialNumber), '_blank', 'noopener');
  }

  private optional(value: string): string | null { return value.trim() || null; }

  private today(): string {
    const date = new Date();
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
  }
}