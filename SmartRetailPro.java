import java.sql.*;
import java.util.*;

public class SmartRetailPro {

    static final String DB_URL = "jdbc:mysql://localhost:3306/dmart";
    static final String USER = "root";
    static final String PASS = "Jai@2403"; // replace with your actual password
    static Connection conn;

    static {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Employee login authentication
    public boolean authenticateEmployee(String id, String password) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM employees WHERE id=? AND password=?");
            stmt.setInt(1, Integer.parseInt(id));
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ Add item with auto-incremented ID
    public void addItemWithAutoId(String name, double price, int qty) {
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO items(name, price, quantity) VALUES (?, ?, ?)");
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, qty);
            stmt.executeUpdate();
            System.out.println("✅ Item added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ View all items
    public List<String> viewItemStrings() {
        List<String> items = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM items");
            while (rs.next()) {
                items.add(rs.getInt(1) + " - " + rs.getString(2) + " - ₹" + rs.getDouble(3) + " - Qty: " + rs.getInt(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    // ✅ Fetch just item names
    public List<String> fetchItemNames() {
        List<String> names = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM items");
            while (rs.next()) {
                names.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }

    // ✅ Customer purchases using item name list
    public String customerPurchaseWithList(String name, String phone, List<String> itemNames, String mode) {
        double total = 0;
        StringBuilder bill = new StringBuilder("------ Invoice ------\n");
        try {
            PreparedStatement custStmt = conn.prepareStatement("INSERT INTO customers(name, phone) VALUES (?, ?)");
            custStmt.setString(1, name);
            custStmt.setString(2, phone);
            custStmt.executeUpdate();

            for (String itemName : itemNames) {
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM items WHERE name=?");
                stmt.setString(1, itemName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    double price = rs.getDouble("price");
                    int qty = rs.getInt("quantity");

                    if (qty > 0) {
                        total += price;
                        bill.append(itemName).append(" - ₹").append(price).append("\n");

                        // update stock
                        PreparedStatement updateStmt = conn.prepareStatement("UPDATE items SET quantity = quantity - 1 WHERE name = ?");
                        updateStmt.setString(1, itemName);
                        updateStmt.executeUpdate();

                        // add to sales table
                        PreparedStatement salesStmt = conn.prepareStatement("INSERT INTO sales(item_name) VALUES (?)");
                        salesStmt.setString(1, itemName);
                        salesStmt.executeUpdate();
                    } else {
                        bill.append(itemName).append(" - ❌ Out of Stock\n");
                    }
                } else {
                    bill.append(itemName).append(" - ❌ Item Not Found\n");
                }
            }

            bill.append("---------------------\n");
            bill.append("Total: ₹").append(total).append("\nPayment Mode: ").append(mode).append("\nThank you!\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bill.toString();
    }

    // ✅ Low stock alert
    public String lowStockAlert() {
        StringBuilder alert = new StringBuilder();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM items WHERE quantity <= 10");
            while (rs.next()) {
                alert.append("⚠ ").append(rs.getString("name"))
                     .append(" is low in stock (Qty: ").append(rs.getInt("quantity")).append(")\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alert.length() > 0 ? alert.toString() : "✅ All items are sufficiently stocked.";
    }

    // ✅ Restocking suggestion based on most sold item
    public String restockSuggestion() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT item_name, COUNT(*) as cnt FROM sales GROUP BY item_name ORDER BY cnt DESC LIMIT 1");
            if (rs.next()) {
                return "🔁 Consider restocking: " + rs.getString("item_name") + " (Top selling item)";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "No sales data available for restocking suggestion.";
    }
}
