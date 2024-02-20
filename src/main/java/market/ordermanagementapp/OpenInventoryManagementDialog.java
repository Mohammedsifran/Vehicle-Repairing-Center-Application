package market.ordermanagementapp;

import Class.InventoryItem;
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

public class OpenInventoryManagementDialog {

    private static List<InventoryItem> inventoryItems = new ArrayList<>();
    private static JTable inventoryTable;

    public static void openInventoryManagementDialog() {
        retrieveInventoryFromDatabase(); // Fetch inventory items from the database

        JFrame inventoryManagementFrame = new JFrame("Manage Inventory");
        inventoryManagementFrame.setSize(800, 600);
        inventoryManagementFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        JTextField inventoryTextField = new JTextField(20);
        JTextField quantityTextField = new JTextField(5);
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton removeButton = new JButton("Remove");

        JLabel nameLabel = new JLabel("Name:");
        JLabel quantityLabel = new JLabel("Quantity:");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newItem = inventoryTextField.getText();
                int quantity = Integer.parseInt(quantityTextField.getText());
                InventoryItem inventoryItem = new InventoryItem(newItem, quantity);
                inventoryItems.add(inventoryItem);
                saveInventoryItemToDatabase(inventoryItem);
                updateInventoryTable();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = inventoryTable.getSelectedRow();
                if (selectedIndex >= 0) {
                    String updatedItem = inventoryTextField.getText();
                    int qty = Integer.parseInt(quantityTextField.getText());

                    InventoryItem selectedInventoryItem = inventoryItems.get(selectedIndex);
                    selectedInventoryItem.setName(updatedItem);
                    selectedInventoryItem.setQuantity(qty);

                    updateInventoryItemInDatabase(selectedInventoryItem);
                    updateInventoryTable();
                }
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = inventoryTable.getSelectedRow();
                if (selectedIndex >= 0) {
                    InventoryItem removedItem = inventoryItems.remove(selectedIndex);
                    removeInventoryItemFromDatabase(removedItem);
                    updateInventoryTable();
                }
            }
        });

        // Create a table model with columns
        String[] columns = {"Name", "Quantity"};
        DefaultTableModel model = new DefaultTableModel(null, columns);

        // Populate the table model with data
        for (InventoryItem inventoryItem : inventoryItems) {
            model.addRow(new Object[]{inventoryItem.getName(), inventoryItem.getQuantity()});
        }

        // Create JTable with the model
        inventoryTable = new JTable(model);

        // Set up a scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(inventoryTable);

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(nameLabel);
        inputPanel.add(inventoryTextField);
        inputPanel.add(quantityLabel);
        inputPanel.add(quantityTextField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(removeButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.NORTH);

        inventoryManagementFrame.add(panel);
        inventoryManagementFrame.setVisible(true);
    }

    private static void updateInventoryTable() {
        DefaultTableModel model = (DefaultTableModel) inventoryTable.getModel();
        model.setRowCount(0);

        for (InventoryItem inventoryItem : inventoryItems) {
            model.addRow(new Object[]{inventoryItem.getName(), inventoryItem.getQuantity()});
        }
    }
    
    public static List<InventoryItem> getInventoryItems() {
        return inventoryItems;
    }

    public static void retrieveInventoryFromDatabase() {
        inventoryItems.clear(); // Clear existing inventory item list

        String sql = "SELECT item_name, quantity FROM inventory";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String itemName = rs.getString("item_name");
                int quantity = rs.getInt("quantity");
                InventoryItem inventoryItem = new InventoryItem(itemName, quantity);
                inventoryItems.add(inventoryItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveInventoryItemToDatabase(InventoryItem inventoryItem) {
        String sql = "INSERT INTO inventory (item_name, quantity) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, inventoryItem.getName());
            pstmt.setInt(2, inventoryItem.getQuantity());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateInventoryItemInDatabase(InventoryItem inventoryItem) {
        String sql = "UPDATE inventory SET quantity = ? WHERE item_name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, inventoryItem.getQuantity());
            pstmt.setString(2, inventoryItem.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void removeInventoryItemFromDatabase(InventoryItem inventoryItem) {
        String sql = "DELETE FROM inventory WHERE item_name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, inventoryItem.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
