import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Chart, registerables } from 'chart.js';
import { DashboardService } from '../../core/services/dashboard.service';
import { DashboardSummary, CategoryBreakdown, MonthlyTrend } from '../../core/models/dashboard.model';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  @ViewChild('categoryChart') categoryChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('trendChart') trendChartRef!: ElementRef<HTMLCanvasElement>;

  summary: DashboardSummary | null = null;
  categoryBreakdown: CategoryBreakdown[] = [];
  monthlyTrend: MonthlyTrend[] = [];

  selectedMonth = new Date().getMonth() + 1;
  selectedYear = new Date().getFullYear();

  private categoryChart: Chart | null = null;
  private trendChart: Chart | null = null;

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.dashboardService.getSummary(this.selectedMonth, this.selectedYear).subscribe(res => this.summary = res);

    this.dashboardService.getCategoryBreakdown(this.selectedMonth, this.selectedYear).subscribe(res => {
      this.categoryBreakdown = res;
      this.renderCategoryChart();
    });

    this.dashboardService.getMonthlyTrend(this.selectedYear).subscribe(res => {
      this.monthlyTrend = res;
      this.renderTrendChart();
    });
  }

  onFilterChange(): void {
    this.loadData();
  }

  private renderCategoryChart(): void {
    if (!this.categoryChartRef) return;
    this.categoryChart?.destroy();

    this.categoryChart = new Chart(this.categoryChartRef.nativeElement, {
      type: 'pie',
      data: {
        labels: this.categoryBreakdown.map(c => c.category),
        datasets: [{
          data: this.categoryBreakdown.map(c => c.total),
          backgroundColor: ['#4f46e5', '#06b6d4', '#f59e0b', '#ef4444', '#10b981', '#8b5cf6', '#ec4899', '#84cc16']
        }]
      },
      options: { responsive: true, plugins: { legend: { position: 'bottom' } } }
    });
  }

  private renderTrendChart(): void {
    if (!this.trendChartRef) return;
    this.trendChart?.destroy();

    const monthNames = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];

    this.trendChart = new Chart(this.trendChartRef.nativeElement, {
      type: 'line',
      data: {
        labels: this.monthlyTrend.map(t => monthNames[t.month - 1]),
        datasets: [
          { label: 'Income', data: this.monthlyTrend.map(t => t.income), borderColor: '#10b981', backgroundColor: 'rgba(16,185,129,0.1)', tension: 0.3 },
          { label: 'Expense', data: this.monthlyTrend.map(t => t.expense), borderColor: '#ef4444', backgroundColor: 'rgba(239,68,68,0.1)', tension: 0.3 }
        ]
      },
      options: { responsive: true, plugins: { legend: { position: 'bottom' } } }
    });
  }
}
