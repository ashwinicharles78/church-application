import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MembersListComponent } from "./members-list/members-list.component";
import { CommonModule } from '@angular/common';
import { EditMemberComponent } from './edit-member/edit-member.component';
import { AddPersonComponent } from './add-person/add-person.component';

@Component({
    selector: 'app-root',
    standalone: true,
    templateUrl: './app.component.html',
    styleUrl: './app.component.css',
    imports: [RouterOutlet, MembersListComponent, CommonModule, EditMemberComponent, RouterLink, RouterLinkActive, AddPersonComponent]
})
export class AppComponent {
  title = 'church-angular-app';
}