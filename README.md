💰 Money Manager App
📱 Overview

Money Manager is a modern Android application built using Java and XML that helps users track their income and expenses efficiently.

The app allows users to record deposits and withdrawals with detailed reasons, automatically calculate the total balance, and maintain a clear transaction history for better financial management.

All data is stored locally using SQLite, making the application lightweight, fast, and fully offline.

🚀 Features

➕ Add Deposit transactions

➖ Add Withdrawal transactions

📝 Add reason for each transaction (Salary, Bonus, Rent, WiFi, etc.)

📅 Automatic date saving

💰 Real-time balance calculation

📋 Transaction history displayed using RecyclerView

🗑 Delete transactions

🎨 Modern UI using Material Design components

🔐 Secure user authentication with email/password

📱 Fully offline (SQLite local database)

🔐 User Authentication (SQLite-based)

🛠️ Technologies Used

Java (Android development)

XML (UI Design)

SQLite (Local database)

RecyclerView

Material Design Components

SQLite Authentication (Email/Password with SHA-256 hashing)

🗄️ Database Structure

Table: transactions

Column	Type
id	INTEGER (Primary Key, Auto Increment)
type	TEXT (deposit / withdrawal)
amount	REAL
reason	TEXT
date	TEXT

## 🔐 Authentication System

The app uses SQLite-based authentication with the following features:

- **User Registration:** Users can create accounts with email and password
- **Secure Password Storage:** Passwords are hashed using SHA-256 before storage
- **Session Management:** User sessions are managed using SharedPreferences
- **Email Validation:** Email format validation on registration and login
- **Password Requirements:** Minimum 6 characters required

### Database Structure

**Table: users**

| Column | Type |
|--------|------|
| id | INTEGER (Primary Key, Auto Increment) |
| full_name | TEXT (NOT NULL) |
| email | TEXT (UNIQUE, NOT NULL) |
| password | TEXT (NOT NULL, SHA-256 hashed) |

## 📝 Notes

- The app will check authentication status on startup
- Users must register/login before accessing the main features
- Logout functionality is available in the main screen (top-left button)
- All user data is stored locally in SQLite (fully offline)
- Passwords are securely hashed using SHA-256 algorithm
- Each user's transactions are stored per device (local database)
