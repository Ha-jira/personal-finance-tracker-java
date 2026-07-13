import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReportService } from '../../core/services/report.service';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent {
  selectedMonth = new Date().getMonth() + 1;
  selectedYear = new Date().getFullYear();
  downloading = false;
  errorMessage = '';

  constructor(private reportService: ReportService) {}

  downloadMonthly(format: string): void {
    this.downloading = true;
    this.errorMessage = '';
    this.reportService.downloadMonthly(this.selectedMonth, this.selectedYear, format).subscribe({
      next: (blob) => this.triggerDownload(blob, `monthly-report-${this.selectedYear}-${this.selectedMonth}.${this.extension(format)}`),
      error: () => { this.downloading = false; this.errorMessage = 'Could not generate report.'; },
      complete: () => this.downloading = false
    });
  }

  downloadAnnual(format: string): void {
    this.downloading = true;
    this.errorMessage = '';
    this.reportService.downloadAnnual(this.selectedYear, format).subscribe({
      next: (blob) => this.triggerDownload(blob, `annual-report-${this.selectedYear}.${this.extension(format)}`),
      error: () => { this.downloading = false; this.errorMessage = 'Could not generate report.'; },
      complete: () => this.downloading = false
    });
  }

  private extension(format: string): string {
    if (format === 'excel') return 'xlsx';
    return format;
  }

  private triggerDownload(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
  }
}
