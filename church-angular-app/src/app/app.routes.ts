import { Routes } from '@angular/router';
import { EditMemberComponent } from './edit-member/edit-member.component';
import { MembersListComponent } from './members-list/members-list.component';
import path from 'path';


export const routes: Routes = [
    { path: 'members', component: MembersListComponent},
    { path: 'edit', component: EditMemberComponent}
];
