import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CategoryBreakdown, DashboardSummary, MonthlyTrend } from '../models/dashboard.model';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private apiUrl = `${environment.apiUrl}/dashboard`;

  constructor(private http: HttpClient) {}

  getSummary(month: number, year: number): Observable<DashboardSummary> {
    const params = new HttpParams().set('month', month).set('year', year);
    return this.http.get<DashboardSummary>(`${this.apiUrl}/summary`, { params });
  }

  getCategoryBreakdown(month: number, year: number): Observable<CategoryBreakdown[]> {
    const params = new HttpParams().set('month', month).set('year', year);
    return this.http.get<CategoryBreakdown[]>(`${this.apiUrl}/category-breakdown`, { params });
  }

  getMonthlyTrend(year: number): Observable<MonthlyTrend[]> {
    const params = new HttpParams().set('year', year);
    return this.http.get<MonthlyTrend[]>(`${this.apiUrl}/monthly-trend`, { params });
  }
}
