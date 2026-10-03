import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PaymentVoucher, PaymentVoucherPage, PaymentVoucherRequest } from './payment-voucher.model';

@Injectable({ providedIn: 'root' })
export class PaymentVoucherService {
  private readonly apiUrl = 'http://localhost:8080/payment-vouchers';

  constructor(private readonly http: HttpClient) {}

  getLatest(): Observable<PaymentVoucher[]> {
    return this.http.get<PaymentVoucher[]>(`${this.apiUrl}/latest`);
  }

  getPage(page: number, size = 20): Observable<PaymentVoucherPage> {
    return this.http.get<PaymentVoucherPage>(this.apiUrl, { params: new HttpParams().set('page', page).set('size', size) });
  }

  findBySerial(serialNumber: string): Observable<PaymentVoucher> {
    return this.http.get<PaymentVoucher>(`${this.apiUrl}/search`, { params: { serialNumber } });
  }

  create(request: PaymentVoucherRequest): Observable<PaymentVoucher> {
    return this.http.post<PaymentVoucher>(this.apiUrl, request);
  }

  printUrl(serialNumber: string): string {
    return `${this.apiUrl}/${encodeURIComponent(serialNumber)}/print`;
  }
}