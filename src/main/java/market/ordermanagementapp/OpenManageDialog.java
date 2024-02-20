package market.ordermanagementapp;

import Class.Allocation;
import Class.EmailSender;
import Class.Employee;
import Class.Orders;
import Connection.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OpenManageDialog {

    private static List<Employee> employees = new ArrayList<>();
    private static List<Orders> orders = new ArrayList<>();
    private static List<Allocation> allocations = new ArrayList<>();
    private static JTable allocationTable;

    public static void openManageDialog() {
        // Fetch employees and orders from the database
        retrieveEmployeesFromDatabase();
        retrieveOrdersFromDatabase();

        JFrame manageDialogFrame = new JFrame("Allocate Employees to Customer Jobs");
        manageDialogFrame.setSize(600, 400);
        manageDialogFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        // Create components for managing employee allocation
        JComboBox<Employee> employeeComboBox = new JComboBox<>(employees.toArray(new Employee[0]));
        JComboBox<Orders> orderComboBox = new JComboBox<>(orders.toArray(new Orders[0]));
        JButton allocateButton = new JButton("Allocate");
        JButton refreshButton = new JButton("Refresh");

allocateButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        Employee selectedEmployee = (Employee) employeeComboBox.getSelectedItem();
        Orders selectedOrder = (Orders) orderComboBox.getSelectedItem();

        // Assuming you have a method in Orders class to get the order id
        int orderId = selectedOrder.getId();

        Allocation allocation = new Allocation(selectedEmployee.getName(), orderId);
        allocations.add(allocation);
        updateAllocationsList();

        // Save the allocation to the database
        saveAllocationToDatabase(allocation);

        // Send an email to the allocated employee
        EmailSender.sendAllocationEmail(selectedEmployee.getEmail(), selectedOrder.getOrderDetails());

        JOptionPane.showMessageDialog(manageDialogFrame,
                "Allocated employee " + selectedEmployee.getName() + " to order " + selectedOrder.getOrderDetails(),
                "Allocation Successful",
                JOptionPane.INFORMATION_MESSAGE);
    }
});


        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update the list of allocations
                updateAllocationsList();
            }
        });

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Select Employee:"));
        inputPanel.add(employeeComboBox);
        inputPanel.add(new JLabel("Select Order:"));
        inputPanel.add(orderComboBox);
        inputPanel.add(allocateButton);
        inputPanel.add(refreshButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Create a table model for allocations
        String[] allocationColumns = {"Employee Name", "Order ID"};
        DefaultTableModel allocationModel = new DefaultTableModel(null, allocationColumns);

        // Create a table with the model
        allocationTable = new JTable(allocationModel);

        // Set up a scroll pane for the table
        JScrollPane allocationScrollPane = new JScrollPane(allocationTable);

        panel.add(allocationScrollPane, BorderLayout.CENTER);

        manageDialogFrame.add(panel);
        manageDialogFrame.setVisible(true);
    }

    private static void saveAllocationToDatabase(Allocation allocation) {
        String sql = "INSERT INTO allocations (employee_name, order_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, allocation.getEmployeeName());
            pstmt.setInt(2, allocation.getOrderId());
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void updateAllocationsList() {
        // Clear existing rows
        DefaultTableModel allocationModel = (DefaultTableModel) allocationTable.getModel();
        allocationModel.setRowCount(0);

        // Add updated rows
        for (Allocation allocation : allocations) {
            allocationModel.addRow(new Object[]{allocation.getEmployeeName(), allocation.getOrderId()});
        }
    }

    private static void retrieveEmployeesFromDatabase() {
        employees.clear();

        String sql = "SELECT * FROM employees";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("employee_name");
                String email = rs.getString("email");
                String contact = rs.getString("contact");
                employees.add(new Employee(id, name, email, contact));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void retrieveOrdersFromDatabase() {
        orders.clear();

        String sql = "SELECT * FROM orders";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String details = rs.getString("order_details");
                String customerName = rs.getString("customer_name");
                String email = rs.getString("email");
                orders.add(new Orders(details, customerName, email));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> openManageDialog());
    }
}
