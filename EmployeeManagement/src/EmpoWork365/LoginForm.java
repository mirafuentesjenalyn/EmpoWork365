/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package EmpoWork365;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;

/**
 *
 * @author jenal
 */
public class LoginForm extends javax.swing.JFrame {
    private static final String PASSWORD_PLACEHOLDER = "Password";


    /**
     * Creates new form MainAdmin
     */
    public LoginForm() {
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
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(17, 94, 94));

        jPanel3.setBackground(new java.awt.Color(17, 94, 94));

        jPanel1.setBackground(new java.awt.Color(8, 127, 127));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel2.setBackground(new java.awt.Color(8, 127, 127));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        eMail.setBackground(new java.awt.Color(229, 255, 237));
        eMail.setForeground(new java.awt.Color(153, 204, 188));
        eMail.setText("Email");
        eMail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                eMailFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                eMailFocusLost(evt);
            }
        });
        jPanel2.add(eMail, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 79, 222, 45));

        passWord.setBackground(new java.awt.Color(229, 255, 237));
        passWord.setForeground(new java.awt.Color(153, 204, 188));
        passWord.setText("Password");
        passWord.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                passWordFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                passWordFocusLost(evt);
            }
        });
        jPanel2.add(passWord, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 136, 222, 45));

        btnLogin.setBackground(new java.awt.Color(185, 230, 230));
        btnLogin.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btnLogin.setForeground(new java.awt.Color(65, 126, 118));
        btnLogin.setText("LOGIN");
        btnLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        jPanel2.add(btnLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 212, 105, 40));

        jLabel2.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 204, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("SIGN IN");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(59, 6, 90, 35));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(34, 78, 0, 78);
        jPanel1.add(jPanel2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("No Account?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 8;
        gridBagConstraints.ipady = 19;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 119, 0, 0);
        jPanel1.add(jLabel3, gridBagConstraints);

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("SIGN UP");
        jButton1.setBorder(null);
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 27;
        gridBagConstraints.ipady = 23;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(19, 6, 42, 0);
        jPanel1.add(jButton1, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(153, 204, 255));
        jLabel1.setText("EmpoWork365");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(194, 194, 194)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(313, 313, 313)
                        .addComponent(jLabel1)))
                .addContainerGap(224, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(56, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(816, 549));
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

        if (password.equals(PASSWORD_PLACEHOLDER) || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email and password cannot be empty.");
            return;
        }

        UserAuthenticate authenticatedUser = loginMethod.authenticate(email, password); 

        if (authenticatedUser != null) {
            String roleName = authenticatedUser.getRoleName();

            if (null == roleName) {
                JOptionPane.showMessageDialog(this, "Invalid role.");
                return;
            } else switch (roleName) {
                case "Admin" -> {
                    MainAdmin adminForm = new MainAdmin();
                    adminForm.setAuthenticatedUser(authenticatedUser);
                    adminForm.setVisible(true);
                }
                case "Employee", "Department Manager", "HR Manager" -> {
                    MainEmployee employeeForm = new MainEmployee();
                    employeeForm.setAuthenticatedUser(authenticatedUser);
                    employeeForm.setVisible(true);
                }
                default -> {
                    JOptionPane.showMessageDialog(this, "Invalid role.");
                    return;
                }
            }

            this.dispose(); 
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password.");
        }
    }//GEN-LAST:event_btnLoginActionPerformed

    private void eMailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_eMailFocusGained
        if (eMail.getText().equals("Email")) {
            eMail.setText("");
            eMail.setForeground(new Color(142,122,69));
        } 
    }//GEN-LAST:event_eMailFocusGained

    private void eMailFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_eMailFocusLost
        if (eMail.getText().isEmpty()) {
            eMail.setText("Email");
            eMail.setForeground(new Color(205,186,136));
        }
    }//GEN-LAST:event_eMailFocusLost

    private void passWordFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passWordFocusGained
        if (String.valueOf(passWord.getPassword()).equals("Password")) {
            passWord.setText("");
            passWord.setEchoChar('*');
            passWord.setForeground(new Color(142,122,69));
        } else {
        }
    }//GEN-LAST:event_passWordFocusGained

    private void passWordFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passWordFocusLost
        if (String.valueOf(passWord.getPassword()).isEmpty()) {
            passWord.setEchoChar((char) 0); 
            passWord.setText("Password"); 
            passWord.setForeground(new Color(205, 186, 136)); 
        }
    }//GEN-LAST:event_passWordFocusLost

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        SignUp signUp = new SignUp();
        signUp.setVisible(true);
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
    private javax.swing.JButton btnLogin;
    private javax.swing.JTextField eMail;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPasswordField passWord;
    // End of variables declaration//GEN-END:variables
}
