import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { map } from 'rxjs';

// ---------------------------------------------------------------------------
// Interfaces - matching http://localhost:8080/members-tree response
// ---------------------------------------------------------------------------

interface FamilyMemberDto {
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

interface FamilySubscriptionDto {
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

interface FamilyNode {
  familyId: number;
  head: FamilyMemberDto;
  spouse?: FamilyMemberDto;
  unmarriedChildren: FamilyMemberDto[];
  level: number;
  children: FamilyNode[];
  pledgeAmount: number;
  pledgeCredit: number;
  pledgeDue: number;
}

// ---------------------------------------------------------------------------
// Component
// ---------------------------------------------------------------------------

@Component({
  selector: 'app-family-tree',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  templateUrl: './family-tree.component.html',
  styleUrls: ['./family-tree.component.css']
})
export class FamilyTreeComponent implements OnInit {

  private readonly API_URL = 'http://localhost:8080/members-tree';

  familyTree: FamilyNode[] = [];
  loading = true;
  error: string | null = null;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<FamilySubscriptionDto[]>(this.API_URL).pipe(
      map(families => this.buildTree(families))
    ).subscribe({
      next:  data => { this.familyTree = data; this.loading = false; },
      error: err  => { this.error = 'Failed to load family records.'; this.loading = false; console.error(err); }
    });
  }

  // ---------------------------------------------------------------------------
  // Tree builder
  // ---------------------------------------------------------------------------

  private buildTree(families: FamilySubscriptionDto[]): FamilyNode[] {

    // Build lookups across all members
    const idToFamilyId   = new Map<string, number>(); // membershipId -> familyId
    const nameToFamilyId = new Map<string, number>(); // normalised name variant -> familyId

    families.forEach(f =>
      f.members.forEach(m => {
        idToFamilyId.set(String(m.membershipId), f.familyId);
        // Store all name variants so abbreviated middle names still match
        // e.g. "Sanjay Cherchill Charles" also stored as "Sanjay C Charles"
        this.nameVariants(m).forEach(v => nameToFamilyId.set(v, f.familyId));
      })
    );

    // Build one FamilyNode per family
    const nodeMap = new Map<number, FamilyNode>();
    families.forEach(f => nodeMap.set(f.familyId, this.buildNode(f)));

    // Wire parent -> child edges
    const childIds = new Set<number>();

    nodeMap.forEach(node => {
      const head = node.head;
      let parentFamilyId: number | undefined;

      // Tier 1: fatherId match (direct ID - most reliable)
      if (this.hasValue(head.fatherId)) {
        parentFamilyId = idToFamilyId.get(head.fatherId!);
      }

      // Tier 2: fatherName match (fallback when fatherId is null)
      if (!parentFamilyId && this.hasValue(head.fatherName)) {
        parentFamilyId = nameToFamilyId.get(this.normalise(head.fatherName!));
      }

      if (parentFamilyId != null && parentFamilyId !== node.familyId) {
        const parentNode = nodeMap.get(parentFamilyId);
        if (parentNode) {
          parentNode.children.push(node);
          childIds.add(node.familyId);
        }
      }
    });

    // Root nodes = families not linked as a child of anyone
    const roots = Array.from(nodeMap.values()).filter(n => !childIds.has(n.familyId));

    roots.forEach(r => this.sortChildren(r));
    roots.forEach(r => this.assignLevels(r, 0));

    return roots;
  }

  // ---------------------------------------------------------------------------
  // Node builder
  // ---------------------------------------------------------------------------

  private buildNode(family: FamilySubscriptionDto): FamilyNode {
    const { head, spouse, unmarriedChildren } = this.classify(family.members);
    return {
      familyId:         family.familyId,
      head:             head ?? family.members[0],
      spouse,
      unmarriedChildren,
      level:            0,
      children:         [],
      pledgeAmount:     family.pledgeAmount,
      pledgeCredit:     family.pledgeCredit,
      pledgeDue:        family.pledgeDue,
    };
  }

