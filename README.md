# Personal Finance Manager

A comprehensive personal finance management system that enables users to track income, expenses, and savings goals through a robust web-based application.

## Features

- User Management and Authentication
- Transaction Management (CRUD operations)
- Category Management (Default and Custom)
- Savings Goals Tracking
- Monthly and Yearly Reports
- Data Isolation and Security

## Technology Stack

- Java 17+
- Spring Boot 3.5.0
- Spring Security
- Spring Data JPA
- H2 Database
- Gradle
- JUnit 5 & Mockito

## Prerequisites

- Java 17 or higher
- Gradle 7.0 or higher
- IDE (IntelliJ IDEA recommended)

## Setup Instructions

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd FinanceManager
   ```

2. Build the project:
   ```bash
   ./gradlew build
   ```

3. Run the application:
   ```bash
   ./gradlew bootRun
   ```

The application will start on `http://localhost:8080`

## API Documentation

### Authentication

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "securePassword123",
    "fullName": "John Doe",
    "phoneNumber": "+1234567890"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "securePassword123"
}
```

#### Logout
```http
POST /api/auth/logout
```

### Transactions

#### Create Transaction
```http
POST /api/transactions
Content-Type: application/json

{
    "amount": 100.00,
    "date": "2024-03-20",
    "categoryId": 1,
    "description": "Grocery shopping"
}
```

#### Get All Transactions
```http
GET /api/transactions
```

#### Get Transactions by Date Range
```http
GET /api/transactions/filter/date?start=2024-01-01&end=2024-03-20
```

#### Update Transaction
```http
PUT /api/transactions/{id}
Content-Type: application/json

{
    "amount": 150.00,
    "categoryId": 2,
    "description": "Updated description"
}
```

#### Delete Transaction
```http
DELETE /api/transactions/{id}
```

### Categories

#### Create Category
```http
POST /api/categories
Content-Type: application/json

{
    "name": "Custom Category",
    "type": "EXPENSE",
    "description": "Custom category description"
}
```

#### Get All Categories
```http
GET /api/categories
```

#### Update Category
```http
PUT /api/categories/{id}
Content-Type: application/json

{
    "name": "Updated Category",
    "type": "EXPENSE",
    "description": "Updated description"
}
```

#### Delete Category
```http
DELETE /api/categories/{id}
```

### Savings Goals

#### Create Goal
```http
POST /api/goals
Content-Type: application/json

{
    "goalName": "New Car",
    "targetAmount": 25000.00,
    "targetDate": "2024-12-31",
    "startDate": "2024-01-01"
}
```

#### Get All Goals
```http
GET /api/goals
```

#### Update Goal
```http
PUT /api/goals/{id}
Content-Type: application/json

{
    "targetAmount": 30000.00,
    "targetDate": "2024-12-31",
    "startDate": "2024-01-01"
}
```

#### Delete Goal
```http
DELETE /api/goals/{id}
```

### Reports

#### Monthly Report
```http
GET /api/reports/monthly/{year}/{month}
```

#### Yearly Report
```http
GET /api/reports/yearly/{year}
```

## Design Decisions

### Architecture

1. **Layered Architecture**
   - Controller Layer: Handles HTTP requests and responses
   - Service Layer: Contains business logic
   - Repository Layer: Manages data persistence
   - DTO Layer: Separates API contracts from internal models

2. **Security**
   - Session-based authentication
   - Secure cookie management
   - Data isolation between users
   - Input validation and sanitization

3. **Data Management**
   - JPA for database operations
   - H2 database for development
   - Transaction management
   - Proper indexing for performance

4. **Error Handling**
   - Global exception handler
   - Custom exception classes
   - Proper HTTP status codes
   - Descriptive error messages

### Default Categories

The system provides the following default categories:

Income:
- Salary

Expenses:
- Food
- Rent
- Transportation
- Entertainment
- Healthcare
- Utilities

### Savings Goals Calculation

Progress calculation:
- Total Income - Total Expenses since goal start date
- Percentage completion = (Current Progress / Target Amount) * 100
- Status tracking: COMPLETED, OVERDUE, IN_PROGRESS

### Reports Generation

1. **Monthly Reports**
   - Category-wise income and expenses
   - Net savings calculation
   - Trend analysis compared to previous month

2. **Yearly Reports**
   - Monthly breakdown
   - Category-wise aggregation
   - Year-over-year comparison

## Testing

Run tests using:
```bash
./gradlew test
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 