export type TransactionType = 'INCOME' | 'EXPENSE';

export interface Transaction {
  id: number;
  type: TransactionType;
  category: string;
  amount: number;
  description: string | null;
  date: string;
}

export interface TransactionRequest {
  type: TransactionType;
  category: string;
  amount: number;
  description: string;
  date: string;
}
