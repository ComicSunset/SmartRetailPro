## SmartRetailPro – “Modern Retail, Classic Simplicity” 🛒📘

# 🛒 SmartRetailPro – The Smart Way to Retail!

Welcome to **SmartRetailPro** – a powerful and intelligent 💡 Java + MySQL or Oracle PL/SQL-based console application that transforms how small businesses handle employees, inventory, customer billing, and stock verification – all in one seamless flow! ⚙️📊

## ✨ Project Highlights

| Feature                          | Description                                                                 |
|----------------------------------|-----------------------------------------------------------------------------|
| JavaFX GUI                       | Intuitive interface for both customer and employee workflows                |
| Customer Portal                  | Secure OTP-based login, shopping cart, dynamic discounts, and GST billing   |
| Employee Portal                  | Face or password login, item inventory control, customer/revenue tracking   |
| Loyalty & Daily Discounts        | Smart discount engine based on visit frequency and day of the week          |
| GST + Stock Automation           | Full billing with GST, low stock alerts, restocking suggestion              |
| Java + JDBC + MySQL Integration  | Clean backend logic, stable SQL operations, modular MVC structure           |

---

## 🧰 Tech Stack

| Layer            | Tools Used                            |
|------------------|----------------------------------------|
| Frontend (GUI)   | JavaFX                                 |
| Backend (Logic)  | Java, JDBC                             |
| Database         | MySQL                                  |
| Facial Login     | OpenCV (JavaCV bindings)               |
| OTP Simulation   | Java (random + timer simulation)       |
| Charts/Reports   | JavaFX Charts (for customer spend)     |

---

## 🎯 User Roles & Capabilities

### 🧑‍💻 **Customer Features**
- Login via **Name + Phone + OTP simulation**
- Browse all items in inventory
- Add multiple items to **Cart**
- Real-time **quantity updates**
- Automatic **GST breakup (CGST + SGST)**
- **Loyalty Discount** if visits ≥ 7 times (10% off)
- **Daily Discounts** (extra 5% off on weekends)
- **Bill Summary + Payment Mode** (Cash, Card, UPI)
- Final **Thank You** message with detailed bill

---

### 👨‍💼 **Employee Features**
- Login via:
  - ✅ Employee ID + Password (MySQL verified)
  - ✅ **Face Recognition** via OpenCV
- Add items (with Name, Price, Quantity, Expiry)
- View complete inventory list
- Get **Low Stock Alerts** (when qty ≤ 10)
- View **Most Sold Item Suggestions** (for restock)
- Access **Customer Spend Summary Table**
- View **Daily Revenue** (with timestamp and weekday)

---

## 🗃️ Database Schema (`shopdb`)

> Make sure to create the following tables in MySQL before running the application.

### Tables:

#### 1. `employee`
| Column     | Type        |
|------------|-------------|
| id         | INT (PK)    |
| name       | VARCHAR     |
| address    | VARCHAR     |
| phone      | VARCHAR     |
| password   | VARCHAR     |
| face_id    | VARCHAR     |

#### 2. `items`
| Column     | Type        |
|------------|-------------|
| id         | INT (PK)    |
| name       | VARCHAR     |
| expiry_date| DATE        |
| price      | DOUBLE      |
| quantity   | INT         |
| status     | VARCHAR     |

#### 3. `customers`
| Column     | Type        |
|------------|-------------|
| id         | INT (PK)    |
| name       | VARCHAR     |
| phone      | VARCHAR     |
| visit_count| INT         |
| total_spent| DOUBLE      |

#### 4. `billing`
| Column     | Type        |
|------------|-------------|
| bill_id    | INT (PK)    |
| customer_id| INT (FK)    |
| item_id    | INT (FK)    |
| qty        | INT         |
| subtotal   | DOUBLE      |
| date       | TIMESTAMP   |

#### 5. `daily_revenue`
| Column     | Type        |
|------------|-------------|
| id         | INT (PK)    |
| amount     | DOUBLE      |
| timestamp  | TIMESTAMP   |
| day_of_week| VARCHAR     |

---

## 🔧 Installation Instructions

### 🖥️ Prerequisites
- ✅ JDK 21 or higher
- ✅ MySQL Server running (`localhost:3306`)
- ✅ JavaFX SDK installed (e.g., `javafx-sdk-24.0.2`)
- ✅ MySQL Connector/J `.jar` file (e.g., `mysql-connector-j-9.4.0.jar`)
- ✅ OpenCV + JavaCV installed for face login

