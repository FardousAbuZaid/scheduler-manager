import { ITask } from 'src/interfaces/ITask';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TaskService } from 'src/services/task-service/task.service';

@Component({
  selector: 'app-tasks',
  templateUrl: './tasks.component.html',
  styleUrls: ['./tasks.component.css'],
})
export class TasksComponent implements OnInit {
  protected tasks: ITask[] = [];

  constructor(private router: Router, private taskService: TaskService) {
    this.tasks = [];
  }

  ngOnInit(): void {
    this.getTasks();
  }

  getTasks() {
    this.taskService.getTasks().subscribe({
      next: (data) => {
        this.tasks = data;
      },
      error: (err) => console.error('Error fetching tasks', err),
    });
  }

  editTask(id: number): void {
    this.router.navigate(['/create-new-task', id]);
  }
}
