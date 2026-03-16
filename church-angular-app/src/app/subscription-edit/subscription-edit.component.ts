import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { SubscriptionService } from '../subscription.service';

@Component({
  selector: 'app-subscription-edit',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule],
  templateUrl: './subscription-edit.component.html',
  styleUrl: './subscription-edit.component.css'
})
export class SubscriptionEditComponent implements OnInit {
  subForm: FormGroup;
  familyId: string = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private subService: SubscriptionService
  ) {
    this.subForm = this.fb.group({
      pledgeAmount: [0],
      pledgeCredit: [0],
      pledgeDue: [0],
      lastPledgeDue: [0],
      pledgeStartDate: [''],
      lastPledgeDepositDate: [''],
      lastPledgeDepositAmount: [0]
    });
  }

  ngOnInit() {
    this.familyId = this.route.snapshot.paramMap.get('id')!;
    this.loadSubscription();
  }

  loadSubscription() {
    this.subService.getById(this.familyId).subscribe(data => {
      // Patch values into the form - if data is null, fields remain empty/default
      this.subForm.patchValue(data);
    });
  }

  save() {
    this.subService.update(this.familyId, this.subForm.value).subscribe(() => {
      alert('Subscription updated successfully!');
    });
  }
}
