package market.ordermanagementapp;

import Class.EmailSender;
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
import Class.Orders;

public class OpenOrderManagementDialog {

    private static List<Orders> orders = new ArrayList<>();
    private static JTable orderTable;

    public static void openOrderManagementDialog() {
        retrieveOrdersFromDatabase(); // Fetch orders from the database

        JFrame orderManagementFrame = new JFrame("Manage Customer Orders");
        orderManagementFrame.setSize(800, 600);
        orderManagementFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        JTextField orderTextField = new JTextField(20);
        JTextField nameTextField = new JTextField(20);
        JTextField emailTextField = new JTextField(20);
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton removeButton = new JButton("Remove");
        JButton vehicleReadyButton = new JButton("Vehicle is Ready");

        // Create a table model with columns
        String[] columns = {"Order", "Name", "Email"};
        DefaultTableModel model = new DefaultTableModel(null, columns);

        // Populate the table model with data
        for (Orders order : orders) {
            model.addRow(new Object[]{order.getOrderDetails(), order.getCustomerName(), order.getEmail()});
        }

        // Create JTable with the model
        orderTable = new JTable(model);

        // Set up a scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(orderTable);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newOrder = orderTextField.getText();
                String customerName = nameTextField.getText();
                String email = emailTextField.getText() + "@gmail.com";
                Orders order = new Orders(newOrder, customerName, email);
                orders.add(order);
                saveOrder(order);
                updateOrderTable();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = orderTable.getSelectedRow();
                if (selectedIndex >= 0) {
                    String updatedOrderName = orderTextField.getText();
                    String updatedName = nameTextField.getText();
                    String updatedContact = emailTextField.getText();
                    Orders updatedOrder = new Orders(updatedOrderName, updatedName, updatedContact);

                    orders.set(selectedIndex, updatedOrder);
                    updateOrderTable();
                }
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = orderTable.getSelectedRow();
                if (selectedIndex >= 0) {
                    orders.remove(selectedIndex);
                    updateOrderTable();
                }
            }
        });

vehicleReadyButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        int selectedIndex = orderTable.getSelectedRow();
        if (selectedIndex >= 0) {
            Orders selectedOrder = orders.get(selectedIndex);
            String selectedEmail = selectedOrder.getEmail();
            if (selectedEmail != null) {
                System.out.println("Selected email: " + selectedEmail);
                updateOrderStatus(selectedOrder);

                // Send email
                EmailSender.sendEmail(selectedEmail, selectedOrder.getOrderDetails());
            } else {
                System.out.println("Email not found for the selected order.");
            }
            updateOrderTable();
        } else {
            System.out.println("No order selected.");
        }
    }
});


        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Order:"));
        inputPanel.add(orderTextField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameTextField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailTextField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(removeButton);
        inputPanel.add(new JLabel()); // Empty label for layout
        inputPanel.add(vehicleReadyButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.NORTH);

        orderManagementFrame.add(panel);
        orderManagementFrame.setVisible(true);
    }

    private static void updateOrderTable() {
        DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
        model.setRowCount(0);

        for (Orders order : orders) {
            model.addRow(new Object[]{order.getOrderDetails(), order.getCustomerName(), order.getEmail()});
        }
    }

    public static List<Orders> getOrders() {
        return orders;
    }

    private static void retrieveOrdersFromDatabase() {
        orders.clear(); // Clear existing order list

        String sql = "SELECT order_details, customer_name, email FROM orders";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String orderDetails = rs.getString("order_details");
                String customerName = rs.getString("customer_name");
                String email = rs.getString("email");
                Orders order = new Orders(orderDetails, customerName, email);
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveOrder(Orders order) {
        String sql = "INSERT INTO orders (order_details, customer_name, email) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, order.getOrderDetails());
            pstmt.setString(2, order.getCustomerName());
            pstmt.setString(3, order.getEmail());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateOrderStatus(Orders order) {
        String sql = "UPDATE orders SET status = 'Vehicle Ready' WHERE order_details = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, order.getOrderDetails());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
