## SmartRetailPro – “Modern Retail, Classic Simplicity” 🛒📘

# 🛒 SmartRetailPro – The Smart Way to Retail!

Welcome to **SmartRetailPro** – a powerful and intelligent 💡 Java + MySQL or Oracle PL/SQL-based console application that transforms how small businesses handle employees, inventory, customer billing, and stock verification – all in one seamless flow! ⚙️📊

---

## ✨ Highlights

🧑‍💼 **Employee Management** – Add/View store employees and their details  
📦 **Inventory Management** – Add items with name, expiry date, price, quantity, and live stock status (In Stock / Out of Stock)  
🧑‍🛍️ **Customer Billing** – Supports multi-item billing for a single customer in one go  
🧾 **Bill Generation** – Displays itemized bill, totals, and payment mode  
⚠️ **Smart Verification** – Real-time mismatch alerts between items selected and billed  
💰 **Flexible Payment** – Accepts Cash 💵 / Card 💳 / PhonePe 📱 with a Thank You message  

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

## 🌟 Why SmartRetailPro?
🔹 Easy to use

🔹 Full retail flow: stock → sale → bill

🔹 Works offline

🔹 Ready to integrate with future GUI or Web UI

## 📌 Future Scope

GUI with JavaFX or Swing

REST API layer with Spring Boot

Barcode scanning & inventory audit module

Daily sales reports generation (CSV, PDF)
