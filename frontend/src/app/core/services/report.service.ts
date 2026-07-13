import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private apiUrl = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient) {}

  downloadMonthly(month: number, year: number, format: string) {
    return this.http.get(`${this.apiUrl}/monthly?month=${month}&year=${year}&format=${format}`, {
      responseType: 'blob'
    });
  }

  downloadAnnual(year: number, format: string) {
    return this.http.get(`${this.apiUrl}/annual?year=${year}&format=${format}`, {
      responseType: 'blob'
    });
  }
}
