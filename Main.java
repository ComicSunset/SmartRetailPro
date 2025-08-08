import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Main JavaFX UI for SmartRetailPro
 */
public class Main extends Application {

    private SmartRetailPro backend;
    private final DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void init() throws Exception {
        super.init();
        try {
            backend = new SmartRetailPro();
        } catch (SQLException ex) {
            ex.printStackTrace();
            showFatal("Database connection failed: " + ex.getMessage());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("EngineersMart - SmartRetailPro");
        showWelcome(primaryStage);
    }

    private void showWelcome(Stage stage) {
        Label title = new Label("Welcome to EngineersMart!!");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Button customerBtn = new Button("Customer");
        Button employeeBtn = new Button("Employee");

        VBox v = new VBox(15, title, customerBtn, employeeBtn);
        v.setPadding(new Insets(20));
        Scene scene = new Scene(v, 380, 240);
        stage.setScene(scene);
        stage.show();

        customerBtn.setOnAction(e -> showCustomerLogin(stage));
        employeeBtn.setOnAction(e -> showEmployeeLogin(stage));
    }

    // ---------------- Customer Flow ----------------
    private void showCustomerLogin(Stage stage) {
        Label l1 = new Label("Name:");
        TextField nameFld = new TextField();
        Label l2 = new Label("Mobile:");
        TextField mobileFld = new TextField();
        Button otpBtn = new Button("Send OTP (simulate)");
        Button back = new Button("Back");

        VBox v = new VBox(10, l1, nameFld, l2, mobileFld, otpBtn, back);
        v.setPadding(new Insets(16));
        stage.setScene(new Scene(v, 380, 300));

        back.setOnAction(e -> showWelcome(stage));

        otpBtn.setOnAction(e -> {
            String name = nameFld.getText().trim();
            String mobile = mobileFld.getText().trim();
            if (name.isEmpty() || mobile.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please enter name and mobile.");
                return;
            }
            // simulate OTP
            int otp = new Random().nextInt(9000) + 1000;
            // show OTP to user (simulation) and ask to enter it
            TextInputDialog dlg = new TextInputDialog();
            dlg.setTitle("OTP Simulation");
            dlg.setHeaderText("Simulated OTP: " + otp);
            dlg.setContentText("Enter the OTP shown above:");
            Optional<String> res = dlg.showAndWait();
            if (res.isPresent() && res.get().trim().equals(String.valueOf(otp))) {
                showCustomerShop(stage, name, mobile);
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid OTP. Try again.");
            }
        });
    }

    private void showCustomerShop(Stage stage, String customerName, String mobile) {
        // fetch items (id->name map)
        Map<Integer, String> items = backend.fetchItemIdNameMap(true);

        // Left: list with checkboxes and quantity spinners
        VBox left = new VBox(8);
        left.setPadding(new Insets(8));
        left.getChildren().add(new Label("Select items and quantity:"));

        Map<Integer, Spinner<Integer>> qtySpinners = new LinkedHashMap<>();
        for (Map.Entry<Integer, String> e : items.entrySet()) {
            int id = e.getKey();
            String nm = e.getValue();
            HBox row = new HBox(8);
            CheckBox cb = new CheckBox(nm + " (ID:" + id + ")");
            Spinner<Integer> sp = new Spinner<>(1, 100, 1);
            sp.setEditable(true);
            sp.setDisable(true);
            cb.selectedProperty().addListener((obs, oldV, newV) -> sp.setDisable(!newV));
            row.getChildren().addAll(cb, sp);
            left.getChildren().add(row);
            qtySpinners.put(id, sp);
        }

        ComboBox<String> paymentMode = new ComboBox<>();
        paymentMode.getItems().addAll("Cash", "Card", "PhonePe");
        paymentMode.setValue("Cash");

        Button checkout = new Button("Checkout");
        Button back = new Button("Back");

        HBox bottom = new HBox(10, new Label("Payment:"), paymentMode, checkout, back);
        bottom.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setLeft(new ScrollPane(left));
        root.setBottom(bottom);

        Scene scene = new Scene(root, 720, 480);
        stage.setScene(scene);

        back.setOnAction(e -> showWelcome(stage));

        checkout.setOnAction(e -> {
            // build cart map
            Map<Integer, Integer> cart = new LinkedHashMap<>();
            for (Map.Entry<Integer, Spinner<Integer>> entry : qtySpinners.entrySet()) {
                Spinner<Integer> sp = entry.getValue();
                // find corresponding checkbox state by inspecting parent row
                HBox row = (HBox) sp.getParent();
                CheckBox cb = (CheckBox) row.getChildren().get(0);
                if (cb.isSelected()) cart.put(entry.getKey(), sp.getValue());
            }

            if (cart.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please select at least one item.");
                return;
            }

            SmartRetailPro.InvoiceResult res = backend.checkout(customerName, mobile, cart, paymentMode.getValue());

            // show invoice text area
            TextArea area = new TextArea(res.invoiceText + "\nRemaining stock (by item id):\n");
            res.remainingQtyByItemId.forEach((k,v) -> area.appendText("ID:" + k + " -> " + v + "\n"));
            area.setEditable(false);
            area.setWrapText(true);

            Button ok = new Button("OK");
            VBox vb = new VBox(8, new Label("Invoice"), area, ok);
            vb.setPadding(new Insets(8));
            Stage inv = new Stage();
            inv.setScene(new Scene(vb, 600, 480));
            inv.setTitle("Invoice");
            inv.show();

            ok.setOnAction(ev -> {
                inv.close();
                // refresh shop screen: reload items
                showCustomerShop(stage, customerName, mobile);
            });
        });
    }

    // ---------------- Employee Flow ----------------
    private void showEmployeeLogin(Stage stage) {
        Label l1 = new Label("Employee ID:");
        TextField idFld = new TextField();
        Label l2 = new Label("Password:");
        PasswordField passFld = new PasswordField();

        Button loginBtn = new Button("Login");
        Button faceBtn = new Button("Face Login (optional)");
        Button back = new Button("Back");

        VBox v = new VBox(10, l1, idFld, l2, passFld, new HBox(8, loginBtn, faceBtn), back);
        v.setPadding(new Insets(16));
        stage.setScene(new Scene(v, 380, 300));

        back.setOnAction(e -> showWelcome(stage));

        loginBtn.setOnAction(e -> {
            boolean ok = backend.authenticateEmployee(idFld.getText().trim(), passFld.getText().trim());
            if (ok) showEmployeeDashboard(stage);
            else showAlert(Alert.AlertType.ERROR, "Invalid credentials");
        });

        faceBtn.setOnAction(e -> {
            // Attempt face login (stub) -> implement OpenCV here
            boolean ok = attemptFaceLogin();
            if (ok) showEmployeeDashboard(stage);
            else showAlert(Alert.AlertType.ERROR, "Face recognition failed (or not configured).");
        });
    }

    // NOTE: This is a stub. Integrate OpenCV code here to open webcam and match faces.
    private boolean attemptFaceLogin() {
        // If OpenCV configured, call face recognition routine.
        // For now return false to force password login, or true for testing.
        return false;
    }

    private void showEmployeeDashboard(Stage stage) {
        Button addItemBtn = new Button("Add Item");
        Button editItemBtn = new Button("Edit Item (price/qty)");
        Button viewItemsBtn = new Button("View Items");
        Button lowStockBtn = new Button("Low Stock Alert");
        Button restockBtn = new Button("Restock Suggestion");
        Button viewRevenueBtn = new Button("View Revenue");
        Button viewCustomersBtn = new Button("View Customers");
        Button logout = new Button("Logout");

        VBox v = new VBox(10, addItemBtn, editItemBtn, viewItemsBtn, lowStockBtn, restockBtn, viewRevenueBtn, viewCustomersBtn, logout);
        v.setPadding(new Insets(16));
        stage.setScene(new Scene(v, 400, 420));

        logout.setOnAction(e -> showWelcome(stage));

        addItemBtn.setOnAction(e -> showAddItem(stage));
        editItemBtn.setOnAction(e -> showEditItem(stage));
        viewItemsBtn.setOnAction(e -> {
            List<String> rows = backend.viewItems();
            showLargeText("Items", String.join("\n", rows));
        });
        lowStockBtn.setOnAction(e -> showLargeText("Low Stock", backend.lowStockAlert()));
        restockBtn.setOnAction(e -> showLargeText("Restock Suggestion", backend.restockSuggestion()));
        viewRevenueBtn.setOnAction(e -> {
            List<String> rows = backend.viewDailyRevenue();
            showLargeText("Daily Revenue", String.join("\n", rows));
        });
        viewCustomersBtn.setOnAction(e -> {
            List<String> rows = backend.viewCustomers();
            showLargeText("Customers", String.join("\n", rows));
        });
    }

    private void showAddItem(Stage stage) {
        Label nameL = new Label("Name:");
        TextField nameFld = new TextField();
        Label priceL = new Label("Price:");
        TextField priceFld = new TextField();
        Label qtyL = new Label("Quantity:");
        TextField qtyFld = new TextField();
        Label expL = new Label("Expiry (YYYY-MM-DD) - optional:");
        TextField expFld = new TextField();

        Button add = new Button("Add");
        Button back = new Button("Back");

        VBox v = new VBox(8, nameL, nameFld, priceL, priceFld, qtyL, qtyFld, expL, expFld, new HBox(8, add, back));
        v.setPadding(new Insets(12));
        stage.setScene(new Scene(v, 420, 420));

        back.setOnAction(e -> showEmployeeDashboard(stage));

        add.setOnAction(e -> {
            try {
                String name = nameFld.getText().trim();
                double price = Double.parseDouble(priceFld.getText().trim());
                int qty = Integer.parseInt(qtyFld.getText().trim());
                java.sql.Date expiry = null;
                String expStr = expFld.getText().trim();
                if (!expStr.isEmpty()) expiry = java.sql.Date.valueOf(expStr);
                boolean ok = backend.addItem(name, expiry, price, qty);
                showAlert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, ok ? "Item added." : "Failed to add.");
                if (ok) {
                    // clear fields
                    nameFld.clear(); priceFld.clear(); qtyFld.clear(); expFld.clear();
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid input: " + ex.getMessage());
            }
        });
    }

    private void showEditItem(Stage stage) {
        Label idL = new Label("Item ID:");
        TextField idFld = new TextField();
        Label priceL = new Label("New Price (leave blank to skip):");
        TextField priceFld = new TextField();
        Label qtyL = new Label("New Quantity (leave blank to skip):");
        TextField qtyFld = new TextField();

        Button apply = new Button("Apply");
        Button back = new Button("Back");

        VBox v = new VBox(8, idL, idFld, priceL, priceFld, qtyL, qtyFld, new HBox(8, apply, back));
        v.setPadding(new Insets(12));
        stage.setScene(new Scene(v, 420, 320));

        back.setOnAction(e -> showEmployeeDashboard(stage));

        apply.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idFld.getText().trim());
                boolean changed = false;
                if (!priceFld.getText().trim().isEmpty()) {
                    double p = Double.parseDouble(priceFld.getText().trim());
                    changed |= backend.updateItemPrice(id, p);
                }
                if (!qtyFld.getText().trim().isEmpty()) {
                    int q = Integer.parseInt(qtyFld.getText().trim());
                    changed |= backend.updateItemQuantity(id, q);
                }
                showAlert(changed ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING, changed ? "Updated" : "No changes or update failed");
            } catch (NumberFormatException nfe) {
                showAlert(Alert.AlertType.ERROR, "Invalid number input");
            }
        });
    }

    // Utility: show long text in modal
    private void showLargeText(String title, String body) {
        TextArea area = new TextArea(body);
        area.setEditable(false);
        area.setWrapText(true);
        Button ok = new Button("OK");
        VBox v = new VBox(8, new Label(title), area, ok);
        v.setPadding(new Insets(10));
        Stage st = new Stage();
        st.setScene(new Scene(v, 700, 500));
        st.setTitle(title);
        st.show();
        ok.setOnAction(e -> st.close());
    }

    // Utility Alerts
    private void showAlert(Alert.AlertType type, String txt) {
        Alert a = new Alert(type, txt, ButtonType.OK);
        a.showAndWait();
    }

    private void showFatal(String message) {
        System.err.println(message);
        Alert a = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        a.showAndWait();
        System.exit(1);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (backend != null) backend.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
