import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IReport } from 'src/interfaces/IReport';

@Injectable({
  providedIn: 'root',
})
export class ReportsService {
  private apiUrl = 'http://localhost:8080/api/reports';

  constructor(private http: HttpClient) {}

  getReports(): Observable<IReport[]> {
    return this.http.get<IReport[]>(this.apiUrl);
  }
}
