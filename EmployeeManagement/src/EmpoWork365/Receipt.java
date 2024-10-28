/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package EmpoWork365;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author jenal
 */
public class Receipt extends javax.swing.JFrame {

    /**
     * Creates new form Receipt
     */
    public Receipt() {
        setUndecorated(true);
        setResizable(false);
        TitleBar titleBar = new TitleBar(this);

        titleBar.setPreferredSize(new Dimension(520, 83)); // Adjust height as needed

        // Add title bar and content panel to the frame
        setLayout(new BorderLayout());
        add(titleBar, BorderLayout.NORTH);

        setSize(520, 866);
        setLocationRelativeTo(null);
        
        initComponents();
        ImageIcon icon = IconLoader.getIcon();
        Image img = icon.getImage();
        
        setIconImage(img);
        
        receiptTextArea1.setEditable(false); 
    }
    
    public void setPayrollDetails(Map<String, String> payrollDetails) {
        StringBuilder receipt = new StringBuilder();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

        receipt.append("----------------------- Employee Details -----------------------\n");
        receipt.append("Employee ID: ").append(payrollDetails.getOrDefault("Employee ID", "")).append("\n");
        receipt.append("Employee Name: ").append(payrollDetails.getOrDefault("Full Name", "")).append("\n");
        receipt.append("Email: ").append(payrollDetails.getOrDefault("Email", "")).append("\n");
        receipt.append("Job Title: ").append(payrollDetails.getOrDefault("Job Title", "")).append("\n");
        receipt.append("Department: ").append(payrollDetails.getOrDefault("Department", "")).append("\n");

        receipt.append("\n---------------------------- MONTH ----------------------------\n");
        receipt.append("                            ").append(payrollDetails.getOrDefault("Month", "").toUpperCase()).append("\n");

        
        int hoursPerMonth = parseInt(payrollDetails.get("Regular Hours/Month"));
        int leaveBalance = parseInt(payrollDetails.get("Leave Balance"));
        BigDecimal totalSalary = parseBigDecimal(payrollDetails.get("Total Salary"));
        BigDecimal ratePerHour = parseBigDecimal(payrollDetails.get("Rate per Hour"));
        BigDecimal totalHoursWorked = parseBigDecimal(payrollDetails.get("Total Hours Worked"));
        BigDecimal overtimeHours = parseBigDecimal(payrollDetails.get("Overtime Hours"));
        BigDecimal philHealth = parseBigDecimal(payrollDetails.get("PhilHealth Deduction"));
        BigDecimal sss = parseBigDecimal(payrollDetails.get("SSS Deduction"));
        BigDecimal pagibig = parseBigDecimal(payrollDetails.get("Pag-IBIG Deduction"));
        BigDecimal incomeTax = parseBigDecimal(payrollDetails.get("Income Tax"));
        BigDecimal unpaidLeaveCost = parseBigDecimal(payrollDetails.get("Unpaid Leave Cost"));
        BigDecimal netSalary = parseBigDecimal(payrollDetails.get("Net Salary"));
        BigDecimal totalDeductions = parseBigDecimal(payrollDetails.get("Total Deductions"));
        BigDecimal unusedLeave = parseBigDecimal(payrollDetails.get("Unused Leave"));
        BigDecimal thirteenthMonthPay = parseBigDecimal(payrollDetails.get("13th Month Pay"));

        // Fill in receipt details with parsed values
        receipt.append("\nSALARY/MONTH: ").append("                                    ").append(currencyFormat.format(totalSalary)).append("\n");
        receipt.append("Rate/Hour: ").append("                                       ").append(ratePerHour).append(" hrs\n");
        receipt.append("Regular Hours/Month: ").append("                                ").append(hoursPerMonth).append(" hrs\n");
        receipt.append("Total Hours Worked: ").append("                              ").append(totalHoursWorked).append(" hrs\n");
        receipt.append("Overtime Hours: ").append("                                    ").append(overtimeHours).append(" hrs\n");

        // Deductions
        receipt.append("\nDEDUCTIONS: ").append("                                      ").append(currencyFormat.format(totalDeductions)).append("\n");
        receipt.append("PhilHealth: ").append("                                      ").append(currencyFormat.format(philHealth)).append("\n");
        receipt.append("SSS: ").append("                                             ").append(currencyFormat.format(sss)).append("\n");
        receipt.append("Pag-ibig: ").append("                                        ").append(currencyFormat.format(pagibig)).append("\n");
        receipt.append("Income Tax: ").append("                                      ").append(currencyFormat.format(incomeTax)).append("\n");
        
        receipt.append("Unpaid Leave: ").append("                                    ").append(currencyFormat.format(unpaidLeaveCost)).append("\n");
        receipt.append("Leave Balance: ").append("                                   ").append(leaveBalance).append("\n");
        receipt.append("Unused Leave: ").append("                                    ").append(currencyFormat.format(unusedLeave)).append("\n");
        receipt.append("13th Month Pay: ").append("                                  ").append(currencyFormat.format(thirteenthMonthPay)).append("\n");

        // Total / Net Salary
        receipt.append("\nNET SALARY: ").append("                                      ").append(currencyFormat.format(netSalary)).append("\n");

        // Set the text content to the JTextArea
        receiptTextArea1.setText(receipt.toString());
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO; // Return 0 if the value is empty or null
        }
        try {
            // Remove any non-numeric characters except . and - for valid decimal representation
            String sanitizedValue = value.replaceAll("[^\\d.-]", "").trim();
            if (sanitizedValue.isEmpty() || sanitizedValue.equals(".") || sanitizedValue.equals("-")) {
                return BigDecimal.ZERO; // Return 0 if the sanitized value is still invalid
            }
            return new BigDecimal(sanitizedValue);
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse BigDecimal from value: " + value + ". Defaulting to 0.");
            return BigDecimal.ZERO;
        }
    }
    
    private int parseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0; // Return 0 if the value is empty or null
        }
        try {
            // Remove any non-numeric characters except for minus sign
            String sanitizedValue = value.replaceAll("[^\\d-]", "").trim();
            return Integer.parseInt(sanitizedValue);
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse Integer from value: " + value + ". Defaulting to 0.");
            return 0;
        }
    }


 

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        receiptTextArea1 = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        btnPrint = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setForeground(new java.awt.Color(2, 98, 154));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("4506 - Polangui, Albay");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 60, 220, -1));

        jLabel3.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("Centro Oriental, San Juan St.");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 80, 220, -1));

        jLabel5.setFont(new java.awt.Font("Monospaced", 1, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("Salary Slip");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 142, 44));

        jSeparator1.setBackground(new java.awt.Color(9, 36, 48));
        jSeparator1.setForeground(new java.awt.Color(9, 36, 48));
        jSeparator1.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        jSeparator1.setMinimumSize(new java.awt.Dimension(60, 20));
        jSeparator1.setPreferredSize(new java.awt.Dimension(60, 20));
        jPanel2.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 280, -1));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        receiptTextArea1.setEditable(false);
        receiptTextArea1.setBackground(new java.awt.Color(255, 255, 255));
        receiptTextArea1.setColumns(20);
        receiptTextArea1.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        receiptTextArea1.setRows(5);
        receiptTextArea1.setBorder(null);
        jScrollPane1.setViewportView(receiptTextArea1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 130, 460, 590));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        btnPrint.setBackground(new java.awt.Color(10, 60, 89));
        btnPrint.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        btnPrint.setForeground(new java.awt.Color(255, 255, 255));
        btnPrint.setText("Print");
        btnPrint.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPrint.setFocusable(false);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnPrint)
                .addContainerGap(69, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 730, 250, 110));

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_END);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        PrinterJob printerJob = PrinterJob.getPrinterJob();

        printerJob.setPrintable((Graphics g, PageFormat pf, int pageIndex) -> {
            // Set margins
            int margin = 50; // Top and left margin
            int bottomMargin = 100; // Bottom margin
            int lineHeight = g.getFontMetrics().getHeight();
            int pageHeight = (int) pf.getImageableHeight();
            int y = margin;

            // Get the text from the receipt
            String receiptText = receiptTextArea1.getText();
            String[] lines = receiptText.split("\n");

            // Draw the labels
            g.setFont(receiptTextArea1.getFont()); // Set the same font as the JTextArea
            g.setColor(Color.BLACK); // Set text color

            // Draw jLabel5 with its own font
            g.setFont(jLabel5.getFont());
            g.drawString(jLabel5.getText(), margin, y);
            y += lineHeight;

            // Add space between jLabel5 and jLabel2
            y += 10; // Adjust this value as needed for the desired space

            // Draw other labels with the JTextArea font
            g.setFont(receiptTextArea1.getFont()); // Revert to JTextArea font
            g.drawString(jLabel2.getText(), margin, y);
            y += lineHeight; 
            g.drawString(jLabel3.getText(), margin, y);
            y += lineHeight; 

            // Add space between jLabel3 and the employee JTextArea
            y += 10; // Add 10 pixels of space (adjust as needed)

            // Draw the receipt text
            int totalContentHeight = lines.length * lineHeight; 
            int linesPerPage = (pageHeight - margin - bottomMargin) / lineHeight;
            int numPages = (int) Math.ceil((double) totalContentHeight / lineHeight / linesPerPage);

            // Check if the requested page index is out of bounds
            if (pageIndex >= numPages) {
                return Printable.NO_SUCH_PAGE; // No more pages
            }

            // Draw each line of the receipt
            int startLine = pageIndex * linesPerPage;
            int endLine = Math.min(lines.length, startLine + linesPerPage);
            for (int i = startLine; i < endLine; i++) {
                // For currency lines, set the font to non-bold
                if (lines[i].contains("SALARY/MONTH") || lines[i].contains("DEDUCTIONS") ||
                    lines[i].contains("NET SALARY") || lines[i].contains("Unused Leave")) {
                    g.setFont(receiptTextArea1.getFont().deriveFont(Font.PLAIN)); // Non-bold font
                } else {
                    g.setFont(receiptTextArea1.getFont()); // Default font
                }
                g.drawString(lines[i], margin, y);
                y += lineHeight; // Move down for the next line
            }

            return Printable.PAGE_EXISTS; // Indicate that the page exists
        });

        boolean doPrint = printerJob.printDialog(); // Show the print dialog
        if (doPrint) {
            try {
                printerJob.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Printing error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        this.dispose();
    }//GEN-LAST:event_btnPrintActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Receipt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Receipt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Receipt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Receipt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Receipt().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPrint;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea receiptTextArea1;
    // End of variables declaration//GEN-END:variables
}
