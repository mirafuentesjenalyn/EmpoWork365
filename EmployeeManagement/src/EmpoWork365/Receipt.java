/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package EmpoWork365;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import static java.lang.Integer.parseInt;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
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
        initComponents();
        receiptTextArea1.setEditable(false); 
    }
    
    public void setPayrollDetails(Map<String, String> payrollDetails) {
        StringBuilder receipt = new StringBuilder();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

        receipt.append("---- Payroll Receipt ----\n");
        receipt.append("Employee ID: ").append(payrollDetails.getOrDefault("Employee ID", "")).append("\n");
        receipt.append("Employee Name: ").append(payrollDetails.getOrDefault("Full Name", "")).append("\n");
        receipt.append("Email: ").append(payrollDetails.getOrDefault("Email", "")).append("\n");
        receipt.append("Job Title: ").append(payrollDetails.getOrDefault("Job Title", "")).append("\n");
        receipt.append("Department: ").append(payrollDetails.getOrDefault("Department", "")).append("\n");

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
        receipt.append("\nSALARY/MONTH: ").append(currencyFormat.format(totalSalary)).append("\n");
        receipt.append("Rate/Hour: ").append(ratePerHour).append(" hrs\n");
        receipt.append("Regular Hours/Month: ").append(hoursPerMonth).append(" hrs\n");
        receipt.append("Total Hours Worked: ").append(totalHoursWorked).append(" hrs\n");
        receipt.append("Overtime Hours: ").append(overtimeHours).append(" hrs\n");

        // Deductions
        receipt.append("\nDEDUCTIONS: ").append(currencyFormat.format(totalDeductions)).append("\n");
        receipt.append("PhilHealth: ").append(currencyFormat.format(philHealth)).append("\n");
        receipt.append("SSS: ").append(currencyFormat.format(sss)).append("\n");
        receipt.append("Pag-Ibig: ").append(currencyFormat.format(pagibig)).append("\n");
        receipt.append("Income Tax: ").append(currencyFormat.format(incomeTax)).append("\n");
        
        receipt.append("Unpaid Leave: ").append(currencyFormat.format(unpaidLeaveCost)).append("\n");
        receipt.append("Leave Balance: ").append(leaveBalance).append("\n");
        receipt.append("Unused Leave: ").append(currencyFormat.format(unusedLeave)).append("\n");
        receipt.append("13th Month Pay: ").append(currencyFormat.format(thirteenthMonthPay)).append("\n");

        // Total / Net Salary
        receipt.append("\nNET SALARY: ").append(currencyFormat.format(netSalary)).append("\n");

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

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        receiptTextArea1 = new javax.swing.JTextArea();
        btnPrint = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Payroll Receipt");

        receiptTextArea1.setColumns(20);
        receiptTextArea1.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        receiptTextArea1.setRows(5);
        jScrollPane1.setViewportView(receiptTextArea1);

        jScrollPane2.setViewportView(jScrollPane1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnPrint.setText("Print");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(160, 160, 160)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(109, 109, 109)
                        .addComponent(btnPrint)
                        .addGap(107, 107, 107)
                        .addComponent(btnClose))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClose)
                    .addComponent(btnPrint))
                .addGap(40, 40, 40))
        );

        pack();
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

            // Total height of all lines and lines per page
            int totalContentHeight = lines.length * lineHeight; 
            int linesPerPage = (pageHeight - margin - bottomMargin) / lineHeight;
            int numPages = (int) Math.ceil((double) totalContentHeight / lineHeight / linesPerPage);

            // Check if the requested page index is out of bounds
            if (pageIndex >= numPages) {
                return Printable.NO_SUCH_PAGE; // No more pages
            }

            // Draw the content
            g.setFont(receiptTextArea1.getFont()); // Set the same font as the JTextArea
            g.setColor(Color.BLACK); // Set text color

            // Draw each line of the receipt
            int startLine = pageIndex * linesPerPage;
            int endLine = Math.min(lines.length, startLine + linesPerPage);
            for (int i = startLine; i < endLine; i++) {
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

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

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
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnPrint;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea receiptTextArea1;
    // End of variables declaration//GEN-END:variables
}
