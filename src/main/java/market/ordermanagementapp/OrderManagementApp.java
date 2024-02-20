package market.ordermanagementapp;

import Class.InventoryItem;
import Class.Supplier;
import Class.Orders;
import Class.Employee;
import Connection.DBConnection;
import javax.swing.*;
import java.awt.BorderLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import static market.ordermanagementapp.OpenInventoryManagementDialog.retrieveInventoryFromDatabase;


public class OrderManagementApp extends JFrame {

    public OrderManagementApp() {
        setTitle("Order Management App");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton orderButton = new JButton("Order");
        orderButton.addActionListener(e -> OpenOrderManagementDialog.openOrderManagementDialog());

        JButton supplierButton = new JButton("Supplier");
        supplierButton.addActionListener(e -> OpenSupplierManagementDialog.openSupplierManagementDialog());

        JButton inventoryButton = new JButton("Inventory");
        inventoryButton.addActionListener(e -> OpenInventoryManagementDialog.openInventoryManagementDialog());

        JButton employeesButton = new JButton("Employees");
        employeesButton.addActionListener(e -> OpenEmployeesManagementDialog.openEmployeesManagementDialog());

        JButton manageButton = new JButton("Manage");
        manageButton.addActionListener(e -> OpenManageDialog.openManageDialog());

        JButton reportButton = new JButton("Report"); // Create a new button for generating a report
       reportButton.addActionListener(e -> generateReport()); // Add an ActionListener to handle report generation

        JPanel panel = new JPanel();
        panel.add(orderButton);
        panel.add(supplierButton);
        panel.add(inventoryButton);
        panel.add(employeesButton);
        panel.add(manageButton);
        panel.add(reportButton); // Add the new "Report" button to the panel

        add(panel);
    }


    private void generateReport() {
    // Retrieve data from the database
    List<Orders> orders = retrieveOrdersFromDatabase();
    List<Supplier> suppliers = retrieveSuppliersFromDatabase();
    List<InventoryItem> inventory = retrieveInventoryFromDatabase();
    List<Employee> employees = retrieveEmployeesFromDatabase();

    // Create a table model with columns for orders, suppliers, inventory, and employees
    String[] columns = {"Orders", "Suppliers", "Inventory", "Employees"};
    DefaultTableModel model = new DefaultTableModel(null, columns);

    // Add rows to the table model based on the retrieved data
    int rowCount = Math.max(Math.max(orders.size(), suppliers.size()), Math.max(inventory.size(), employees.size()));
    for (int i = 0; i < rowCount; i++) {
        String order = i < orders.size() ? orders.get(i).toString() : "";
        String supplier = i < suppliers.size() ? suppliers.get(i).toString() : "";
        String item = i < inventory.size() ? inventory.get(i).toString() : ""; // Convert InventoryItem to String
        String employee = i < employees.size() ? employees.get(i).toString() : "";
        model.addRow(new Object[]{order, supplier, item, employee});
    }

    // Create a table with the model and display it in a new frame
    JTable table = new JTable(model);
    JScrollPane scrollPane = new JScrollPane(table);

    JFrame reportFrame = new JFrame("Report");
    reportFrame.setSize(800, 600);
    reportFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    reportFrame.add(scrollPane, BorderLayout.CENTER);
    reportFrame.setVisible(true);
}

 public static List<Orders> retrieveOrdersFromDatabase() {
        List<Orders> orders = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM orders";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String orderDetails = resultSet.getString("order_details");
                    String customerName = resultSet.getString("customer_name");
                    String email = resultSet.getString("email");

                    Orders order = new Orders(orderDetails, customerName, email);
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    // Retrieve suppliers from the database
    public static List<Supplier> retrieveSuppliersFromDatabase() {
        List<Supplier> suppliers = new ArrayList<>();

       try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM suppliers";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    String supplierName = resultSet.getString("supplier_name");
                    String companyName = resultSet.getString("company_name");
                    String contact = resultSet.getString("contact");

                    Supplier supplier = new Supplier(supplierName, companyName, contact);
                    suppliers.add(supplier);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suppliers;
    }

    public static List<InventoryItem> retrieveInventoryFromDatabase() {
        List<InventoryItem> inventoryItems = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM inventory";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    String itemName = resultSet.getString("item_name");
                    int quantity = resultSet.getInt("quantity");

                    InventoryItem item = new InventoryItem(itemName, quantity);
                    inventoryItems.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventoryItems;
    }
    
     public static List<Employee> retrieveEmployeesFromDatabase() {
        List<Employee> employees = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM employees";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("employee_name");
                    String email = resultSet.getString("email");
                    String contact = resultSet.getString("contact");

                    Employee employee = new Employee(id, name, email, contact);
                    employees.add(employee);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OrderManagementApp().setVisible(true));
    }
}