### 📁 File Structure
📁 SmartRetailPro/
├── SmartRetailPro.java (Backend Java logic)
├── Main.java (JavaFX GUI + logic)
├── face_data/ (Captured face images)
├── sql/
│ └── create_tables.sql (SQL schema for MySQL)
├── README.md


---

## ⚙️ Compilation & Run Guide (Windows CMD)

### 🔨 Step 1: Set Paths

set PATH_TO_FX="D:\JAVA STUFF\javafx-sdk-24.0.2\lib"
set MYSQL_JAR="D:\JAVA STUFF\mysql-connector-j-9.4.0.jar"
📦 Step 2: Compile Code
javac --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -cp ".;%MYSQL_JAR%" SmartRetailPro.java Main.java

▶️ Step 3: Run Application

java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -cp ".;%MYSQL_JAR%" Main

# 📸 Face Recognition (Employee Login)

Store employee images in face_data/

Employee's face will be matched with trained data

OpenCV (via webcam) triggers facial login

💡 Uses EigenFaceRecognizer from OpenCV with real-time frame processing

## 📊 Smart Features Recap

Feature	                                  Trigger	                                Impact

Loyalty Discount	                     visit_count ≥ 7	                        10% extra discount

Weekend Discount	                 day_of_week = Saturday/Sunday	             5% daily discount

Low Stock Alert	                     item.quantity ≤ 10	                   Alert shown on employee dashboard

Restocking Suggestion             Most sold item auto-detected	         Employee prompted to reorder

Revenue Logging	               Every bill generates revenue record            For daily revenue tracking

OTP Login (Customer)	         Simulated using random 4-digit code	           Ensures basic login security


## 📈 Sample Output
💬 "Welcome to EngineersMart!!"

➡️ Role: Customer or Employee

💻 If Employee:

Face or ID+Password login

Dashboard: Add/View Items, Alerts, Spend Table

🛍️ If Customer:

OTP login

Cart interface

Final bill: Loyalty + Daily + GST breakdown

Mode of payment + Thank you note

## 📌 Future Enhancements
Cloud-based MySQL deployment (AWS RDS)

Email/SMS billing receipts

Barcode scanning for item entry

Stock reorder API integration

Customer profiles with purchase history

## 🌟 Why SmartRetailPro?

✅ Real Problems, Real Solutions:

**Retail Problem**	                                            **How SmartRetailPro Solves It**

Repeated customer details every visit	              Remembers customers and tracks visits & spending
Stockouts or overstocking	                        Auto alerts when stock is low + most sold item restocking
Manual, inconsistent billing	                      GST breakdown, dynamic discounts, loyalty rewards
Employee access control	                           Secure login via ID/password + optional face recognition
Poor customer retention	                                Loyalty program with visit-based discounts
Weekend offers & promotional pricing	              Dynamic discounts on weekends integrated into system
No daily sales insight	                            Daily revenue with timestamp and weekday tracking
Error-prone inventory updates	                         GUI-based item add/edit with expiry tracking

# 💡 Key Design Principles

**Modular**: Clean separation between UI (JavaFX), business logic (Java), and storage (MySQL)

**Realistic UX**: Mimics real billing counters with step-by-step purchase and payment flow

**Secure**: Employee login is password-verified; optionally uses facial recognition

**Expandable**: Easy to plug in new features like barcode scanning or online orders

**Visually Friendly**: Built using JavaFX for modern, clean GUI experience

**Data-Driven**: All key events (purchases, revenue, stock alerts) are database-logged

# 🎯 Who is it for?

User Type	What They Can Do

👨‍💼 Store Employees	Add/view items, manage inventory, track customers & revenue

🧑‍💻 Customers	View items, shop easily, get discounts, see final bill

🧪 Developers	Learn from a full-stack JavaFX + JDBC + MySQL application

💼 Business Owners	Use it as a POS prototype for small-to-medium stores

# 🏆 What Makes It Stand Out?

🧠 Smart discounts based on day and loyalty

🤳 Face recognition (OpenCV) login option

🛒 Integrated cart with item breakdown and total

💾 MySQL backend with complete logging and analytics

🔄 Modular Java code, great for academic or personal projects

📱 OTP simulation for customer login

📊 Revenue and customer spend dashboards

