import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { TasksComponent } from './components/tasks/tasks.component';
import { CalenderComponent } from './components/calender/calender.component';
import { ReportsComponent } from './components/reports/reports.component';
import { CreateNewTaskComponent } from './components/tasks/create-new-task/create-new-task.component';
import { HttpClientModule } from '@angular/common/http';
import { TaskService } from 'src/services/task-service/task.service';
import { FormsModule } from '@angular/forms';
@NgModule({
  declarations: [
    AppComponent,
    SidebarComponent,
    TasksComponent,
    CalenderComponent,
    ReportsComponent,
    CreateNewTaskComponent,
  ],
  imports: [BrowserModule, AppRoutingModule, HttpClientModule, FormsModule],
  providers: [TaskService],
  bootstrap: [AppComponent],
})
export class AppModule {}
