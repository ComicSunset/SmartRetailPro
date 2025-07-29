import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;

public class Main extends Application {
    SmartRetailPro backend = new SmartRetailPro();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("EngineersMart - SmartRetailPro");

        Label welcome = new Label("Welcome to EngineersMart!!");
        Button customerBtn = new Button("Customer");
        Button employeeBtn = new Button("Employee");

        VBox root = new VBox(15, welcome, customerBtn, employeeBtn);
        root.setPadding(new Insets(20));
        stage.setScene(new Scene(root, 300, 200));
        stage.show();

        customerBtn.setOnAction(e -> showCustomerLogin(stage));
        employeeBtn.setOnAction(e -> showEmployeeLogin(stage));
    }

    private void showCustomerLogin(Stage stage) {
        Label nameLbl = new Label("Name:");
        TextField nameFld = new TextField();
        Label phoneLbl = new Label("Phone:");
        TextField phoneFld = new TextField();
        Button login = new Button("Login");

        VBox vbox = new VBox(10, nameLbl, nameFld, phoneLbl, phoneFld, login);
        vbox.setPadding(new Insets(20));
        stage.setScene(new Scene(vbox, 300, 250));

        login.setOnAction(e -> showCustomerDashboard(stage, nameFld.getText(), phoneFld.getText()));
    }

    private void showCustomerDashboard(Stage stage, String name, String phone) {
        ListView<String> itemList = new ListView<>();
        itemList.getItems().addAll(backend.fetchItemNames());
        itemList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Label modeLbl = new Label("Payment Mode:");
        ComboBox<String> paymentMode = new ComboBox<>();
        paymentMode.getItems().addAll("Cash", "Card", "PhonePe");

        Button purchaseBtn = new Button("Purchase");

        VBox vbox = new VBox(10, new Label("Select Items:"), itemList, modeLbl, paymentMode, purchaseBtn);
        vbox.setPadding(new Insets(20));
        stage.setScene(new Scene(vbox, 350, 400));

        purchaseBtn.setOnAction(e -> {
            List<String> selectedItems = itemList.getSelectionModel().getSelectedItems();
            String mode = paymentMode.getValue();
            String bill = backend.customerPurchaseWithList(name, phone, selectedItems, mode);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, bill, ButtonType.OK);
            alert.setTitle("Invoice");
            alert.setHeaderText("Purchase Complete");
            alert.showAndWait();
        });
    }

    private void showEmployeeLogin(Stage stage) {
        Label idLbl = new Label("Employee ID:");
        TextField idFld = new TextField();
        Label passLbl = new Label("Password:");
        PasswordField passFld = new PasswordField();
        Button login = new Button("Login");

        VBox vbox = new VBox(10, idLbl, idFld, passLbl, passFld, login);
        vbox.setPadding(new Insets(20));
        stage.setScene(new Scene(vbox, 300, 250));

        login.setOnAction(e -> {
            boolean valid = backend.authenticateEmployee(idFld.getText(), passFld.getText());
            if (valid) {
                showEmployeeDashboard(stage);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid credentials", ButtonType.OK);
                alert.showAndWait();
            }
        });
    }

    private void showEmployeeDashboard(Stage stage) {
        Button addItem = new Button("Add Item");
        Button viewItems = new Button("View Items");
        Button viewLowStock = new Button("Low Stock Alert");
        Button restock = new Button("Restock Suggestion");

        VBox vbox = new VBox(10, addItem, viewItems, viewLowStock, restock);
        vbox.setPadding(new Insets(20));
        stage.setScene(new Scene(vbox, 300, 250));

        addItem.setOnAction(e -> showAddItemScreen(stage));
        viewItems.setOnAction(e -> {
            List<String> items = backend.viewItemStrings();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, String.join("\n", items), ButtonType.OK);
            alert.setTitle("Items");
            alert.setHeaderText("Item List");
            alert.showAndWait();
        });

        viewLowStock.setOnAction(e -> {
            String alertMsg = backend.lowStockAlert();
            Alert alert = new Alert(Alert.AlertType.WARNING, alertMsg, ButtonType.OK);
            alert.setTitle("Low Stock Alert");
            alert.setHeaderText("Check Inventory");
            alert.showAndWait();
        });

        restock.setOnAction(e -> {
            String suggestion = backend.restockSuggestion();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, suggestion, ButtonType.OK);
            alert.setTitle("Restock Suggestion");
            alert.setHeaderText("Recommendation");
            alert.showAndWait();
        });
    }

    private void showAddItemScreen(Stage stage) {
        Label nameLbl = new Label("Item Name:");
        TextField nameFld = new TextField();
        Label priceLbl = new Label("Price:");
        TextField priceFld = new TextField();
        Label qtyLbl = new Label("Quantity:");
        TextField qtyFld = new TextField();
        Button addBtn = new Button("Add");

        VBox vbox = new VBox(10, nameLbl, nameFld, priceLbl, priceFld, qtyLbl, qtyFld, addBtn);
        vbox.setPadding(new Insets(20));
        stage.setScene(new Scene(vbox, 300, 300));

        addBtn.setOnAction(e -> {
            String name = nameFld.getText();
            double price = Double.parseDouble(priceFld.getText());
            int qty = Integer.parseInt(qtyFld.getText());
            backend.addItemWithAutoId(name, price, qty);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Item Added Successfully", ButtonType.OK);
            alert.showAndWait();
        });
    }
}
