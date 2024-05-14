import { Routes } from '@angular/router';
import { EditMemberComponent } from './edit-member/edit-member.component';
import { MembersListComponent } from './members-list/members-list.component';
import path from 'path';
import { FamilyTreeComponent } from './family-tree/family-tree.component';
import { UpcomingEventsComponent } from './upcoming-events/upcoming-events.component';


export const routes: Routes = [
    { path: 'members', component: MembersListComponent},
    { path: 'edit/:id', component: EditMemberComponent},
    { path: 'tree', component: FamilyTreeComponent},
    { path: 'events', component: UpcomingEventsComponent}
];
