/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package EmpoWork365;

import EmpoWork365.Employee;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author jenal
 */
public class Receipt extends javax.swing.JFrame {

        private double unpaidLeaveCost;
    private double totalDeductions;

    /**
     * Creates new form Receipt
     */
    public Receipt() {
        initComponents();
        receiptTextArea1.setEditable(false); 
    }
 public void setPayrollDetails(Employee employee, double totalSalary, double totalHoursWorked,
                                  double overtimeHours, double totalDeductions, double netSalary,
                                  double unusedLeave, double thirteenthMonthPay, int selectedMonth,
                                  int totalAbsences) {
        boolean isDecember = (selectedMonth == 12); // Check if it's December

        // Generate payroll receipt content
        String receiptContent = generatePayrollReceipt(
            employee, totalSalary, totalHoursWorked, overtimeHours, unpaidLeaveCost, netSalary,
            unusedLeave, thirteenthMonthPay, isDecember, selectedMonth, totalAbsences
        );

        // Set the text content to the JTextArea
        receiptTextArea1.setText(receiptContent);
    }

    // Generate the payroll receipt content
    private String generatePayrollReceipt(Employee employee, double totalSalary, double totalHoursWorked,
                                          double overtimeHours, double unpaidLeaveCost, double netSalary,
                                          double unusedLeave, double thirteenthMonthPay, boolean isDecember,
                                          int selectedMonth, int totalAbsences) {
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
        receipt.append("Employee Name: ").append(employee.getFirstname()).append(" ").append(employee.getLastname()).append("\n");
        receipt.append("Job Title: ").append(employee.getJobtitle()).append("\n");
        receipt.append("Department: ").append(employee.getDepartmentName()).append("\n");

        // Payroll month
        receipt.append("Payroll Month: ").append(monthName).append("\n");
        receipt.append("\n");

        // Salary details
        receipt.append("Total Hours Worked: ").append(String.format("%.2f hrs", totalHoursWorked)).append("\n");
        receipt.append("Overtime Hours: ").append(String.format("%.2f hrs", overtimeHours)).append("\n");
        receipt.append("Total Salary: ").append(currencyFormat.format(totalSalary)).append("\n");

        receipt.append("\nDeductions:\n");
        receipt.append("PhilHealth: ").append(currencyFormat.format(calculatePhilHealthDeduction(totalSalary))).append("\n");
        receipt.append("SSS: ").append(currencyFormat.format(calculateSSSDeduction(totalSalary))).append("\n");
        receipt.append("Income Tax: ").append(currencyFormat.format(totalDeductions)).append("\n");
        receipt.append("Pag-Ibig: ").append(currencyFormat.format(calculatePagIbigDeduction(totalSalary))).append("\n");
        receipt.append("Total Deductions: ").append(currencyFormat.format(calculateIncomeTax(totalSalary))).append("\n");

        receipt.append("Net Salary: ").append(currencyFormat.format(netSalary)).append("\n");

        if (isDecember) {
            // December-specific details
            receipt.append("Unpaid Leave Deduction: ").append(currencyFormat.format(unpaidLeaveCost)).append("\n");
            receipt.append("Unused Leave: ").append(currencyFormat.format(unusedLeave)).append("\n");
            receipt.append("13th Month Pay: ").append(currencyFormat.format(thirteenthMonthPay)).append("\n");
            receipt.append("Total Absences: ").append(totalAbsences).append("\n");

            // Add unused leave and 13th month pay to salary
            double totalDecemberSalary = netSalary + unusedLeave + thirteenthMonthPay;
            receipt.append("Total December Salary: ").append(currencyFormat.format(totalDecemberSalary)).append("\n");
        }


        return receipt.toString();
    }

    // Additional helper methods for calculating deductions
 private double calculatePhilHealthDeduction(double totalSalary) {
        return 0.01 * totalSalary; // Example calculation
    }

    private double calculateSSSDeduction(double totalSalary) {
        return 0.02 * totalSalary; // Example calculation
    }

    private double calculatePagIbigDeduction(double totalSalary) {
        return totalSalary <= 200 ? 0.01 * totalSalary : 0.02 * totalSalary; // Example calculation
    }

    private double calculateIncomeTax(double totalSalary) {
        // Example calculation for income tax, adjust according to your requirements
        return totalSalary * 0.1; // Example 10% tax
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
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Payroll Receipt");

        receiptTextArea1.setColumns(20);
        receiptTextArea1.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
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

        jButton2.setText("Close");

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
                        .addComponent(jButton2))
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
                    .addComponent(jButton2)
                    .addComponent(btnPrint))
                .addGap(40, 40, 40))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
//        PrinterJob printerJob = PrinterJob.getPrinterJob();
//
//        printerJob.setPrintable((Graphics g, PageFormat pf, int pageIndex) -> {
//            // Set margins
//            int margin = 50; // Top and left margin
//            int bottomMargin = 100; // Bottom margin
//            int lineHeight = g.getFontMetrics().getHeight();
//            int pageHeight = (int) pf.getImageableHeight();
//            int y1 = margin;
//            // Calculate total content height and lines
//            String receiptText = receiptTextArea1.getText();
//            String[] lines = receiptText.split("\n");
//            int totalContentHeight = (lines.length + 4) * lineHeight; // 4 extra lines for the header
//            // Calculate lines per page, considering bottom margin
//            int linesPerPage = (pageHeight - margin - bottomMargin) / lineHeight;
//            int numPages = (int) Math.ceil((double) totalContentHeight / lineHeight / linesPerPage);
//            if (pageIndex >= numPages) {
//                return Printable.NO_SUCH_PAGE; // No more pages
//            }
//            //                // Draw header on each page
////                g.setFont(jLabel1.getFont());
////                g.drawString(jLabel1.getText(), margin, y);
////                y += lineHeight;
////
////                g.setFont(jLabel2.getFont());
////                g.drawString(jLabel2.getText(), margin, y);
////                y += lineHeight;
////
////                g.drawString(jLabel3.getText(), margin, y);
////                y += lineHeight;
////
////                g.drawString(jLabel4.getText(), margin, y);
////                y += lineHeight;
//
//        // Draw receipt content
//        g.setFont(receiptTextArea1.getFont());
//        int startLine = pageIndex * linesPerPage;
//        int endLine = Math.min(lines.length, startLine + linesPerPage);
//                    for (int i = startLine; i < endLine; i++) {
//                        if (y1 + lineHeight > pageHeight - bottomMargin) {
//                            break; // Stop drawing if we reach the bottom margin
//                        }
//                        g.drawString(lines[i], margin, y1);
//                        y1 += lineHeight;
//                    }
//                    return Printable.PAGE_EXISTS; // Indicate that the page exists
//                });
//
//        boolean doPrint = printerJob.printDialog(); // Show the print dialog
//        if (doPrint) {
//            try {
//                printerJob.print(); // Print the document
//            } catch (PrinterException ex) {
//                JOptionPane.showMessageDialog(this, "Printing error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        }
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
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea receiptTextArea1;
    // End of variables declaration//GEN-END:variables
}
