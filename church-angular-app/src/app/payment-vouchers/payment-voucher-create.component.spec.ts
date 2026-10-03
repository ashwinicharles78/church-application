import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { PaymentVoucherCreateComponent } from './payment-voucher-create.component';

describe('PaymentVoucherCreateComponent', () => {
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaymentVoucherCreateComponent, RouterTestingModule],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('creates a voucher and calculates the net amount in the request', () => {
    const fixture = TestBed.createComponent(PaymentVoucherCreateComponent);
    const component = fixture.componentInstance;
    component.form.patchValue({
      approvedAmount: 1200,
      amountInWords: 'One thousand two hundred rupees',
      purpose: 'Office supplies',
      payee: 'Stationery store',
      totalBillsAmount: 1200,
      advanceAmount: 200
    });
    component.submit();

    const request = httpTesting.expectOne('http://localhost:8080/payment-vouchers');
    expect(request.request.method).toBe('POST');
    expect(request.request.body.netAmount).toBeUndefined();
    expect(request.request.body.advanceAmount).toBe(200);
    request.flush({
      serialNumber: 'PV-2026-000001', netAmount: 1000, voucherDate: '2026-10-01', approvedAmount: 1200,
      amountInWords: 'One thousand two hundred rupees', purpose: 'Office supplies', payee: 'Stationery store',
      billNumber: null, billDate: null, paymentMethod: 'CASH', paymentReferenceNumber: null,
      paymentReferenceDate: null, totalBillsAmount: 1200, advanceAmount: 200, paymentDate: '2026-10-01',
      receiptMethod: null, receiptReferenceNumber: null, receiptDate: null, receiptAmount: null,
      paidBy: null, createdAt: '2026-10-01T10:00:00'
    });

    expect(component.createdVoucher?.serialNumber).toBe('PV-2026-000001');
    expect(component.netAmount).toBe(1000);
  });
});