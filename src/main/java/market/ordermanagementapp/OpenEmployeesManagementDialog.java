package market.ordermanagementapp;

import Class.Employee;
import Connection.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OpenEmployeesManagementDialog {

    private static List<Employee> employees = new ArrayList<>();
    private static JTable employeeTable;

    public static void openEmployeesManagementDialog() {
        retrieveEmployeesFromDatabase(); // Fetch employees from the database

        JFrame employeesManagementFrame = new JFrame("Manage Employees");
        employeesManagementFrame.setSize(800, 600);
        employeesManagementFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        // Create components for managing employees
        JTextField employeeTextField = new JTextField(20);
        JTextField employeeemailField = new JTextField(20);
        JTextField employeecontactField = new JTextField(20);
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton removeButton = new JButton("Remove");

        // Create a table model with columns
        String[] columns = {"ID", "Name", "Email", "Contact"};
        DefaultTableModel model = new DefaultTableModel(null, columns);

        // Populate the table model with data
        for (Employee employee : employees) {
            model.addRow(new Object[]{employee.getId(), employee.getName(), employee.getEmail(), employee.getContact()});
        }

        // Create JTable with the model
        employeeTable = new JTable(model);

        // Set up a scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(employeeTable);

        addButton.addActionListener(e -> {
            String newEmployeeName = employeeTextField.getText();
            String newEmployeeemail = employeeemailField.getText();
            String newEmployeecontact = employeecontactField.getText();
            Employee newEmployee = saveEmployee(newEmployeeName, newEmployeeemail, newEmployeecontact); // Save to the database and get the created employee
            employees.add(newEmployee);
            updateEmployeeTable();
        });

        // Update button action listener
        updateButton.addActionListener(e -> {
            int selectedRowIndex = employeeTable.getSelectedRow();
            if (selectedRowIndex >= 0) {
                Employee selectedEmployee = employees.get(selectedRowIndex);
                String updatedEmployeeName = employeeTextField.getText();
                String updatedEmployeeemail = employeeemailField.getText();
                String updatedEmployeecontact = employeecontactField.getText();
                updateEmployeeInDatabase(selectedEmployee.getId(), updatedEmployeeName, updatedEmployeeemail, updatedEmployeecontact);

                // Update the existing employee in the list
                selectedEmployee.setName(updatedEmployeeName);
                selectedEmployee.setEmail(updatedEmployeeemail);
                selectedEmployee.setContact(updatedEmployeecontact);

                updateEmployeeTable();
            }
        });
        // Remove button action listener
        removeButton.addActionListener(e -> {
            int selectedRowIndex = employeeTable.getSelectedRow();
            if (selectedRowIndex >= 0) {
                Employee selectedEmployee = employees.get(selectedRowIndex);
                removeEmployeeFromDatabase(selectedEmployee.getId());
                employees.remove(selectedEmployee);
                updateEmployeeTable();
            }
        });

        // Add components to the panel
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(employeeTextField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(employeeemailField);
        inputPanel.add(new JLabel("Contact:"));
        inputPanel.add(employeecontactField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(removeButton);

        panel.add(inputPanel, BorderLayout.SOUTH);

        // Add the panel to the frame
        employeesManagementFrame.add(panel);

        // Set frame visibility
        employeesManagementFrame.setVisible(true);
    }

    private static void updateEmployeeTable() {
        // Clear existing rows
        DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
        model.setRowCount(0);

        // Add updated rows
        for (Employee employee : employees) {
            model.addRow(new Object[]{employee.getId(), employee.getName(), employee.getEmail(), employee.getContact()});
        }
    }

    public static List<Employee> getEmployees() {
        return employees;
    }

    // Fetch employees from the database
    private static void retrieveEmployeesFromDatabase() {
        employees.clear(); // Clear existing employee list

        String sql = "SELECT * FROM employees";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int employeeId = rs.getInt("id");
                String employeeName = rs.getString("employee_name");
                String email = rs.getString("email");
                String contact = rs.getString("contact");
                Employee employee = new Employee(employeeId, employeeName, email, contact);
                employees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

        // Update an employee in the database
        private static void updateEmployeeInDatabase(int id, String updatedEmployeeName, String email, String contact) {
            String sql = "UPDATE employees SET employee_name = ?, email = ?, contact = ? WHERE id = ?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, updatedEmployeeName);
                pstmt.setString(2, email);
                pstmt.setString(3, contact);
                pstmt.setInt(4, id);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Employee updated successfully in the database.");
                } else {
                    System.out.println("Failed to update employee in the database.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    // Remove an employee from the database
    private static void removeEmployeeFromDatabase(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Save employee to the database
    private static Employee saveEmployee(String employeeName, String email, String contact) {
        String sql = "INSERT INTO employees (employee_name, email, contact) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, employeeName);
             pstmt.setString(2, email);
              pstmt.setString(3, contact);
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int employeeId = generatedKeys.getInt(1);
                return new Employee(employeeId, employeeName, email, contact);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
