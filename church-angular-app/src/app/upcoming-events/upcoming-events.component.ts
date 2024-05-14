import { DatePipe, NgFor, NgIf } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';

@Component({
  selector: 'app-upcoming-events',
  standalone: true,
  imports: [NgIf, NgFor, DatePipe],
  templateUrl: './upcoming-events.component.html',
  styleUrl: './upcoming-events.component.css'
})
export class UpcomingEventsComponent {
  events: any[] = [];

  constructor(private http : HttpClient){}

  ngOnInit(){
    let response = this.http.get("http://localhost:8080/events");
    response.subscribe((data: any)=> this.events = data);
  }
  
}
