import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FamilyTreeComponent } from './family-tree.component';

const MOCK_API_RESPONSE = [
  {
    familyId: 159,
    members: [
      {
        membershipId: 302, familyId: 159, firstName: 'Sanjay', middleName: 'Cherchill',
        lastName: 'Charles', gender: 'Male', maritalStatus: 'Married',
        fatherId: null, fatherName: 'Lt. Mr B.D Charles',
        spouseId: '303', spouseName: 'Sunita Charles',
        motherId: null, contact: '8955445313', status: 'Active',
        pledgeAmount: null, dateOfMarriage: '09-Feb-1990'
      },
      {
        membershipId: 303, familyId: 159, firstName: 'Sunita', lastName: 'Charles',
        gender: 'Female', maritalStatus: 'Married',
        fatherId: null, spouseId: '302', spouseName: 'Sanjay C Charles',
        motherId: null, contact: '9461763950', status: 'Active',
        pledgeAmount: null, dateOfMarriage: '09-Feb-1990'
      }
    ],
    pledgeAmount: 0, pledgeCredit: 0, pledgeDue: 0,
    headMemberId: null, pledgeStartDate: null,
    lastPledgeDepositDate: null, lastPledgeDepositAmount: 0
  },
  {
    familyId: 160,
    members: [
      {
        membershipId: 305, familyId: 160, firstName: 'Sagar', lastName: 'Charles',
        gender: 'Male', maritalStatus: 'Married',
        fatherId: null, fatherName: 'Sanjay C Charles',
        spouseId: null, motherId: null, contact: '9983333287', status: 'Active',
        pledgeAmount: null, dateOfMarriage: '27-Dec-2018'
      },
      {
        membershipId: 306, familyId: 160, firstName: 'Angela', lastName: 'Charles',
        gender: 'Female', maritalStatus: 'Married',
        fatherId: null, spouseId: '305', motherId: null,
        contact: '9983333287', status: 'Active', pledgeAmount: null
      },
      {
        membershipId: 352, familyId: 160, firstName: 'Ethan', lastName: 'Charles',
        gender: 'Male', maritalStatus: 'Unmarried',
        fatherId: '305', motherId: '306',
        spouseId: null, contact: '7742689250', status: 'Active', pledgeAmount: null
      }
    ],
    pledgeAmount: 0, pledgeCredit: 0, pledgeDue: 0,
    headMemberId: null, pledgeStartDate: null,
    lastPledgeDepositDate: null, lastPledgeDepositAmount: 0
  },
  {
    familyId: 161,
    members: [
      {
        membershipId: 304, familyId: 161, firstName: 'Ashwini', lastName: 'Charles',
        gender: 'Male', maritalStatus: 'Married',
        fatherId: '302', fatherName: 'Sanjay C Charles',
        spouseId: '402', motherId: '303', contact: '8955445313', status: 'Active',
        pledgeAmount: null
      },
      {
        membershipId: 402, familyId: 161, firstName: 'Richa', lastName: 'Rose',
        gender: 'Female', maritalStatus: 'Married',
        fatherId: null, fatherName: 'Ritesh Garg',
        spouseId: '304', motherId: null, contact: '7742689250', status: 'Active',
        pledgeAmount: null
      }
    ],
    pledgeAmount: 0, pledgeCredit: 0, pledgeDue: 0,
    headMemberId: null, pledgeStartDate: null,
    lastPledgeDepositDate: null, lastPledgeDepositAmount: 0
  }
];

describe('FamilyTreeComponent', () => {
  let component: FamilyTreeComponent;
  let fixture: ComponentFixture<FamilyTreeComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FamilyTreeComponent, HttpClientTestingModule]
    }).compileComponents();

    fixture = TestBed.createComponent(FamilyTreeComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should start in loading state', () => {
    expect(component.loading).toBeTrue();
    expect(component.familyTree.length).toBe(0);
    httpMock.expectOne('http://localhost:8080/members-tree').flush([]);
  });

  it('should build tree with Family 159 as root', () => {
    fixture.detectChanges();
    httpMock.expectOne('http://localhost:8080/members-tree').flush(MOCK_API_RESPONSE);

    expect(component.loading).toBeFalse();
    expect(component.error).toBeNull();
    expect(component.familyTree.length).toBe(1);

    const root = component.familyTree[0];
    expect(root.familyId).toBe(159);
    expect(root.level).toBe(0);
  });

  it('should set Sanjay as head of family 159', () => {
    fixture.detectChanges();
    httpMock.expectOne('http://localhost:8080/members-tree').flush(MOCK_API_RESPONSE);

    const root = component.familyTree[0];
    expect(root.head.membershipId).toBe(302);
    expect(root.head.firstName).toBe('Sanjay');
  });

  it('should set Sunita as spouse of family 159', () => {
    fixture.detectChanges();
    httpMock.expectOne('http://localhost:8080/members-tree').flush(MOCK_API_RESPONSE);

    const root = component.familyTree[0];
    expect(root.spouse?.membershipId).toBe(303);
    expect(root.spouse?.firstName).toBe('Sunita');
  });

  it('should link Family 160 and 161 as children of Family 159', () => {
    fixture.detectChanges();
    httpMock.expectOne('http://localhost:8080/members-tree').flush(MOCK_API_RESPONSE);

    const root = component.familyTree[0];
    expect(root.children.length).toBe(2);

    const childIds = root.children.map(c => c.familyId);
    expect(childIds).toContain(160);
    expect(childIds).toContain(161);
  });

  it('should assign level 1 to children of root', () => {
    fixture.detectChanges();
    httpMock.expectOne('http://localhost:8080/members-tree').flush(MOCK_API_RESPONSE);

    component.familyTree[0].children.forEach(child => {
      expect(child.level).toBe(1);
    });
  });

  it('should place Ethan (352) as unmarried child inside Family 160', () => {
    fixture.detectChanges();
    httpMock.expectOne('http://localhost:8080/members-tree').flush(MOCK_API_RESPONSE);

    const family160 = component.familyTree[0].children.find(c => c.familyId === 160);
    expect(family160).toBeDefined();
    expect(family160!.unmarriedChildren.length).toBe(1);
    expect(family160!.unmarriedChildren[0].membershipId).toBe(352);
  });

  it('should handle API error gracefully', () => {
    fixture.detectChanges();
    httpMock.expectOne('http://localhost:8080/members-tree')
      .error(new ErrorEvent('Network error'));

    expect(component.loading).toBeFalse();
    expect(component.error).toBe('Failed to load family records.');
    expect(component.familyTree.length).toBe(0);
  });

  it('initial() should return first letter of first name', () => {
    const member = MOCK_API_RESPONSE[0].members[0] as any;
    expect(component.initial(member)).toBe('S');
  });

  it('displayName() should return first + last name', () => {
    const member = MOCK_API_RESPONSE[0].members[0] as any;
    expect(component.displayName(member)).toBe('Sanjay Charles');
  });

  it('headRole() should return Father for male head', () => {
    fixture.detectChanges();
    httpMock.expectOne('http://localhost:8080/members-tree').flush(MOCK_API_RESPONSE);

    const root = component.familyTree[0];
    expect(component.headRole(root)).toBe('Father');
  });

  it('generationLabel() should return correct labels', () => {
    expect(component.generationLabel(0)).toBe('1st Generation');
    expect(component.generationLabel(1)).toBe('2nd Generation');
    expect(component.generationLabel(2)).toBe('3rd Generation');
  });
});