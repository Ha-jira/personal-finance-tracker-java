import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BudgetService } from '../../core/services/budget.service';
import { Budget } from '../../core/models/budget.model';

@Component({
  selector: 'app-budgets',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './budgets.component.html',
  styleUrls: ['./budgets.component.css']
})
export class BudgetsComponent implements OnInit {
  budgets: Budget[] = [];
  form: FormGroup;
  showForm = false;
  errorMessage = '';

  selectedMonth = new Date().getMonth() + 1;
  selectedYear = new Date().getFullYear();

  categories = ['Food', 'Travel', 'Rent', 'Utilities', 'Entertainment', 'Health', 'Shopping', 'Other'];

  constructor(private fb: FormBuilder, private budgetService: BudgetService) {
    this.form = this.fb.group({
      category: ['', Validators.required],
      monthlyLimit: [null, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.budgetService.getAll(this.selectedMonth, this.selectedYear).subscribe(res => this.budgets = res);
  }

  openAddForm(): void {
    this.form.reset();
    this.showForm = true;
  }

  submit(): void {
    if (this.form.invalid) return;
    this.errorMessage = '';

    this.budgetService.create({
      category: this.form.value.category,
      monthlyLimit: this.form.value.monthlyLimit,
      month: this.selectedMonth,
      year: this.selectedYear
    }).subscribe({
      next: () => {
        this.showForm = false;
        this.load();
      },
      error: (err) => this.errorMessage = err.error?.message || 'Something went wrong.'
    });
  }

  delete(id: number): void {
    if (!confirm('Delete this budget?')) return;
    this.budgetService.delete(id).subscribe(() => this.load());
  }

  cancel(): void {
    this.showForm = false;
  }

  progressPercent(b: Budget): number {
    const pct = (b.spent / b.monthlyLimit) * 100;
    return Math.min(pct, 100);
  }
}
