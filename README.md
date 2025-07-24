## SmartRetailPro – “Modern Retail, Classic Simplicity” 🛒📘

# 🛒 SmartRetailPro – The Smart Way to Retail!

Welcome to **SmartRetailPro** – a powerful and intelligent 💡 Java + MySQL or Oracle PL/SQL-based console application that transforms how small businesses handle employees, inventory, customer billing, and stock verification – all in one seamless flow! ⚙️📊

---

## ✨ Highlights

👨‍💼 ***Employee Module***

➕ Add new employees with ID, name, address, and phone

🔍 View list of all employees in tabular format

📦 ***Inventory (Item) Module***

➕ Add new items with name, expiry date, price, and quantity

📊 Tracks quantity and automatically updates stock status (IN STOCK or OUT)

🚨 Shows low stock alerts if quantity falls below threshold

🧾 ***Customer Billing Module***

🟢 **Mode 1**: Manual Purchase Entry
Takes customer name & mobile once

Allows adding multiple items with quantity

Validates stock and updates remaining quantity

Shows bill summary with item-wise cost

Shows remaining stock and alerts if low

Prompts for payment mode (Cash/Card/PhonePe)

🔵** Mode 2**: Customer List Purchase
Accepts predefined item list from customer

Validates available stock automatically

Calculates total bill & updates DB in background

📈 ***Customer Spend Tracker****
View full table of customers with:

👤 Name

📱 Mobile number

💰 Total amount spent across purchases

 
---

## 🛠️ Tech Stack

| 🔧 Tech         | ⚡ Description             |
|-----------------|----------------------------|
| `Java`          | Backend logic using JDBC + OOP |
| `MySQL/Oracle PL/sql`         | Relational DB for all data |
| `JDBC`          | Java Database Connectivity |
| `Scanner`       | CLI input from the user    |

---

## 📂 Database Overview

Your database is called `shopdb` and consists of the following tables:

- 👨‍💼 `employee`: Stores employee details (ID, name, address, phone)
- 📦 `items`: Stores item info (ID, name, expiry, stock, price)
- 👤 `customers`: Stores customer details (name, phone)
- 🧾 `billing`: Maps customer purchases with total and item breakdown

---

## ⚙️ Installation & Setup

### ✅ Prerequisites:

Before running this project, make sure you have the following installed:

- ☕ **Java JDK (v21 or above)** – for compiling and running the application
- 🐬 **MySQL Server** – for database backend
- 🧠 **Oracle 23ai** *(optional)* – for advanced analytics/integration
- 🐳 **Docker** *(optional)* – to containerize and run your app/db environment
- 💡 **MySQL Connector/J** – JDBC driver to connect Java with MySQL

---

🧠 **Java Concepts & SQL Used**

💡 Concept

OOP (Methods/Classes) --->>	Modular code structure

JDBC API	--->> Database operations

Collection Framework	--->> Billing and summary storage

Conditional Statements --->>	 Stock checks, status flags

Loops	--->> Menu handling, multiple item input

SQL Joins & Aggregations --->> Customer total spend analytics

Exception Handling	--->> Runtime safety

Static & Final --->> Constants and single Scanner instance

## 🌟 Why SmartRetailPro?

⚡ Superfast CLI interface for real-time billing

🔄 Two billing methods for flexibility

🔍 Intelligent stock monitoring

🧾 Clean and detailed bill generation

💰 Tracks lifetime spending per customer

🎯 Resume-friendly and recruiter-attracting

## 📌 Future Scope

GUI with JavaFX or Swing

REST API layer with Spring Boot

Barcode scanning & inventory audit module

Daily sales reports generation (CSV, PDF)