  /**
   * Classifies members into head, spouse, and unmarried children.
   *
   * Head selection (in priority order):
   *   1. Married male — regardless of whether he has a fatherId.
   *      The fatherId tells us WHERE in the tree he belongs, not whether
   *      he is a head. Sagar has fatherId="302" (child of Sanjay) but is
   *      still the head of his own family 163.
   *      BUG THAT WAS HERE: previously filtered !hasValue(fatherId) which
   *      caused Angela (no fatherId) to be picked as head instead of Sagar.
   *   2. Any member with no spouseId (single parent / unmarried head).
   *   3. First member as last resort.
   *
   * Spouse = member whose spouseId matches the head's membershipId.
   * Unmarried children = remaining members with no spouseId.
   */
  private classify(members: FamilyMemberDto[]): {
    head: FamilyMemberDto | undefined;
    spouse: FamilyMemberDto | undefined;
    unmarriedChildren: FamilyMemberDto[];
  } {
    // Step 1: find the married male head (fatherId is irrelevant here)
    let head = members.find(m =>
      m.gender?.toLowerCase() === 'male' &&
      m.maritalStatus?.toLowerCase() === 'married'
    );

    // Step 2: fallback - any member without a spouseId
    if (!head) {
      head = members.find(m => !this.hasValue(m.spouseId));
    }

    // Step 3: last resort
    if (!head) head = members[0];

    // Spouse = the member whose spouseId points to the head
    const headId = String(head?.membershipId ?? '');
    const spouse = members.find(m =>
      m.membershipId !== head?.membershipId &&
      m.spouseId === headId
    );

    // Unmarried children = everyone else with no spouseId
    const unmarriedChildren = members.filter(m =>
      m.membershipId !== head?.membershipId &&
      m.membershipId !== spouse?.membershipId &&
      !this.hasValue(m.spouseId)
    );

    return { head, spouse, unmarriedChildren };
  }

  // ---------------------------------------------------------------------------
  // Template helpers (called from HTML)
  // ---------------------------------------------------------------------------

  initial(m: FamilyMemberDto): string {
    return (m.firstName?.[0] ?? '?').toUpperCase();
  }

  displayName(m: FamilyMemberDto): string {
    return `${m.firstName ?? ''} ${m.lastName ?? ''}`.trim();
  }

  headRole(node: FamilyNode): string {
    return (node.head.gender ?? '').toLowerCase() === 'female' ? 'Mother' : 'Father';
  }

  generationLabel(level: number): string {
    const labels = ['1st Generation', '2nd Generation', '3rd Generation', '4th Generation'];
    return labels[level] ?? `Generation ${level + 1}`;
  }

  hasChildren(node: FamilyNode): boolean {
    return node.children.length > 0;
  }

  headColor(familyId: number): string {
    const colors = ['#2d5a3d', '#3d4f7a', '#7a3d4f', '#4a5a2d', '#5a3d7a'];
    return colors[familyId % colors.length];
  }

  spouseColor(familyId: number): string {
    const colors = ['#b8960c', '#c05050', '#4a90c0', '#8060c0', '#50a878'];
    return colors[familyId % colors.length];
  }

  // ---------------------------------------------------------------------------
  // Private utilities
  // ---------------------------------------------------------------------------

  private assignLevels(node: FamilyNode, level: number): void {
    node.level = level;
    node.children.forEach(c => this.assignLevels(c, level + 1));
  }

  private sortChildren(node: FamilyNode): void {
    node.children.sort((a, b) => a.familyId - b.familyId);
    node.children.forEach(c => this.sortChildren(c));
  }

  /**
   * Returns every plausible normalised name variant for a member.
   *
   * Needed because fatherName is often abbreviated, e.g.:
   *   Member stores: firstName=Sanjay, middleName=Cherchill, lastName=Charles
   *   fatherName ref: "Sanjay C Charles" (abbreviated middle initial)
   *
   * Variants produced:
   *   "sanjay cherchill charles"  <- full
   *   "sanjay c charles"          <- abbreviated middle initial
   *   "sanjay charles"            <- no middle
   */
  private nameVariants(m: FamilyMemberDto): string[] {
    const first  = (m.firstName  ?? '').trim();
    const middle = (m.middleName ?? '').trim();
    const last   = (m.lastName   ?? '').trim();

    const variants = new Set<string>();

    if (first && last) {
      variants.add(this.normalise(`${first} ${last}`));
      if (middle) {
        variants.add(this.normalise(`${first} ${middle} ${last}`));
        variants.add(this.normalise(`${first} ${middle[0]} ${last}`));
      }
    }

    return Array.from(variants);
  }

  private normalise(s: string): string {
    return s.toLowerCase().replace(/\s+/g, ' ').trim();
  }

  private hasValue(v: string | null | undefined): boolean {
    if (!v) return false;
    const t = v.trim();
    return t.length > 0 &&
           t.toLowerCase() !== 'n/a' &&
           t.toLowerCase() !== 'na';
  }
}