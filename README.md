# Personal Finance Tracker

A full-stack personal finance tracker built with **Angular**, **Spring Boot**, **Spring Security (JWT)**, **Spring Data JPA**, and **MySQL**.

## Tech Stack
- **Frontend:** Angular
- **Backend:** Spring Boot 3, Spring Security, Spring Data JPA
- **Database:** MySQL 8
- **Auth:** JWT (JSON Web Tokens)
- **Build tools:** Maven, npm
- **Version control:** Git & GitHub

## Project Status
All core features are implemented:
- [x] User authentication (register / login / JWT / forgot & reset password)
- [x] Transactions (add / edit / delete / list, filterable by month)
- [x] Budgets (set monthly limits per category, live status: Safe / Near Limit / Over Budget)
- [x] Dashboard (totals, savings, category-wise pie chart, monthly income vs expense trend)
- [x] Reports (monthly & annual, downloadable as PDF, CSV, or Excel)

## Folder Structure
```
personal-finance-tracker/
├── backend/     -> Spring Boot REST API
└── frontend/    -> Angular application
```

## Backend Setup (quick reference)
1. Create a MySQL database named `finance_tracker`.
2. Edit `backend/src/main/resources/application.properties` with your own MySQL username/password (and, optionally, Gmail SMTP credentials for password-reset emails).
3. Open the `backend` folder in IntelliJ IDEA and run `FinanceTrackerApplication`.
4. API runs at `http://localhost:8080`.

## Frontend Setup (quick reference)
1. In a terminal: `cd frontend && npm install && npm start`
2. App runs at `http://localhost:4200`.


## API Endpoints

| Method | Endpoint                          | Description                          | Auth required |
|--------|------------------------------------|---------------------------------------|----------------|
| POST   | /api/auth/register                 | Create a new account                  | No             |
| POST   | /api/auth/login                    | Log in, receive a JWT token           | No             |
| POST   | /api/auth/forgot-password          | Request a password reset code         | No             |
| POST   | /api/auth/reset-password           | Reset password using the code         | No             |
| GET    | /api/user/me                       | Get the logged-in user's profile      | Yes            |
| GET/POST/PUT/DELETE | /api/transactions     | Manage transactions                   | Yes            |
| GET/POST/PUT/DELETE | /api/budgets          | Manage monthly budgets                | Yes            |
| GET    | /api/dashboard/summary             | Income / expense / savings totals     | Yes            |
| GET    | /api/dashboard/category-breakdown  | Spending by category                  | Yes            |
| GET    | /api/dashboard/monthly-trend       | Income vs expense per month           | Yes            |
| GET    | /api/reports/monthly?format=       | Monthly report (csv/pdf/excel)        | Yes            |
| GET    | /api/reports/annual?format=        | Annual report (csv/pdf/excel)         | Yes            |
