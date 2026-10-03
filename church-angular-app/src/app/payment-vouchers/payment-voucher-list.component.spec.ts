import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { PaymentVoucherListComponent } from './payment-voucher-list.component';

describe('PaymentVoucherListComponent', () => {
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaymentVoucherListComponent, RouterTestingModule],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('loads a page of vouchers', () => {
    const fixture = TestBed.createComponent(PaymentVoucherListComponent);
    fixture.detectChanges();

    const request = httpTesting.expectOne('http://localhost:8080/payment-vouchers?page=0&size=20');
    request.flush({ content: [], page: 0, size: 20, totalElements: 0, totalPages: 0 });

    expect(fixture.componentInstance.loading).toBeFalse();
  });

  it('finds a voucher by its serial number', () => {
    const fixture = TestBed.createComponent(PaymentVoucherListComponent);
    fixture.detectChanges();
    httpTesting.expectOne('http://localhost:8080/payment-vouchers?page=0&size=20')
      .flush({ content: [], page: 0, size: 20, totalElements: 0, totalPages: 0 });

    fixture.componentInstance.serialNumber = 'PV-2026-000001';
    fixture.componentInstance.search();
    const request = httpTesting.expectOne('http://localhost:8080/payment-vouchers/search?serialNumber=PV-2026-000001');
    expect(request.request.method).toBe('GET');
    request.flush({ serialNumber: 'PV-2026-000001', payee: 'Supplier', netAmount: 500 });

    expect(fixture.componentInstance.vouchers[0].serialNumber).toBe('PV-2026-000001');
    expect(fixture.componentInstance.searched).toBeTrue();
  });
});