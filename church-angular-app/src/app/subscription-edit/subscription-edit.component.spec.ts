import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { SubscriptionEditComponent } from './subscription-edit.component';

describe('SubscriptionEditComponent', () => {
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubscriptionEditComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => 'family-1' } } }
        }
      ]
    }).compileComponents();

    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('loads the selected family pledge account', () => {
    const fixture = TestBed.createComponent(SubscriptionEditComponent);
    fixture.detectChanges();

    const request = httpTesting.expectOne('http://localhost:8080/subscription/family-1');
    expect(request.request.method).toBe('GET');
    request.flush({
      familyId: 'family-1',
      exists: false,
      configured: false,
      monthlyAmount: 0,
      pledgeStartDate: null,
      pledgeDue: 0,
      pledgeCredit: 0,
      lastDepositDate: null,
      lastDepositAmount: 0,
      recordedTransactionId: null,
      rateHistory: [],
      transactions: []
    });

    expect(fixture.componentInstance.account?.familyId).toBe('family-1');
  });

  it('requires the family-specific phrase and returns to fresh setup after reset', () => {
    const fixture = TestBed.createComponent(SubscriptionEditComponent);
    fixture.detectChanges();
    httpTesting.expectOne('http://localhost:8080/subscription/family-1').flush({
      familyId: 'family-1',
      exists: true,
      configured: true,
      monthlyAmount: 200,
      pledgeStartDate: '2026-01-01',
      pledgeDue: 100,
      pledgeCredit: 0,
      lastDepositDate: null,
      lastDepositAmount: 0,
      recordedTransactionId: null,
      rateHistory: [],
      transactions: []
    });

    spyOn(window, 'prompt').and.returnValue('RESET family-1');
    fixture.componentInstance.resetAccount();

    const resetRequest = httpTesting.expectOne('http://localhost:8080/subscription/family-1/reset');
    expect(resetRequest.request.method).toBe('POST');
    expect(resetRequest.request.body).toEqual({ confirmation: 'RESET family-1' });
    resetRequest.flush({
      familyId: 'family-1',
      exists: true,
      configured: false,
      monthlyAmount: 0,
      pledgeStartDate: null,
      pledgeDue: 0,
      pledgeCredit: 0,
      lastDepositDate: null,
      lastDepositAmount: 0,
      recordedTransactionId: null,
      rateHistory: [],
      transactions: []
    });

    expect(fixture.componentInstance.setupForm.controls.mode.value).toBe('FRESH');
    expect(fixture.componentInstance.successMessage).toContain('reset');
  });
});
