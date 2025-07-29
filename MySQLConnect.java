import java.sql.*;
import java.util.Scanner;

public class MySQLConnect {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Database connection info
        String jdbcURL = "jdbc:mysql://localhost:3306/Employee_details";
        String username = "root";
        String password = "Jai@2403";

        try {
            // Load JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Connect to database
            Connection conn = DriverManager.getConnection(jdbcURL, username, password);

            int choice;
            do {
                System.out.println("\n==== Employee Menu ====");
                System.out.println("1. Enter new employee");
                System.out.println("2. View all employees");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        // Insert new employee
                        System.out.print("Enter Employee ID: ");
                        int empId = sc.nextInt();
                        sc.nextLine(); // consume newline

                        System.out.print("Enter Name: ");
                        String name = sc.nextLine();

                        System.out.print("Enter Address: ");
                        String address = sc.nextLine();

                        System.out.print("Enter Phone Number: ");
                        String phone = sc.nextLine();

                        String insertSQL = "INSERT INTO employee (emp_id, name, address, phone_number) VALUES (?, ?, ?, ?)";
                        PreparedStatement pstmt = conn.prepareStatement(insertSQL);
                        pstmt.setInt(1, empId);
                        pstmt.setString(2, name);
                        pstmt.setString(3, address);
                        pstmt.setString(4, phone);

                        int rows = pstmt.executeUpdate();
                        System.out.println(rows + " row(s) inserted successfully!");
                        pstmt.close();
                        break;

                    case 2:
                        // View all employees
                        String selectSQL = "SELECT * FROM employee";
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(selectSQL);

                        System.out.println("\nEmployee Records:");
                        System.out.println("ID\tName\t\tAddress\t\tPhone Number");
                        while (rs.next()) {
                            System.out.println(rs.getInt("emp_id") + "\t" +
                                               rs.getString("name") + "\t\t" +
                                               rs.getString("address") + "\t\t" +
                                               rs.getString("phone_number"));
                        }
                        rs.close();
                        stmt.close();
                        break;

                    case 3:
                        System.out.println("Exiting program. Goodbye!");
                        break;

                    default:
                        System.out.println("Invalid choice. Try again!");
                }

            } while (choice != 3);

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
