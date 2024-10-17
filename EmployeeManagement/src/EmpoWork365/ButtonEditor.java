package EmpoWork365;


import java.awt.Component;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.event.ActionEvent;
import java.util.EventObject;

public class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
    private final JButton button;
    private String label;
    private final JTable table;

    public ButtonEditor(JButton button, JTable table) {
        this.button = button;
        this.table = table;

        // Action when the button is clicked
        button.addActionListener((ActionEvent e) -> {
            fireEditingStopped(); // Stop editing and notify listeners
            int row = table.getSelectedRow();
            if (row >= 0) { // Check if a row is selected
                int applicationId = (int) table.getValueAt(row, 0); // Assuming application ID is in the first column
                handleLeaveRequest(applicationId, label);
            }
        });
    }

    @Override
    public Object getCellEditorValue() {
        return label; // Return the button label
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row) {
        label = (value == null) ? "Approve" : value.toString();
        button.setText(label);
        return button; // Return the button as the editor component
    }

    private void handleLeaveRequest(int applicationId, String action) {
        // Implement your logic to approve/reject leave requests based on applicationId
        // You may want to update the database here
        // For now, we'll just show a message dialog
        JOptionPane.showMessageDialog(table, action + " request for application ID: " + applicationId);
        // You might call a method to update the database here
    }

    // Override this method to prevent the default behavior of cell editing
    @Override
    public boolean isCellEditable(EventObject e) {
        return true;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
