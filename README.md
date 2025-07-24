## SmartRetailPro

# 🛒 SmartRetailPro – The Smart Way to Retail!

Welcome to **SmartRetailPro** – a powerful and intelligent 💡 Java + MySQL-based console application that transforms how small businesses handle employees, inventory, customer billing, and stock verification – all in one seamless flow! ⚙️📊

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
| `MySQL`         | Relational DB for all data |
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
- Java (JDK 8+)
- MySQL installed and running
- `mysql-connector-java-<version>.jar` in your classpath

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
