import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GenerateReport extends JFrame {

    public GenerateReport() {
        super("Report");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Create tabs for orders, suppliers, inventory, and employees
        tabbedPane.addTab("Orders", createTablePanel(new String[]{"Order"}));
        tabbedPane.addTab("Suppliers", createTablePanel(new String[]{"Supplier"}));
        tabbedPane.addTab("Inventory", createTablePanel(new String[]{"Item", "Quantity"}));
        tabbedPane.addTab("Employees", createTablePanel(new String[]{"Employee Name", "Email", "Contact"}));

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel createTablePanel(String[] columns) {
        JPanel panel = new JPanel(new BorderLayout());

        // Create a table model with specified columns
        DefaultTableModel model = new DefaultTableModel(null, columns);

        // Create a table with the model
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GenerateReport());
    }
}
