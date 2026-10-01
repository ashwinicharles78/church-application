import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { MembersListComponent } from './members-list.component';

describe('MembersListComponent', () => {
  let component: MembersListComponent;
  let fixture: ComponentFixture<MembersListComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MembersListComponent, HttpClientTestingModule, RouterTestingModule]
    })
    .compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(MembersListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    httpMock.expectOne('http://localhost:8080/all-members').flush([{
      membershipId: 12,
      familyId: 'F-2',
      firstName: 'Mina',
      lastName: 'Lee',
      title: '',
      status: 'Active',
      dob: new Date('1990-01-01'),
      age: '36',
      fatherName: '',
      spouseName: '',
      spouseId: null,
      cmcmembershipId: 'CMC-12'
    }]);
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('renders labeled member fields and the existing actions', () => {
    const element = fixture.nativeElement as HTMLElement;

    expect(element.querySelector('[aria-label="Family ID for Mina Lee"]')).toBeTruthy();
    expect(element.querySelector('[aria-label="CMC membership ID for Mina Lee"]')).toBeTruthy();
    expect(element.textContent).toContain('Preview');
    expect(element.textContent).toContain('Tree');
    expect(element.textContent).toContain('Edit');
    expect(element.textContent).toContain('Delete');
  });

  it('keeps the CMC bulk update request contract', () => {
    component.onCmcMembershipIdInput(12, { target: { value: 'CMC-NEW' } } as unknown as Event);
    component.saveCmcMembershipIds();

    const request = httpMock.expectOne('http://localhost:8080/bulk-update-cmc-ids');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual([{ membershipId: 12, cmcMembershipId: 'CMC-NEW' }]);
    request.flush('ok');
  });
});
