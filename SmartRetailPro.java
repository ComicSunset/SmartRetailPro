import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;

/**
 * SmartRetailPro - Backend (JDBC) for SmartRetailPro application.
 * - Uses java.sql.Date for expiry_date
 * - All DB operations are prepared statements
 * - Implements customer purchase, billing, daily_revenue, inventory management, reports
 */
public class SmartRetailPro {

    // === CONFIGURE THESE ===
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Dmart"; // change if needed
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Jai@2403"; // change to your password

    // TAX & DISCOUNT POLICIES
    private static final double CGST_RATE = 0.09;   // 9%
    private static final double SGST_RATE = 0.09;   // 9%
    private static final double WEEKDAY_DISCOUNT = 0.05; // 5% weekday baseline
    private static final double WEEKEND_DISCOUNT = 0.10; // 10% weekend
    private static final int LOYALTY_THRESHOLD = 7; // visits needed for loyalty discount
    private static final double LOYALTY_DISCOUNT = 0.10; // 10% loyalty discount

    private Connection conn;

    public SmartRetailPro() throws SQLException {
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    // ========================= Employee Authentication =========================
    public boolean authenticateEmployee(String empId, String password) {
        String sql = "SELECT 1 FROM employee WHERE emp_id = ? AND password = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, empId);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // ========================= Item Management =========================
    public boolean addItem(String name, java.sql.Date expiry, double price, int quantity) {
        String sql = "INSERT INTO items(name, expiry_date, price, quantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            if (expiry != null) ps.setDate(2, expiry);
            else ps.setNull(2, Types.DATE);
            ps.setDouble(3, price);
            ps.setInt(4, quantity);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateItemQuantity(int itemId, int newQty) {
        String sql = "UPDATE items SET quantity = ? WHERE item_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQty);
            ps.setInt(2, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateItemPrice(int itemId, double price) {
        String sql = "UPDATE items SET price = ? WHERE item_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, price);
            ps.setInt(2, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<String> viewItems() {
        List<String> out = new ArrayList<>();
        String sql = "SELECT item_id, name, expiry_date, price, quantity FROM items ORDER BY item_id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("item_id");
                String nm = rs.getString("name");
                java.sql.Date exp = rs.getDate("expiry_date");
                double price = rs.getDouble("price");
                int qty = rs.getInt("quantity");
                out.add(String.format("%d | %s | Exp: %s | ₹%.2f | Qty: %d", id, nm, (exp == null ? "-" : exp.toString()), price, qty));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return out;
    }

    public Map<Integer, String> fetchItemIdNameMap(boolean onlyAvailable) {
        Map<Integer, String> map = new LinkedHashMap<>();
        String sql = "SELECT item_id, name FROM items" + (onlyAvailable ? " WHERE quantity > 0" : "") + " ORDER BY name";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getInt("item_id"), rs.getString("name"));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return map;
    }

    public Optional<Item> getItemById(int itemId) {
        String sql = "SELECT item_id, name, expiry_date, price, quantity FROM items WHERE item_id = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Item(
                            rs.getInt("item_id"),
                            rs.getString("name"),
                            rs.getDate("expiry_date"),
                            rs.getDouble("price"),
                            rs.getInt("quantity")
                    ));
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return Optional.empty();
    }

    // ========================= Customer & Purchase =========================
    // find or create customer by mobile
    public int findOrCreateCustomer(String name, String mobile) throws SQLException {
        String find = "SELECT customer_id FROM customers WHERE mobile = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(find)) {
            ps.setString(1, mobile);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        String insert = "INSERT INTO customers(name, mobile) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, mobile);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Unable to create/find customer");
    }

    public int incrementVisitCount(int customerId) throws SQLException {
        String upd = "UPDATE customers SET visit_count = visit_count + 1 WHERE customer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(upd)) {
            ps.setInt(1, customerId);
            ps.executeUpdate();
        }
        String sel = "SELECT visit_count FROM customers WHERE customer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sel)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Purchase flow:
     * - Accepts a cart: Map<itemId, qty>
     * - Checks inventory, reduces quantity, creates billing rows (one row per item purchase),
     * - Adds a row to daily_revenue summarizing the checkout,
     * - Calculates discounts (weekday/weekend + loyalty) and GST.
     *
     * Returns Invoice object with textual invoice and map of remaining quantities.
     */
    public InvoiceResult checkout(String customerName, String mobile, Map<Integer, Integer> cart, String paymentMode) {
        Map<Integer, Integer> remaining = new LinkedHashMap<>();
        StringBuilder invoice = new StringBuilder();
        double subtotal = 0.0;

        try {
            conn.setAutoCommit(false);

            // 1) find/create customer + increment visits
            int custId = findOrCreateCustomer(customerName, mobile);
            int visits = incrementVisitCount(custId);

            // 2) compute discount rates
            double dailyDisc = isWeekend() ? WEEKEND_DISCOUNT : WEEKDAY_DISCOUNT;
            double loyaltyDisc = (visits >= LOYALTY_THRESHOLD) ? LOYALTY_DISCOUNT : 0.0;
            double combinedDiscountRate = dailyDisc + loyaltyDisc;

            // 3) For each cart item, validate and deduct
            for (Map.Entry<Integer, Integer> e : cart.entrySet()) {
                int itemId = e.getKey();
                int qtyRequested = e.getValue();

                Optional<Item> optItem = getItemById(itemId);
                if (!optItem.isPresent()) {
                    invoice.append(String.format("Item #%d - NOT FOUND\n", itemId));
                    continue;
                }
                Item item = optItem.get();
                if (item.quantity < qtyRequested) {
                    invoice.append(String.format("%s - Requested %d but only %d available -> SKIPPED\n", item.name, qtyRequested, item.quantity));
                    remaining.put(itemId, item.quantity);
                    continue;
                }

                // deduct stock
                try (PreparedStatement upd = conn.prepareStatement("UPDATE items SET quantity = quantity - ? WHERE item_id = ? AND quantity >= ?")) {
                    upd.setInt(1, qtyRequested);
                    upd.setInt(2, itemId);
                    upd.setInt(3, qtyRequested);
                    int changed = upd.executeUpdate();
                    if (changed == 0) {
                        // concurrency conflict
                        invoice.append(String.format("%s - Could not reserve requested qty -> SKIPPED\n", item.name));
                        remaining.put(itemId, getItemById(itemId).map(i -> i.quantity).orElse(0));
                        continue;
                    }
                }

                // insert into billing (one row per item group)
                double lineTotal = item.price * qtyRequested;
                try (PreparedStatement bill = conn.prepareStatement("INSERT INTO billing(customer_id, item_id, quantity, total_cost) VALUES (?, ?, ?, ?)")) {
                    bill.setInt(1, custId);
                    bill.setInt(2, itemId);
                    bill.setInt(3, qtyRequested);
                    bill.setDouble(4, lineTotal);
                    bill.executeUpdate();
                }

                subtotal += lineTotal;
                // remaining qty
                remaining.put(itemId, getItemById(itemId).map(i -> i.quantity).orElse(0));
                invoice.append(String.format("%s x%d  ₹%.2f\n", item.name, qtyRequested, lineTotal));
            }

            // 4) Discounts & taxes
            double discountAmount = subtotal * combinedDiscountRate;
            double afterDiscount = subtotal - discountAmount;
            double cgst = afterDiscount * CGST_RATE;
            double sgst = afterDiscount * SGST_RATE;
            double total = afterDiscount + cgst + sgst;

            // 5) insert daily_revenue
            try (PreparedStatement rev = conn.prepareStatement("INSERT INTO daily_revenue(customer_id, total_amount, payment_mode) VALUES (?, ?, ?)")) {
                rev.setInt(1, custId);
                rev.setDouble(2, total);
                rev.setString(3, paymentMode == null ? "Unknown" : paymentMode);
                rev.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);

            // 6) build final invoice text
            StringBuilder finalInv = new StringBuilder();
            finalInv.append("------ EngineersMart Invoice ------\n");
            finalInv.append("Customer: ").append(customerName).append(" | Mobile: ").append(mobile).append("\n");
            finalInv.append("Items:\n").append(invoice.toString());
            finalInv.append("-----------------------------------\n");
            finalInv.append(String.format("Subtotal: ₹%.2f\n", subtotal));
            finalInv.append(String.format("Discount (%.2f%%): -₹%.2f\n", combinedDiscountRate * 100.0, discountAmount));
            finalInv.append(String.format("CGST (%.2f%%): ₹%.2f\n", CGST_RATE * 100.0, cgst));
            finalInv.append(String.format("SGST (%.2f%%): ₹%.2f\n", SGST_RATE * 100.0, sgst));
            finalInv.append(String.format("TOTAL: ₹%.2f\n", total));
            finalInv.append("Payment Mode: ").append(paymentMode == null ? "Unknown" : paymentMode).append("\n");
            finalInv.append("Thank you for shopping at EngineersMart!\n");

            return new InvoiceResult(finalInv.toString(), total, remaining);

        } catch (SQLException ex) {
            ex.printStackTrace();
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ignored) {}
            return new InvoiceResult("Error processing checkout: " + ex.getMessage(), 0.0, remaining);
        }
    }

    private boolean isWeekend() {
        DayOfWeek d = LocalDateTime.now().getDayOfWeek();
        return d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY;
    }

    // ========================= Reports / Employee Views =========================
    public List<String> viewDailyRevenue() {
        List<String> rows = new ArrayList<>();
        String sql = "SELECT rev_id, customer_id, total_amount, payment_mode, timestamp, day_of_week FROM daily_revenue ORDER BY timestamp DESC LIMIT 200";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(String.format("Rev#%d | Cust:%d | ₹%.2f | %s | %s | %s",
                        rs.getInt("rev_id"),
                        rs.getInt("customer_id"),
                        rs.getDouble("total_amount"),
                        rs.getString("payment_mode"),
                        rs.getTimestamp("timestamp"),
                        rs.getString("day_of_week")));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return rows;
    }

    public List<String> viewCustomers() {
        List<String> rows = new ArrayList<>();
        String sql = "SELECT customer_id, name, mobile, visit_count FROM customers ORDER BY customer_id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(String.format("%d | %s | %s | Visits: %d",
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("mobile"),
                        rs.getInt("visit_count")));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return rows;
    }

