import { Component, OnInit } from '@angular/core';
import { ITask } from 'src/interfaces/ITask';
import { TaskService } from 'src/services/task-service/task.service';

interface Day {
  date: Date;
  isToday: boolean;
  tasks: ITask[];
}

@Component({
  selector: 'app-calender',
  templateUrl: './calender.component.html',
  styleUrls: ['./calender.component.css'],
})
export class CalenderComponent implements OnInit {
  protected tasks: ITask[] = [];

  constructor(private taskService: TaskService) {
    this.tasks = [];
  }
  daysOfWeek = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
  months = [
    'January',
    'February',
    'March',
    'April',
    'May',
    'June',
    'July',
    'August',
    'September',
    'October',
    'November',
    'December',
  ];

  currentMonth = new Date().getMonth();
  currentYear = new Date().getFullYear();
  currentWeekStart: Date = this.getStartOfWeek(new Date());
  calendar: Day[][] = [];
  isMonthView = true;
  activeNavButton: string = 'today';

  showPopup = false;
  selectedDay: Day | null = null;

  ngOnInit(): void {
    this.getTasks();
  }
  getTasks() {
    this.taskService.getTasks().subscribe({
      next: (data) => {
        this.tasks = data;
        this.generateCalendar();
      },
      error: (err) => console.error('Error fetching tasks', err),
    });
  }
  generateCalendar() {
    if (this.isMonthView) {
      this.generateMonthView();
    } else {
      this.generateWeekView();
    }
  }

  generateMonthView() {
    const firstDay = new Date(this.currentYear, this.currentMonth, 1).getDay();
    const daysInMonth = new Date(
      this.currentYear,
      this.currentMonth + 1,
      0
    ).getDate();
    const today = new Date();
    let calendar: Day[][] = [];
    let week: Day[] = [];

    for (let i = 0; i < firstDay; i++) {
      week.push({
        date: new Date(this.currentYear, this.currentMonth, -firstDay + i + 1),
        isToday: false,
        tasks: [],
      });
    }

    for (let day = 1; day <= daysInMonth; day++) {
      const date = new Date(this.currentYear, this.currentMonth, day);
      const isToday = date.toDateString() === today.toDateString();
      const tasks = this.tasks.filter(
        (task) =>
          new Date(task.startTime).toDateString() === date.toDateString()
      );

      week.push({ date, isToday, tasks });

      if (week.length === 7) {
        calendar.push(week);
        week = [];
      }
    }

    if (week.length > 0) {
      calendar.push(week);
    }

    this.calendar = calendar;
  }

  generateWeekView() {
    const today = new Date();
    const weekStart = this.currentWeekStart;
    const weekEnd = new Date(weekStart);
    weekEnd.setDate(weekEnd.getDate() + 6);

    let calendar: Day[][] = [[]];
    let currentDate = new Date(weekStart);

    for (let i = 0; i < 7; i++) {
      const date = new Date(currentDate);
      const isToday = date.toDateString() === today.toDateString();
      const tasks = this.tasks.filter(
        (task) =>
          new Date(task.startTime).toDateString() === date.toDateString()
      );
      calendar[0].push({ date, isToday, tasks });
      currentDate.setDate(currentDate.getDate() + 1);
    }

    this.calendar = calendar;
  }

  getStartOfWeek(date: Date): Date {
    const day = date.getDay();
    const diff = date.getDate() - day + (day === 0 ? -6 : 1);
    return new Date(date.setDate(diff));
  }

  previousMonth() {
    this.activeNavButton = 'previous';
    if (this.currentMonth === 0) {
      this.currentMonth = 11;
      this.currentYear--;
    } else {
      this.currentMonth--;
    }
    this.generateCalendar();
  }

  nextMonth() {
    this.activeNavButton = 'next';
    if (this.currentMonth === 11) {
      this.currentMonth = 0;
      this.currentYear++;
    } else {
      this.currentMonth++;
    }
    this.generateCalendar();
  }

  previousWeek() {
    this.activeNavButton = 'previous';
    this.currentWeekStart.setDate(this.currentWeekStart.getDate() - 7);
    this.generateCalendar();
  }

  nextWeek() {
    this.activeNavButton = 'next';
    this.currentWeekStart.setDate(this.currentWeekStart.getDate() + 7);
    this.generateCalendar();
  }

  getWeekRange(): string {
    const start = this.currentWeekStart;
    const end = new Date(start);
    end.setDate(start.getDate() + 6);
    return `${start.getDate()} ${
      this.months[start.getMonth()]
    } - ${end.getDate()} ${this.months[end.getMonth()]}`;
  }

  goToToday() {
    this.activeNavButton = 'today';
    const today = new Date();
    this.currentMonth = today.getMonth();
    this.currentYear = today.getFullYear();
    this.currentWeekStart = this.getStartOfWeek(today);
    this.generateCalendar();
  }

  showTaskPopup(day: Day) {
    this.selectedDay = day;
    this.showPopup = true;
  }

  closePopup() {
    this.showPopup = false;
  }

  switchToMonthView() {
    this.isMonthView = true;
    this.generateCalendar();
  }

  switchToWeekView() {
    this.isMonthView = false;
    this.currentWeekStart = this.getStartOfWeek(new Date());
    this.generateCalendar();
  }
}
