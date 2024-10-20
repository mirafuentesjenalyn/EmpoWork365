/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package EmpoWork365;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author jenal
 */
public class LoginForm extends javax.swing.JFrame {
    private static final String PASSWORD_PLACEHOLDER = "Password";
    private static final String EMAIL_PLACEHOLDER = "Email";


    /**
     * Creates new form MainAdmin
     */
    public LoginForm() {
        setUndecorated(true);
        setResizable(false);  

        initComponents();
        setTitle("EmpoWork365");
        
//        ImageIcon icon = IconLoader.getIcon();
//        Image img = icon.getImage();
//        
//        setIconImage(img);
        

        
        eMail.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()== KeyEvent.VK_ENTER) {
                    btnLoginActionPerformed(null);
                }
            }
        });
        passWord.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnLoginActionPerformed(null);
                }
            }
        });
        
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(43, 101, 135));
                btnLogin.setForeground(new Color(185, 230, 230));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(185, 230, 230));
                btnLogin.setForeground(new Color(43, 101, 135));
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                // Optional: Change color when button is pressed
                btnLogin.setBackground(new Color(100, 150, 150)); // Set to a darker shade or any other color
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                // Reset to default color when released
                btnLogin.setBackground(new Color(185, 230, 230));
                btnLogin.setForeground(new Color(43, 101, 135));
            }
        });



    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        eMail = new javax.swing.JTextField();
        passWord = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnClose = new javax.swing.JButton();
        btnMin = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(17, 94, 94));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new Color(0, 0, 0, 50));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel2.setBackground(new Color(0, 0, 0, 0));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        eMail.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        eMail.setForeground(new java.awt.Color(65, 126, 118));
        eMail.setText("Email");
        eMail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                eMailFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                eMailFocusLost(evt);
            }
        });
        jPanel2.add(eMail, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, 240, 45));

        passWord.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        passWord.setForeground(new java.awt.Color(65, 126, 118));
        passWord.setText("Password");
        passWord.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                passWordFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                passWordFocusLost(evt);
            }
        });
        jPanel2.add(passWord, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, 240, 45));

        btnLogin.setBackground(new java.awt.Color(185, 230, 230));
        btnLogin.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btnLogin.setForeground(new java.awt.Color(43, 101, 135));
        btnLogin.setText("LOGIN");
        btnLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        jPanel2.add(btnLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 210, 110, 40));

        jLabel2.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("SIGN IN");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, 90, 35));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(204, 204, 204));
        jLabel3.setText("No Account?");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 270, -1, 30));

        jButton1.setBackground(new Color(0, 0, 0, 80));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(204, 204, 204));
        jButton1.setText("SIGN UP");
        jButton1.setBorder(null);
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);
        jButton1.setOpaque(true);
        jButton1.setRequestFocusEnabled(false);
        jButton1.setRolloverEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 270, 90, 30));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/email.png"))); // NOI18N
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/pass.png"))); // NOI18N
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, -1, 34));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.ipady = 29;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(43, 57, 52, 69);
        jPanel1.add(jPanel2, gridBagConstraints);

        jPanel3.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(209, 50, 360, 390));

        jPanel5.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jPanel5ComponentResized(evt);
            }
        });
        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/bg2.jpg"))); // NOI18N
        jPanel5.add(jLabel6, java.awt.BorderLayout.CENTER);

        jPanel3.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 800, 560));

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel4.setBackground(new java.awt.Color(0, 36, 57));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Empower Work for 365");
        jPanel4.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, 60));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/close.png"))); // NOI18N
        btnClose.setContentAreaFilled(false);
        btnClose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClose.setFocusable(false);
        btnClose.setRequestFocusEnabled(false);
        btnClose.setRolloverEnabled(false);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel4.add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 20, 50, 40));

        btnMin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/minimize.png"))); // NOI18N
        btnMin.setContentAreaFilled(false);
        btnMin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMin.setFocusable(false);
        btnMin.setRequestFocusEnabled(false);
        btnMin.setRolloverEnabled(false);
        btnMin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMinActionPerformed(evt);
            }
        });
        jPanel4.add(btnMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 20, 50, 40));

        getContentPane().add(jPanel4, java.awt.BorderLayout.PAGE_START);

        setSize(new java.awt.Dimension(795, 608));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public class CallHome {

    public void setLoggedInUser(UserAuthenticate user) {
        }
    }

    
    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        LoginMethod loginMethod = new LoginMethod();
        String email = eMail.getText().trim(); 
        String password = new String(passWord.getPassword());
        btnLogin.setBackground(new Color(185, 230, 230));
        btnLogin.setForeground(new Color(43, 101, 135));
        // Validate fields
        if (email.equals(EMAIL_PLACEHOLDER) || password.equals(PASSWORD_PLACEHOLDER) || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email and password cannot be empty.");
            return;
        }

        UserAuthenticate authenticatedUser = loginMethod.authenticate(email, password); 

        if (authenticatedUser != null) {
            new Employee(authenticatedUser.getId(),  
                    authenticatedUser.getFirstname(),
                    authenticatedUser.getLastname(),
                    authenticatedUser.getEmail(),
                    authenticatedUser.getGender(),
                    authenticatedUser.getJobtitle(),
                    authenticatedUser.getDepartmentName(),
                    authenticatedUser.getImagepath());


            String roleName = authenticatedUser.getRoleName();

            if (roleName == null) {
                JOptionPane.showMessageDialog(this, "Invalid role.");
                return;
            } else {
                switch (roleName) {
                    case "Admin" -> {
                        MainAdmin adminForm = new MainAdmin();
                        adminForm.setAuthenticatedUser(authenticatedUser);
                        adminForm.setVisible(true);
                    }
                    case "Employee", "Department Manager", "HR Manager" -> {
                        try {
                            MainEmployee employeeForm = new MainEmployee();
                            employeeForm.setAuthenticatedUser(authenticatedUser);
                            employeeForm.setVisible(true);
                        } catch (SQLException ex) {
                            Logger.getLogger(LoginForm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    default -> {
                        JOptionPane.showMessageDialog(this, "Invalid role.");
                        return;
                    }
                }
            }

            this.dispose(); 
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password.");
        }
        
    }//GEN-LAST:event_btnLoginActionPerformed

    private void eMailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_eMailFocusGained
        if (eMail.getText().equals(EMAIL_PLACEHOLDER)) {
            eMail.setText("");
            eMail.setForeground(new Color(65,126,118));
        }
    }//GEN-LAST:event_eMailFocusGained

    private void eMailFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_eMailFocusLost
        if (eMail.getText().isEmpty()) {
            eMail.setForeground(new Color(65,126,118)); 
            eMail.setText(EMAIL_PLACEHOLDER);
        }
    }//GEN-LAST:event_eMailFocusLost

    private void passWordFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passWordFocusGained
        if (String.valueOf(passWord.getPassword()).equals("Password")) {
            passWord.setText("");
            passWord.setEchoChar('*');
            passWord.setForeground(new Color(65,126,118));
        } else {
        }
    }//GEN-LAST:event_passWordFocusGained

    private void passWordFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passWordFocusLost
        if (String.valueOf(passWord.getPassword()).isEmpty()) {
            passWord.setEchoChar((char) 0); 
            passWord.setText("Password"); 
            passWord.setForeground(new Color(65,126,118)); 
        }
    }//GEN-LAST:event_passWordFocusLost

    private void jPanel5ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel5ComponentResized
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel5ComponentResized

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        System.exit(0); // Close the application
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnMinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMinActionPerformed
        this.setState(JFrame.ICONIFIED); 
    }//GEN-LAST:event_btnMinActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        SignUp signUp = new SignUp();
        signUp.setVisible(true);ere:
        
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setFocusPainted(false); 
    }//GEN-LAST:event_jButton1ActionPerformed



    
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
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnMin;
    private javax.swing.JTextField eMail;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPasswordField passWord;
    // End of variables declaration//GEN-END:variables
}
