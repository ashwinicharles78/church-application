export interface Member {
    membershipId: number;
    familyId: number | null;
    title: string;
    lastName: string;
    middleName: string;
    firstName: string;
    fatherName: string;
    fatherId: string | null;
    motherName: string;
    motherId: string | null;
    spouseName: string;
    spouseId: string | null;
    gender: Gender;
    dob: Date;
    age: string;
    address: string;
    maritalStatus: MaritalStatus;
    dateOfMarriage: Date | null;
    yearsOfMarriage: string;
    contact: string;
    email: string | null;
    baptised: string | null;
    baptisedDate: Date | null;
    confirmed: string | null;
    confirmationDate: Date | null;
    fullMember: string;
    nonResidentMember: string | null;
    preparatoryMember: string;
    selfDependent: string;
    pledgeNumber: string | null;
    pledgeAmount: string | null;
    status: string;
    inactiveReason: string;
    inactiveSince: string;
  }
  
  export enum Gender {
    Male = 'Male',
    Female = 'Female',
    Other = 'Other'
  }
  
  export enum MaritalStatus {
    Married = 'Married',
    Unmarried = 'Unmarried',
    Divorced = 'Divorced',
    Widowed = 'Widowed'
  }