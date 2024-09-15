import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { ITask } from 'src/interfaces/ITask';
import { TaskService } from 'src/services/task-service/task.service';

@Component({
  selector: 'app-create-new-task',
  templateUrl: './create-new-task.component.html',
  styleUrls: ['./create-new-task.component.css'],
})
export class CreateNewTaskComponent implements OnInit {
  formData = {
    startDate: '',
    endDate: '',
    startHour: 0,
    startMinute: 0,
    duration: 0,
    frequency: 'daily',
    repeatInterval: 0,
  };

  isEditMode = false;
  taskId: number | null = null;

  constructor(
    private taskService: TaskService,
    private router: Router,
    private route: ActivatedRoute
  ) {}
  ngOnInit(): void {
    this.taskId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.taskId) {
      this.isEditMode = true;
      this.loadTask(this.taskId);
    }
  }

  loadTask(id: number): void {
    this.taskService.getTaskById(id).subscribe({
      next: (task) => {
        this.formData = {
          startDate: this.formatDateToYYYYMMDD(new Date(task.startTime)),
          endDate: this.formatDateToYYYYMMDD(new Date(task.endTime)),
          startHour: new Date(task.startTime).getHours(),
          startMinute: new Date(task.startTime).getMinutes(),
          duration: parseInt(task.duration, 10),
          frequency: 'daily',
          repeatInterval: task.repeatInterval,
        };
        this.taskId = id;
      },
      error: (err) => console.error('Error loading task', err),
    });
  }

  onSubmit(form: NgForm) {
    if (form.valid) {
      const {
        startDate,
        endDate,
        startHour,
        startMinute,
        duration,
        repeatInterval,
      } = this.formData;

      const start = new Date(startDate);
      const startTime = new Date(start);
      startTime.setHours(startHour);
      startTime.setMinutes(startMinute);
      const endTime = new Date(startTime);
      endTime.setMinutes(startTime.getMinutes() + duration);

      const tasks = this.generateTasks(
        startDate,
        endDate,
        startTime,
        endTime,
        duration,
        repeatInterval
      );

      if (this.isEditMode && this.taskId) {
        tasks.forEach((task) => {
          this.taskService.updateTask(this.taskId!, task).subscribe({
            error: (error) => console.error('Error updating task', error),
          });
        });
      } else {
        tasks.forEach((task) => {
          this.taskService.createTask(task).subscribe({
            error: (error) => console.error('Error creating task', error),
          });
        });
      }
      this.router.navigate(['/tasks']);
    }
  }

  generateTasks(
    startDate: string,
    endDate: string,
    startTime: Date,
    endTime: Date,
    duration: number,
    repeatEvery: number
  ): ITask[] {
    const tasks: ITask[] = [];
    let currentDate = new Date(startDate);
    let end = new Date(endDate);

    while (currentDate <= end) {
      tasks.push({
        id: 0,
        startTime: `${this.formatDateToYYYYMMDD(currentDate)} ${this.formatTime(
          startTime
        )}`,
        endTime: `${this.formatDateToYYYYMMDD(currentDate)} ${this.formatTime(
          endTime
        )}`,
        duration: duration.toString() + ' min',
        repeatInterval: repeatEvery,
      });
      currentDate.setDate(currentDate.getDate() + repeatEvery);
    }
    return tasks;
  }

  formatTime(date: Date): string {
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }

  formatDateToYYYYMMDD(date: Date) {
    date = new Date(date);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