    public String lowStockAlert() {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT name, quantity FROM items WHERE quantity <= 10 ORDER BY quantity ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                sb.append(String.format("⚠ %s - Qty: %d\n", rs.getString("name"), rs.getInt("quantity")));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return sb.length() == 0 ? "All items sufficiently stocked." : sb.toString();
    }

    public String restockSuggestion() {
        String sql = "SELECT i.name, SUM(b.quantity) AS sold FROM billing b JOIN items i ON b.item_id = i.item_id GROUP BY i.name ORDER BY sold DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return String.format("Top seller: %s (sold %d) - consider restocking", rs.getString("name"), rs.getInt("sold"));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return "No sales data available.";
    }

    // Close DB
    public void close() {
        try { if (conn != null && !conn.isClosed()) conn.close(); } catch (SQLException ignored) {}
    }

    // ================ Helper Types ================
    public static class Item {
        public final int itemId;
        public final String name;
        public final java.sql.Date expiry;
        public final double price;
        public final int quantity;
        public Item(int itemId, String name, java.sql.Date expiry, double price, int quantity) {
            this.itemId = itemId; this.name = name; this.expiry = expiry; this.price = price; this.quantity = quantity;
        }
    }

    public static class InvoiceResult {
        public final String invoiceText;
        public final double totalAmount;
        public final Map<Integer, Integer> remainingQtyByItemId;
        public InvoiceResult(String invoiceText, double totalAmount, Map<Integer,Integer> rem) {
            this.invoiceText = invoiceText; this.totalAmount = totalAmount; this.remainingQtyByItemId = rem;
        }
    }
}
