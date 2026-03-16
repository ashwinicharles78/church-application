import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root' // Makes this service available app-wide
})
export class SubscriptionService {
  // Update this URL to point to your Spring Boot backend
  private baseUrl = 'http://localhost:8080/subscription';

  constructor(private http: HttpClient) {}

  /**
   * Fetches the subscription details by familyId.
   */
  getById(familyId: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/${familyId}`);
  }

  /**
   * Updates the subscription record.
   */
  update(familyId: string, subscriptionData: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${familyId}`, subscriptionData);
  }
}