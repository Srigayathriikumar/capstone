import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Teamdetails } from './teamdetails/teamdetails';
import { CreateProjectComponent } from './pages/create-project/create-project.component';
import { CommonResourcesComponent } from './pages/resources/common-resources/common-resources.component';
import { ManagerResourcesComponent } from './pages/resources/manager-resources/manager-resources.component';
import { AuditLogComponent } from './pages/audit-log/audit-log.component';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'create-project', component: CreateProjectComponent, canActivate: [AuthGuard] },
  { path: 'team/:id', component: Teamdetails, canActivate: [AuthGuard] },
  { path: 'team/:teamId/resources/common', component: CommonResourcesComponent, canActivate: [AuthGuard] },
  { path: 'team/:teamId/resources/manager', component: ManagerResourcesComponent, canActivate: [AuthGuard] },
  { path: 'team/:id/audit-log', component: AuditLogComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '/login' }
];
