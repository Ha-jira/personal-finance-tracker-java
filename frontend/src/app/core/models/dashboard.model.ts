export interface DashboardSummary {
  totalIncome: number;
  totalExpense: number;
  savings: number;
}

export interface CategoryBreakdown {
  category: string;
  total: number;
}

export interface MonthlyTrend {
  month: number;
  income: number;
  expense: number;
}
