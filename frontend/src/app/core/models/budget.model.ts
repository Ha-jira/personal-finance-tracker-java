export interface Budget {
  id: number;
  category: string;
  monthlyLimit: number;
  spent: number;
  month: number;
  year: number;
  status: 'SAFE' | 'WARNING' | 'OVER_BUDGET';
}

export interface BudgetRequest {
  category: string;
  monthlyLimit: number;
  month: number;
  year: number;
}
