import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TasksComponent } from './components/tasks/tasks.component';
import { CalenderComponent } from './components/calender/calender.component';
import { ReportsComponent } from './components/reports/reports.component';
import { CreateNewTaskComponent } from './components/tasks/create-new-task/create-new-task.component';

const routes: Routes = [
  { path: '', redirectTo: '/tasks', pathMatch: 'full' },
  { path: 'tasks', component: TasksComponent },
  { path: 'calendar', component: CalenderComponent },
  { path: 'reports', component: ReportsComponent },
  { path: 'create-new-task', component: CreateNewTaskComponent },
  { path: 'create-new-task/:id', component: CreateNewTaskComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
