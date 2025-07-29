-- 🔹 Step 1: Create and select the database
CREATE DATABASE IF NOT EXISTS Dmart;
USE Dmart;

-- 🔹 Step 2: Employee Table (Login via ID + Password)
CREATE TABLE IF NOT EXISTS employee (
    emp_id VARCHAR(20) PRIMARY KEY,            -- Alphanumeric ID
    name VARCHAR(100),
    address VARCHAR(100),
    phone_number VARCHAR(15),
    password VARCHAR(50)                       -- For login authentication
);

-- 🔹 Step 3: Items Table
CREATE TABLE IF NOT EXISTS items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    expiry_date DATE,
    price DECIMAL(10, 2),
    quantity INT,
    -- Remove stock_status from here. Low stock logic handled in Java using quantity.
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 🔹 Step 4: Customers Table
CREATE TABLE IF NOT EXISTS customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    mobile VARCHAR(15),
    visit_count INT DEFAULT 0                  -- Used for loyalty discounts
);

-- 🔹 Step 5: Billing Table
-- Captures multiple items per bill (one row per item bought)
CREATE TABLE IF NOT EXISTS billing (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    item_id INT,
    quantity INT,
    total_cost DECIMAL(10,2),
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (item_id) REFERENCES items(item_id)
);

-- 🔹 Step 6: Daily Revenue Table
-- One row per customer bill with payment mode and auto timestamp
CREATE TABLE IF NOT EXISTS daily_revenue (
    rev_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    total_amount DECIMAL(10,2),
    payment_mode VARCHAR(20),
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    day_of_week VARCHAR(10) GENERATED ALWAYS AS (DAYNAME(timestamp)) STORED,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- 🔹 Step 7: Add Indexes for performance (optional but recommended)
CREATE INDEX idx_customer_id ON billing(customer_id);
CREATE INDEX idx_item_id ON billing(item_id);
CREATE INDEX idx_customer_revenue ON daily_revenue(customer_id);
select * from employee;