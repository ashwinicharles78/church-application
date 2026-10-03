import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Member } from '../memeber';
import { PaymentVoucher } from '../payment-vouchers/payment-voucher.model';
import { PaymentVoucherService } from '../payment-vouchers/payment-voucher.service';

interface ChurchEvent {
  eventType: string;
  date: string | Date;
  memberName: string;
  familyId: string | null;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  members: Member[] = [];
  events: ChurchEvent[] = [];
  recentVouchers: PaymentVoucher[] = [];
  membersLoading = true;
  eventsLoading = true;
  vouchersLoading = true;
  membersError = false;
  eventsError = false;
  vouchersError = false;

  constructor(private http: HttpClient, readonly voucherService: PaymentVoucherService) {}

  ngOnInit(): void {
    this.http.get<Member[]>('http://localhost:8080/all-members').subscribe({
      next: members => {
        this.members = members;
        this.membersLoading = false;
      },
      error: () => {
        this.membersError = true;
        this.membersLoading = false;
      }
    });

    this.http.get<ChurchEvent[]>('http://localhost:8080/events').subscribe({
      next: events => {
        this.events = events;
        this.eventsLoading = false;
      },
      error: () => {
        this.eventsError = true;
        this.eventsLoading = false;
      }
    });

    this.voucherService.getLatest().subscribe({
      next: vouchers => {
        this.recentVouchers = vouchers;
        this.vouchersLoading = false;
      },
      error: () => {
        this.vouchersError = true;
        this.vouchersLoading = false;
      }
    });
  }

  get activeMemberCount(): number {
    return this.members.filter(member => member.status?.toLowerCase() === 'active').length;
  }

  printUpcomingEvents(): void {
    window.print();
  }
}