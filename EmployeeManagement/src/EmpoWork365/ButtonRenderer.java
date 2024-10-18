package EmpoWork365;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ButtonRenderer extends JPanel implements TableCellRenderer {

    private final JButton approveButton = new JButton("Approve");
    private final JButton rejectButton = new JButton("Reject");

    public ButtonRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); 
        
        approveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rejectButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        approveButton.setOpaque(true);
        rejectButton.setOpaque(true);

        // Set colors for the buttons
        approveButton.setBackground(new Color(34, 165, 102));
        rejectButton.setBackground(new Color(165, 61, 33));

        approveButton.setForeground(Color.WHITE);
        rejectButton.setForeground(Color.WHITE);

        approveButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rejectButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        add(approveButton);
        add(rejectButton);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        return this; // Return the panel containing both buttons
    }
}


