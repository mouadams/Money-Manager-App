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

📱 Fully offline (SQLite local database)

🛠️ Technologies Used

Java (Android development)

XML (UI Design)

SQLite (Local database)

RecyclerView

Material Design Components

🗄️ Database Structure

Table: transactions

Column	Type
id	INTEGER (Primary Key, Auto Increment)
type	TEXT (deposit / withdrawal)
amount	REAL
reason	TEXT
date	TEXT
