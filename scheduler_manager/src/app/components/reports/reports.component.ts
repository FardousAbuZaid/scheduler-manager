import { Component, OnInit } from '@angular/core';
import { ReportsService } from 'src/services/reports-service/reports.service';
import { IReport } from 'src/interfaces/IReport';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css'],
})
export class ReportsComponent implements OnInit {
  reports: IReport[] = [];

  constructor(private reportsService: ReportsService) {}

  ngOnInit(): void {
    this.getReports();
  }

  getReports() {
    this.reportsService.getReports().subscribe({
      next: (data) => {
        this.reports = data;
      },
      error: (err) => console.error('Error fetching reports', err),
    });
  }
}
