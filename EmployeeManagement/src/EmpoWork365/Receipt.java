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
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JOptionPane;

/**
 *
 * @author jenal
 */
public class Receipt extends javax.swing.JFrame {

    private BigDecimal  unpaidLeaveCost;
    private BigDecimal totalDeductions;

    /**
     * Creates new form Receipt
     */
    public Receipt() {
        initComponents();
        receiptTextArea1.setEditable(false); 
    }
    
    public void setPayrollDetails(Employee employee, BigDecimal totalSalary, BigDecimal ratePerHour, 
                               BigDecimal totalHoursWorked, BigDecimal overtimeHours, 
                               BigDecimal totalDeductions, BigDecimal netSalary, 
                               BigDecimal unusedLeave, BigDecimal thirteenthMonthPay, 
                               int selectedMonth) {
        // Ensure all values are initialized
        this.unpaidLeaveCost = BigDecimal.ZERO; // Assume it will be passed for December
        // Remove the deduction calculation here since it's already done
        this.totalDeductions = totalDeductions; // Use passed value directly

        boolean isDecember = (selectedMonth == 12);

        // Generate payroll receipt content
        String receiptContent = generatePayrollReceipt(
            employee, totalSalary, ratePerHour, totalHoursWorked, overtimeHours, netSalary,
            unusedLeave, thirteenthMonthPay, isDecember, selectedMonth
        );

        // Set the text content to the JTextArea
        receiptTextArea1.setText(receiptContent);
    }

     private String generatePayrollReceipt(Employee employee, BigDecimal totalSalary, BigDecimal ratePerHour, 
                                           BigDecimal totalHoursWorked, BigDecimal overtimeHours, 
                                           BigDecimal netSalary, BigDecimal unusedLeave, 
                                           BigDecimal thirteenthMonthPay, boolean isDecember,
                                           int selectedMonth) {
        StringBuilder receipt = new StringBuilder();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

        // Month names array
        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        String monthName = monthNames[selectedMonth - 1];  // Adjust for zero-based index

        // Employee details
        receipt.append("---- Payroll Receipt ----\n");
        receipt.append("Employee ID: ").append(employee.getEmployeeId()).append("\n");
        receipt.append("Employee Name: ").append(employee.getFirstname()).append(" ").append(employee.getLastname()).append("\n");
        receipt.append("Job Title: ").append(employee.getJobtitle()).append("\n");
        receipt.append("Department: ").append(employee.getDepartmentName()).append("\n");

        // Payroll month
        receipt.append("\nMONTHLY").append("\n").append(monthName.toUpperCase()).append("\n");

        // Salary details
        receipt.append("\nSALARY/MONTH: ").append(currencyFormat.format(totalSalary != null ? totalSalary : BigDecimal.ZERO)).append("\n");
        receipt.append("Rate/Hour: ").append(String.format("%.2f hrs", ratePerHour != null ? ratePerHour : BigDecimal.ZERO)).append("\n");
        receipt.append("Total Hours Worked: ").append(String.format("%.2f hrs", totalHoursWorked != null ? totalHoursWorked : BigDecimal.ZERO)).append("\n");
        receipt.append("Overtime Hours: ").append(String.format("%.2f hrs", overtimeHours != null ? overtimeHours : BigDecimal.ZERO)).append("\n");

        // Deductions
        receipt.append("\nDEDUCTIONS: ").append(currencyFormat.format(totalDeductions)).append("\n");
        receipt.append("PhilHealth: ").append(currencyFormat.format(calculatePhilHealthDeduction(totalSalary))).append("\n");
        receipt.append("SSS: ").append(currencyFormat.format(calculateSSSDeduction(totalSalary))).append("\n");
        receipt.append("Pag-Ibig: ").append(currencyFormat.format(calculatePagIbigDeduction(totalSalary))).append("\n");

        if (isDecember) {
            receipt.append("\nOTHERS:").append("\n");
            receipt.append("Unpaid Leave Deduction: ").append(currencyFormat.format(unpaidLeaveCost)).append("\n");
            receipt.append("Unused Leave: ").append(currencyFormat.format(unusedLeave != null ? unusedLeave : BigDecimal.ZERO)).append("\n");
            receipt.append("Bonus: ").append(currencyFormat.format(thirteenthMonthPay != null ? thirteenthMonthPay : BigDecimal.ZERO)).append("\n");

            // Total calculation for December
            BigDecimal totalDecemberSalary = netSalary.add(unusedLeave != null ? unusedLeave : BigDecimal.ZERO)
                .add(thirteenthMonthPay != null ? thirteenthMonthPay : BigDecimal.ZERO)
                .subtract(unpaidLeaveCost); // Deduct unpaid leave from total salary
            receipt.append("\nTOTAL: ").append(currencyFormat.format(totalDecemberSalary)).append("\n");
        } else {
            receipt.append("\nTOTAL: ").append(currencyFormat.format(netSalary != null ? netSalary : BigDecimal.ZERO)).append("\n");
        }

        return receipt.toString();
    }

    private BigDecimal calculatePhilHealthDeduction(BigDecimal totalSalary) {
        return totalSalary.multiply(BigDecimal.valueOf(0.01)); // Example calculation
    }

    private BigDecimal calculateSSSDeduction(BigDecimal totalSalary) {
        return totalSalary.multiply(BigDecimal.valueOf(0.02)); // Example calculation
    }

    private BigDecimal calculatePagIbigDeduction(BigDecimal totalSalary) {
        return totalSalary.compareTo(BigDecimal.valueOf(200)) <= 0 
            ? totalSalary.multiply(BigDecimal.valueOf(0.01)) 
            : totalSalary.multiply(BigDecimal.valueOf(0.02)); // Example calculation
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
