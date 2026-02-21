// ── Raw API shapes ─────────────────────────────────────────────────────────────

export interface FamilyMemberDto {
  membershipId: number;
  familyId: number;
  firstName: string;
  middleName?: string;
  lastName: string;
  gender?: string;
  fatherName?: string | null;
  fatherId?: string | null;
  motherName?: string | null;
  motherId?: string | null;
  spouseName?: string | null;
  spouseId?: string | null;
  maritalStatus?: string;
  dob?: string;
  age?: string | null;
  contact?: string;
  email?: string | null;
  status?: string;
  baptised?: string;
  confirmed?: string;
  fullMember?: string;
  pledgeAmount?: number | null;
  dateOfMarriage?: string | null;
  yearsOfMarriage?: string;
}

export interface FamilySubscriptionDto {
  familyId: number;
  members: FamilyMemberDto[];
  headMemberId?: number | null;
  pledgeAmount: number;
  pledgeCredit: number;
  pledgeDue: number;
  pledgeStartDate?: string | null;
  lastPledgeDepositDate?: string | null;
  lastPledgeDepositAmount: number;
}

// ── Client-side tree node ──────────────────────────────────────────────────────

export interface FamilyNode {
  familyId: number;
  /** The primary head: father (married male) or single parent */
  head: FamilyMemberDto;
  /** Spouse of the head, if present in the same family */
  spouse?: FamilyMemberDto;
  /** Unmarried members still in this family (no spouseId) */
  unmarriedChildren: FamilyMemberDto[];
  /** Depth in tree: 0 = root generation */
  level: number;
  /** Child families whose head's fatherName resolves to this family's head */
  children: FamilyNode[];
  pledgeAmount: number;
  pledgeCredit: number;
  pledgeDue: number;
}
