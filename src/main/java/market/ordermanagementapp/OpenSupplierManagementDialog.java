package market.ordermanagementapp;

import Class.Supplier;
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

public class OpenSupplierManagementDialog {

    private static List<Supplier> suppliers = new ArrayList<>();
    private static JTable supplierTable;

    public static void openSupplierManagementDialog() {
        retrieveSuppliersFromDatabase(); // Fetch suppliers from the database

        JFrame supplierManagementFrame = new JFrame("Manage Suppliers");
        supplierManagementFrame.setSize(800, 600);
        supplierManagementFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        // Create components for managing suppliers
        JTextField supplierNameTextField = new JTextField(20);
        JTextField companyNameTextField = new JTextField(20);
        JTextField contactTextField = new JTextField(20);
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton removeButton = new JButton("Remove");

        JLabel nameLabel = new JLabel("Name:");
        JLabel companyLabel = new JLabel("Company:");
        JLabel contactLabel = new JLabel("Contact:");
        
                // Create a table model with columns
        String[] columns = {"Name", "Company", "Contact"};
        DefaultTableModel model = new DefaultTableModel(null, columns);

        // Populate the table model with data
        for (Supplier supplier : suppliers) {
            model.addRow(new Object[]{supplier.getId(), supplier.getSupplierName(), supplier.getCompanyName(), supplier.getContact()});
        }
        
        supplierTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(supplierTable);


        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newSupplierName = supplierNameTextField.getText();
                String newCompanyName = companyNameTextField.getText();
                String newContact = contactTextField.getText();
                Supplier newSupplier = new Supplier(newSupplierName, newCompanyName, newContact);
                insertSupplierIntoDatabase(newSupplier);
                suppliers.add(newSupplier);
               
                updateSupplierTable();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = supplierTable.getSelectedRow();
                if (selectedIndex >= 0) {
                    Supplier selectedsupplier = suppliers.get(selectedIndex);
                    String updatedSupplierName = supplierNameTextField.getText();
                    String updatedCompanyName = companyNameTextField.getText();
                    String updatedContact = contactTextField.getText();
                    Supplier updatedSupplier = new Supplier(updatedSupplierName, updatedCompanyName, updatedContact);

                    suppliers.set(selectedIndex, updatedSupplier);
                    updateSupplierInDatabase(updatedSupplier);
                    updateSupplierTable();
                }
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = supplierTable.getSelectedRow();
                if (selectedIndex >= 0) {
                    Supplier removedSupplier = suppliers.remove(selectedIndex);
                    removeSupplierFromDatabase(removedSupplier);
                    updateSupplierTable();
                }
            }
        });


        // Add components to the panel
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(nameLabel);
        inputPanel.add(supplierNameTextField);
        inputPanel.add(companyLabel);
        inputPanel.add(companyNameTextField);
        inputPanel.add(contactLabel);
        inputPanel.add(contactTextField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(removeButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        supplierManagementFrame.add(panel);
        supplierManagementFrame.setVisible(true);
    }
    
        private static void updateSupplierTable() {
        // Clear existing rows
        DefaultTableModel model = (DefaultTableModel) supplierTable.getModel();
        model.setRowCount(0);

        for (Supplier supplier : suppliers) {
            model.addRow(new Object[]{supplier.getSupplierName(), supplier.getCompanyName(), supplier.getContact()});
        }
    }

    // Fetch suppliers from the database
    private static void retrieveSuppliersFromDatabase() {
        suppliers.clear(); // Clear existing supplier list

        String sql = "SELECT supplier_name, company_name, contact FROM suppliers";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String supplierName = rs.getString("supplier_name");
                String companyName = rs.getString("company_name");
                String contact = rs.getString("contact");
                Supplier supplier = new Supplier(supplierName, companyName, contact);
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insert a new supplier into the database
    private static void insertSupplierIntoDatabase(Supplier supplier) {
        String sql = "INSERT INTO suppliers (supplier_name, company_name, contact) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, supplier.getSupplierName());
            pstmt.setString(2, supplier.getCompanyName());
            pstmt.setString(3, supplier.getContact());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update a supplier in the database
    private static void updateSupplierInDatabase(Supplier supplier) {
        String sql = "UPDATE suppliers SET company_name = ?, contact = ? WHERE supplier_name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, supplier.getCompanyName());
            pstmt.setString(2, supplier.getContact());
            pstmt.setString(3, supplier.getSupplierName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Remove a supplier from the database
    private static void removeSupplierFromDatabase(Supplier supplier) {
        String sql = "DELETE FROM suppliers WHERE supplier_name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, supplier.getSupplierName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get the list of suppliers
    public static List<Supplier> getSuppliers() {
        return suppliers;
    }
}
