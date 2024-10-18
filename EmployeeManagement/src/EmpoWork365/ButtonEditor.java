package EmpoWork365;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonEditor extends DefaultCellEditor implements ActionListener {
    private final JButton approveButton = new JButton("Approve");
    private final JButton rejectButton = new JButton("Reject");
    private int leaveId;

    public ButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        approveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rejectButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Set colors for buttons
        approveButton.setBackground(new Color(34, 165, 102));
        approveButton.setForeground(Color.WHITE);
        rejectButton.setBackground(new Color(165, 61, 33));
        rejectButton.setForeground(Color.WHITE);

        // Set button borders
        approveButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rejectButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        approveButton.addActionListener(this);
        rejectButton.addActionListener(this);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        leaveId = (Integer) table.getValueAt(row, 0); 

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); // Adjusted layout
        panel.add(approveButton);
        panel.add(rejectButton);

        // Set a preferred size for the panel
        panel.setPreferredSize(new Dimension(200, 30)); // Adjust height as needed
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return leaveId;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == approveButton) {
            int confirmation = JOptionPane.showConfirmDialog(approveButton, 
                "Are you sure you want to approve this leave request?", 
                "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                ((MainAdmin) SwingUtilities.getWindowAncestor(approveButton)).approveLeave(leaveId);
            }
        } else if (e.getSource() == rejectButton) {
            int confirmation = JOptionPane.showConfirmDialog(rejectButton, 
                "Are you sure you want to reject this leave request?", 
                "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                ((MainAdmin) SwingUtilities.getWindowAncestor(rejectButton)).rejectLeave(leaveId);
            }
        }
        fireEditingStopped(); 
    }
}
