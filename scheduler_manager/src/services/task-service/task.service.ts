import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ITask } from 'src/interfaces/ITask';                                                                                                                                                                                                                                                                                                                                                        

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private apiUrl = 'http://localhost:8080/api/tasks';

  constructor(private http: HttpClient) {}  

  getTasks(): Observable<ITask[]> {
    return this.http.get<ITask[]>(this.apiUrl);
  }

  getTaskById(id: number) {
    return this.http.get<ITask>(`${this.apiUrl}/${id}`);
  }

  createTask(taskData: ITask) {
    return this.http.post(this.apiUrl, taskData);
  }

  updateTask(id: number, taskData: ITask) {
    return this.http.put<ITask>(`${this.apiUrl}/${id}`, taskData);
  }
}
