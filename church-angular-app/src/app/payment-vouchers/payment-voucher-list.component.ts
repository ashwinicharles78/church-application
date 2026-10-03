import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PaymentVoucher, PaymentVoucherPage } from './payment-voucher.model';
import { PaymentVoucherService } from './payment-voucher.service';

@Component({
  selector: 'app-payment-voucher-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './payment-voucher-list.component.html',
  styleUrl: './payment-voucher-list.component.css'
})
export class PaymentVoucherListComponent implements OnInit {
  vouchers: PaymentVoucher[] = [];
  page = 0;
  totalPages = 0;
  totalElements = 0;
  readonly pageSize = 20;
  serialNumber = '';
  loading = true;
  errorMessage = '';
  searched = false;

  constructor(readonly voucherService: PaymentVoucherService) {}

  ngOnInit(): void { this.loadPage(); }

  loadPage(page = 0): void {
    this.loading = true;
    this.errorMessage = '';
    this.searched = false;
    this.voucherService.getPage(page, this.pageSize).subscribe({
      next: result => this.applyPage(result),
      error: () => { this.errorMessage = 'Payment vouchers could not be loaded.'; this.loading = false; }
    });
  }

  search(): void {
    const serial = this.serialNumber.trim();
    if (!serial) { this.loadPage(); return; }
    this.loading = true;
    this.errorMessage = '';
    this.searched = true;
    this.voucherService.findBySerial(serial).subscribe({
      next: voucher => { this.vouchers = [voucher]; this.page = 0; this.totalPages = 1; this.totalElements = 1; this.loading = false; },
      error: (error: HttpErrorResponse) => {
        this.vouchers = [];
        this.totalPages = 0;
        this.totalElements = 0;
        this.errorMessage = error.status === 404 ? '' : 'Voucher search failed. Try again.';
        this.loading = false;
      }
    });
  }

  previous(): void { if (this.page > 0) this.loadPage(this.page - 1); }
  next(): void { if (this.page + 1 < this.totalPages) this.loadPage(this.page + 1); }

  clearSearch(): void {
    this.serialNumber = '';
    this.loadPage();
  }

  private applyPage(result: PaymentVoucherPage): void {
    this.vouchers = result.content;
    this.page = result.page;
    this.totalPages = result.totalPages;
    this.totalElements = result.totalElements;
    this.loading = false;
  }
}