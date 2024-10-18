/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EmpoWork365;

/**
 *
 * @author jenal
 */

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {

    public MultiLineCellRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
        setMargin(new Insets(15, 20, 15, 20)); 
    }

@Override
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    setText(value != null ? value.toString() : "");

    int columnWidth = table.getColumnModel().getColumn(column).getWidth();
    setSize(columnWidth, Short.MAX_VALUE);

    int preferredHeight = getPreferredSize().height;

    if (table.getRowHeight(row) != preferredHeight) {
        table.setRowHeight(row, preferredHeight);
    }

    if (isSelected) {
        setBackground(table.getSelectionBackground());
        setForeground(table.getSelectionForeground());
    } else {
        setBackground(table.getBackground());
        setForeground(table.getForeground());
    }

    return this;
}

}
