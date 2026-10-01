import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { DashboardComponent } from './dashboard.component';

describe('DashboardComponent', () => {
  let fixture: ComponentFixture<DashboardComponent>;
  let component: DashboardComponent;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardComponent, HttpClientTestingModule, RouterTestingModule]
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => httpMock.verify());

  it('loads member totals and upcoming occasions independently', () => {
    fixture.detectChanges();

    httpMock.expectOne('http://localhost:8080/all-members').flush([
      { membershipId: 1, firstName: 'Mina', lastName: 'Lee', status: 'Active' },
      { membershipId: 2, firstName: 'Jon', lastName: 'Reed', status: 'Inactive' }
    ]);
    httpMock.expectOne('http://localhost:8080/events').flush([
      { eventType: 'Birthday', date: '2026-10-01', memberName: 'Mina Lee', familyId: '8' }
    ]);
    fixture.detectChanges();

    expect(component.members.length).toBe(2);
    expect(component.activeMemberCount).toBe(1);
    expect(component.events.length).toBe(1);
    expect(fixture.nativeElement.textContent).toContain('Membership at a glance');
    expect(fixture.nativeElement.textContent).toContain('Mina Lee');
  });

  it('shows an error for a failed member request without hiding event data', () => {
    fixture.detectChanges();

    httpMock.expectOne('http://localhost:8080/all-members').flush('Unavailable', {
      status: 500,
      statusText: 'Server Error'
    });
    httpMock.expectOne('http://localhost:8080/events').flush([]);
    fixture.detectChanges();

    expect(component.membersError).toBeTrue();
    expect(component.eventsError).toBeFalse();
    expect(fixture.nativeElement.textContent).toContain('Unavailable');
    expect(fixture.nativeElement.textContent).toContain('No birthdays or anniversaries');
  });

  it('opens the browser print dialog for upcoming events', () => {
    const printSpy = spyOn(window, 'print');

    component.printUpcomingEvents();

    expect(printSpy).toHaveBeenCalled();
  });
});