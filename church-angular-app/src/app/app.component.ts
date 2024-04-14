import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MembersListComponent } from "./members-list/members-list.component";
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-root',
    standalone: true,
    templateUrl: './app.component.html',
    styleUrl: './app.component.css',
    imports: [RouterOutlet, MembersListComponent, CommonModule]
})
export class AppComponent {
  title = 'church-angular-app';
}
