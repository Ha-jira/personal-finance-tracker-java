import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TransactionService } from '../../core/services/transaction.service';
import { Transaction } from '../../core/models/transaction.model';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.css']
})
export class TransactionsComponent implements OnInit {
  transactions: Transaction[] = [];
  form: FormGroup;
  editingId: number | null = null;
  showForm = false;
  errorMessage = '';

  categories = ['Food', 'Travel', 'Rent', 'Utilities', 'Entertainment', 'Health', 'Shopping', 'Salary', 'Other'];

  constructor(private fb: FormBuilder, private transactionService: TransactionService) {
    this.form = this.fb.group({
      type: ['EXPENSE', Validators.required],
      category: ['', Validators.required],
      amount: [null, [Validators.required, Validators.min(0.01)]],
      description: [''],
      date: [new Date().toISOString().substring(0, 10), Validators.required]
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.transactionService.getAll().subscribe(res => this.transactions = res);
  }

  openAddForm(): void {
    this.editingId = null;
    this.form.reset({ type: 'EXPENSE', category: '', amount: null, description: '', date: new Date().toISOString().substring(0, 10) });
    this.showForm = true;
  }

  edit(t: Transaction): void {
    this.editingId = t.id;
    this.form.setValue({
      type: t.type,
      category: t.category,
      amount: t.amount,
      description: t.description || '',
      date: t.date
    });
    this.showForm = true;
  }

  submit(): void {
    if (this.form.invalid) return;
    this.errorMessage = '';

    const request = this.form.value;

    const obs = this.editingId
      ? this.transactionService.update(this.editingId, request)
      : this.transactionService.create(request);

    obs.subscribe({
      next: () => {
        this.showForm = false;
        this.load();
      },
      error: (err) => this.errorMessage = err.error?.message || 'Something went wrong.'
    });
  }

  delete(id: number): void {
    if (!confirm('Delete this transaction?')) return;
    this.transactionService.delete(id).subscribe(() => this.load());
  }

  cancel(): void {
    this.showForm = false;
  }
}
