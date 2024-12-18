/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package EmpoWork365;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 *
 * @author jenal
 */
public final class EditUserDetails extends javax.swing.JFrame {
    private UserAuthenticate loggedInUser; 
    private Connection connection;
    private int userId;
    private String imagePath;
    private UserUpdateListener userUpdateListener; 
    private static final String PASSWORD_PLACEHOLDER = "Password";

    /**
     * Creates new form SignUp
     * @param mainAdmin
     * @param mainEmployee
     */
    public EditUserDetails(MainAdmin mainAdmin, MainEmployee mainEmployee) {
        
        setUndecorated(true);
        setResizable(false);
        TitleBar titleBar = new TitleBar(this);

        titleBar.setPreferredSize(new Dimension(780, 83)); // Adjust height as needed

        // Add title bar and content panel to the frame
        setLayout(new BorderLayout());
        add(titleBar, BorderLayout.NORTH);

        setSize(780, 731);
        setLocationRelativeTo(null);
        
        ImageIcon icon = IconLoader.getIcon();
        Image img = icon.getImage();
        
        setIconImage(img);
        
        this.userUpdateListener = mainAdmin;

        initComponents();
        this.userId = -1;

        initializeConnection();
        populateGenderComboBox();
        setImageLabel(imagePath);
        
    }
    
    public void setUserUpdateListener(UserUpdateListener listener) {
        this.userUpdateListener = listener;
    }
        
    public void setMainAdmin(MainAdmin admin) {
    }

    private void notifyUserUpdated() {
        if (userUpdateListener != null) {
            userUpdateListener.onUserUpdated(loggedInUser);
        }
    }
    
   public void UpdateUserDetails(UserAuthenticate user) {
        if (user != null) {
            this.loggedInUser = user; // Save the logged-in user
            this.userId = user.getId();
            firstName.setText(user.getFirstname());
            lastName.setText(user.getLastname());
            comboBoxGender.setSelectedItem(user.getGender());
            eMail.setText(user.getEmail());
            passWord.setText(user.getPassword());
            imagePath = user.getImagepath();
            setImageLabel(imagePath);
            
            user.getEmail();
            notifyUserUpdated(); 
        }
    }
 
    public void setEmployeeId(int userId) {
        this.userId = userId; 
        loadEmployeeData();
    }

    private void initializeConnection() {
        sqlConnector dbConnector = new sqlConnector(); 
        try {
            connection = dbConnector.createConnection(); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void populateGenderComboBox() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String selectGenderSQL = "SELECT DISTINCT fld_gender FROM tbl_employees"; 
                List<String> genderList = new ArrayList<>();

                try (PreparedStatement pstmt = connection.prepareStatement(selectGenderSQL);
                     ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String gender = rs.getString("fld_gender");
                        if (gender != null && !gender.isEmpty()) {
                            genderList.add(gender);
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error fetching gender values: " + ex.getMessage());
                }

                // Update the ComboBox on the Event Dispatch Thread
                SwingUtilities.invokeLater(() -> comboBoxGender.setModel(new DefaultComboBoxModel<>(genderList.toArray(new String[0]))));
                return null;
            }

            @Override
            protected void done() {
                // Once ComboBox is populated, call loadEmployeeData to set selected gender
                loadEmployeeData();
            }
        };
        worker.execute();
    }

    private void loadEmployeeData() {
        String selectEmployeeSQL = "SELECT e.fld_first_name, e.fld_last_name, e.fld_email, e.fld_password, "
                                  + "e.fld_gender, e.fld_role_id, e.fld_image_path "
                                  + "FROM tbl_employees e "
                                  + "WHERE e.fld_employee_id = ?"; 

        try (PreparedStatement pstmt = connection.prepareStatement(selectEmployeeSQL)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery(); 

            if (rs.next()) { 
                String firstNameFromDB = rs.getString("fld_first_name");
                String lastNameFromDB = rs.getString("fld_last_name");
                firstName.setText(firstNameFromDB); 
                lastName.setText(lastNameFromDB); 
                eMail.setText(rs.getString("fld_email")); 
                passWord.setText(rs.getString("fld_password")); 
                comboBoxGender.setSelectedItem(rs.getString("fld_gender"));
                imagePath = rs.getString("fld_image_path");
                setImageLabel(imagePath); 
            } 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading employee data: " + e.getMessage());
        }
    }
    
    private void setImageLabel(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            displayImage(imagePath); 
        }
    }
    
    private boolean isEmailDuplicateForLoggedInUser(String currentUserEmail, int userId) {
        if (currentUserEmail.equals(currentUserEmail)) { 
            return false;
        }

        String query = "SELECT fld_employee_id FROM tbl_employees WHERE fld_email = ? AND fld_employee_id != ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, currentUserEmail);
            pstmt.setInt(2, userId); 
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking for duplicate email: " + e.getMessage());
        }
        return false;
    }

    private boolean isDataUnchanged(int targetEmployeeId) {
        String query = "SELECT fld_first_name, fld_last_name, fld_email, fld_password, fld_gender, fld_image_path "
                     + "FROM tbl_employees WHERE fld_employee_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, targetEmployeeId); 
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String originalFirstName = rs.getString("fld_first_name");
                String originalLastName = rs.getString("fld_last_name");
                String originalEmail = rs.getString("fld_email");
                String originalPassword = rs.getString("fld_password");
                String originalGender = rs.getString("fld_gender");
                String originalImagePath = rs.getString("fld_image_path");

                boolean isFirstNameChanged = !firstName.getText().equals(originalFirstName);
                boolean isLastNameChanged = !lastName.getText().equals(originalLastName);
                boolean isEmailChanged = !eMail.getText().equals(originalEmail);
                boolean isPasswordChanged = !String.valueOf(passWord.getPassword()).equals(originalPassword);

                boolean isGenderChanged = !comboBoxGender.getSelectedItem().toString().trim().equalsIgnoreCase(originalGender.trim());

                boolean isImagePathChanged = !imagePath.equals(originalImagePath);


                return !(isFirstNameChanged || isLastNameChanged || isEmailChanged || 
                         isPasswordChanged || isGenderChanged || isImagePathChanged);
            } else {
                return false; 
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking data: " + e.getMessage());
            return true;
        }
    }

    private void handleFocusGained(JTextField field, String placeholder) {
    if (field.getText().equals(placeholder)) {
        field.setText("");
        field.setForeground(new Color(17, 94, 94));
    }
}

    private void handleFocusLost(JTextField field, String placeholder) {
        if (field.getText().isEmpty()) {
            field.setText(placeholder);
            field.setForeground(new Color(153, 204, 188));
        }
    }
    
    private boolean areFieldsEmpty() {
        return firstName.getText().trim().isEmpty() || 
               lastName.getText().trim().isEmpty() || 
               eMail.getText().trim().isEmpty() || 
               String.valueOf(passWord.getPassword()).trim().isEmpty() ||
               comboBoxGender.getSelectedItem() == null ||
               imagePath == null || 
               imagePath.trim().isEmpty();  
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        firstName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        lastName = new javax.swing.JTextField();
        comboBoxGender = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        eMail = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        passWord = new javax.swing.JPasswordField();
        jPanel5 = new javax.swing.JPanel();
        imageLabel = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        btnConfirmEdit = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(240, 240, 240));

        jPanel1.setBackground(new java.awt.Color(240, 240, 240));

        jLabel2.setBackground(new java.awt.Color(0, 36, 57));
        jLabel2.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 36, 57));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Edit Personal Details");

        jPanel2.setBackground(new java.awt.Color(240, 240, 240));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("First Name");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 6, 280, -1));

        firstName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        firstName.setForeground(new java.awt.Color(126, 138, 150));
        firstName.setText("Enter First Name");
        firstName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                firstNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                firstNameFocusLost(evt);
            }
        });
        jPanel2.add(firstName, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 32, 290, 45));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Last Name");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 120, -1));

        lastName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lastName.setForeground(new java.awt.Color(126, 138, 150));
        lastName.setText("Enter Last Name");
        lastName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                lastNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                lastNameFocusLost(evt);
            }
        });
        jPanel2.add(lastName, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 130, 290, 45));

        comboBoxGender.setToolTipText("");
        jPanel2.add(comboBoxGender, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 291, 45));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Gender");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 200, 280, -1));

        jPanel4.setBackground(new java.awt.Color(240, 240, 240));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Email");
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 6, 121, -1));

        eMail.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        eMail.setForeground(new java.awt.Color(126, 138, 150));
        eMail.setText("Enter Email");
        eMail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                eMailFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                eMailFocusLost(evt);
            }
        });
        eMail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActionPerformed(evt);
            }
        });
        jPanel4.add(eMail, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 32, 273, 45));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Password");
        jPanel4.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 95, 121, -1));

        passWord.setForeground(new java.awt.Color(126, 138, 150));
        passWord.setText("Password");
        passWord.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                passWordFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                passWordFocusLost(evt);
            }
        });
        jPanel4.add(passWord, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 127, 273, 45));

        jPanel5.setBackground(new java.awt.Color(204, 204, 255));

        imageLabel.setBackground(new java.awt.Color(204, 204, 255));
        imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imageLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(imageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );

        btnCancel.setBackground(new java.awt.Color(39, 82, 108));
        btnCancel.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btnCancel.setForeground(new java.awt.Color(255, 255, 255));
        btnCancel.setText("CANCEL");
        btnCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancel.setFocusable(false);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(170, 154, 182));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Add Image");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setFocusable(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        btnConfirmEdit.setBackground(new java.awt.Color(0, 36, 57));
        btnConfirmEdit.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btnConfirmEdit.setForeground(new java.awt.Color(255, 255, 255));
        btnConfirmEdit.setText("CONFIRM");
        btnConfirmEdit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfirmEdit.setFocusable(false);
        btnConfirmEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmEditActionPerformed(evt);
            }
        });

        btnClear.setBackground(new java.awt.Color(71, 146, 146));
        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("Clear All");
        btnClear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClear.setFocusable(false);
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(80, 80, 80)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton2)
                                .addGap(158, 158, 158)))
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnConfirmEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 81, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(80, 80, 80))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2)
                            .addComponent(btnClear))))
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConfirmEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(126, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void lastNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lastNameFocusGained
        handleFocusGained(lastName, "Enter Last Name");
    }//GEN-LAST:event_lastNameFocusGained

    private void lastNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lastNameFocusLost
          handleFocusLost(lastName, "Enter Last Name");
    }//GEN-LAST:event_lastNameFocusLost

    private void passWordFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passWordFocusGained
        if (String.valueOf(passWord.getPassword()).equals("Password")) {
            passWord.setText("");
            passWord.setEchoChar('*');
            passWord.setForeground(new Color(17,94,94));
        }
    }//GEN-LAST:event_passWordFocusGained

    private void passWordFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passWordFocusLost
        if (String.valueOf(passWord.getPassword()).isEmpty()) {
            passWord.setEchoChar((char) 0); 
            passWord.setText("Password"); 
            passWord.setForeground(new Color(153, 204, 188)); 
        } else {
            passWord.setEchoChar('*'); 
        }
    }//GEN-LAST:event_passWordFocusLost

    private void btnConfirmEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmEditActionPerformed
        System.out.println("currentUserId: " + userId);

        String email = eMail.getText();

        if (isDataUnchanged(userId)) {
            JOptionPane.showMessageDialog(this, "No changes detected. Please modify the fields before saving.", "No Changes", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (isEmailDuplicateForLoggedInUser(email, userId)) {
            JOptionPane.showMessageDialog(this, "Email already exists. Please use a different email.", "Duplicate Email", JOptionPane.WARNING_MESSAGE);
            return; 
        }
          
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return;
        }
                  
        if (areFieldsEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields are empty.", "Duplicate Email", JOptionPane.WARNING_MESSAGE);
            return; 
        }

          String updateEmployeeSQL = "UPDATE tbl_employees SET fld_first_name = ?, fld_last_name = ?, "
                                   + "fld_email = ?, fld_password = ?, fld_gender = ?, fld_image_path = ? "
                                   + "WHERE fld_employee_id = ?";

          try (PreparedStatement pstmt = connection.prepareStatement(updateEmployeeSQL)) {
              pstmt.setString(1, firstName.getText()); 
              pstmt.setString(2, lastName.getText());  
              pstmt.setString(3, eMail.getText());   
              pstmt.setString(4, String.valueOf(passWord.getPassword())); 
              pstmt.setString(5, comboBoxGender.getSelectedItem().toString());
              pstmt.setString(6, imagePath);
              pstmt.setInt(7, userId);  

              int updatedRows = pstmt.executeUpdate();
              if (updatedRows > 0) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            
                // Update the loggedInUser with new details
                loggedInUser.setFirstname(firstName.getText());
                loggedInUser.setLastname(lastName.getText());
                loggedInUser.setEmail(eMail.getText());
                loggedInUser.setPassword(String.valueOf(passWord.getPassword()));
                loggedInUser.setGender(comboBoxGender.getSelectedItem().toString());
                loggedInUser.setImagepath(imagePath); // Update image path if changed

                // Notify MainAdmin with updated user details
                notifyUserUpdated(); // Now notify with the updated user
                dispose(); 
              } else {
                  JOptionPane.showMessageDialog(this, "No profile found to update.", "Error", JOptionPane.ERROR_MESSAGE);
              }
          } catch (SQLException e) {
              JOptionPane.showMessageDialog(this, "Error updating profile: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
          }
    }//GEN-LAST:event_btnConfirmEditActionPerformed

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    
    private void firstNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_firstNameFocusGained
        if (firstName.getText().equals("Enter First Name")) {
            firstName.setText("");
            firstName.setForeground(new Color(17,94,94));
        }
    }//GEN-LAST:event_firstNameFocusGained

    private void firstNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_firstNameFocusLost
        if (firstName.getText().isEmpty()) {
            firstName.setText("Enter First Name");
            firstName.setForeground(new Color(153,204,188));
        }
    }//GEN-LAST:event_firstNameFocusLost

    private void eMailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_eMailFocusGained
        if (eMail.getText().equals("Enter Email")) {
            eMail.setText("");
            eMail.setForeground(new Color(17,94,94));
        }
    }//GEN-LAST:event_eMailFocusGained

    private void eMailFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_eMailFocusLost
        if (eMail.getText().isEmpty()) {
            eMail.setText("Enter Email");
            eMail.setForeground(new Color(153,204,188));
        }
    }//GEN-LAST:event_eMailFocusLost

    private void ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        selectImage(); 

        try {
            updateEmployeeImageInDatabase(imagePath, userId);
        } catch (IOException ex) {
            Logger.getLogger(EditUserDetails.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearFields();
    }//GEN-LAST:event_btnClearActionPerformed

    private void resetImageSelection() {
        imagePath = null;
        imageLabel.setIcon(new ImageIcon("src/Users/user.png"));  
    }

    private String updateEmployeeImageInDatabase(String imagePath, int userId) throws IOException {
        String updateImageSQL = "UPDATE tbl_employees SET fld_image_path = ? WHERE fld_employee_id = ?";
        String[] acceptedImageExtensions = {".jpg", ".jpeg", ".png"};

        // Check if imagePath is null or empty
        if (imagePath == null || imagePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No image path provided.", "Error", JOptionPane.ERROR_MESSAGE);
            resetImageSelection(); 
            return null;
        }

        String fileExtension = imagePath.substring(imagePath.lastIndexOf(".")).toLowerCase();
        boolean isImage = Arrays.asList(acceptedImageExtensions).contains(fileExtension);

        if (!isImage) {
            JOptionPane.showMessageDialog(this, "Please select a valid image file (jpg, jpeg, png).", "Invalid File Type", JOptionPane.ERROR_MESSAGE);
            resetImageSelection(); 
            return null; // Return here if invalid image
        }

        String destinationFolder = "src/Users/";
        String newFileName = "employee_" + System.currentTimeMillis() + fileExtension;
        String destinationPath = destinationFolder + newFileName;

        File sourceFile = new File(imagePath);
        File destinationFile = new File(destinationPath);

        try (PreparedStatement pstmt = connection.prepareStatement(updateImageSQL)) {
            pstmt.setString(1, destinationPath); // Make sure to set the correct destination path in the database
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();

            // Ensure the destination folder exists
            destinationFile.getParentFile().mkdirs(); 
            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return destinationPath; 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating employee image: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return null; // Ensure to return null in case of failure
    }



    private void displayImage(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                BufferedImage img = ImageIO.read(imageFile);
                if (img != null) {
                    int width = 120;
                    int height = 120;
                    Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledImg));
                } else {
                    throw new IOException("Failed to load image: " + imagePath);
                }
            } else {
                throw new IOException("Image file not found at: " + imagePath);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error displaying image: " + e.getMessage(), "Image Error", JOptionPane.ERROR_MESSAGE);
            imageLabel.setIcon(new ImageIcon("src/Users/user.png")); 
        }
    }


    public void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg", "gif"));

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imagePath = selectedFile.getAbsolutePath(); // Store the selected image path
            displayImage(imagePath); // Display the selected image
        }
    }


    
    private void clearFields() {
        firstName.setText("");
        lastName.setText("");
        eMail.setText("");
        passWord.setText("");
        comboBoxGender.setSelectedIndex(-1);
        imageLabel.setIcon(new ImageIcon("src/Users/user.png")); 
        imagePath = ""; 
    }
    
    
     
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
            java.util.logging.Logger.getLogger(EditUserDetails.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditUserDetails.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditUserDetails.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditUserDetails.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                UserAuthenticate user = new UserAuthenticate();
                MainAdmin mainAdmin = new MainAdmin();
                MainEmployee mainEmployee = new MainEmployee();
                
                new EditUserDetails(mainAdmin, mainEmployee).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnConfirmEdit;
    private javax.swing.JComboBox<String> comboBoxGender;
    private javax.swing.JTextField eMail;
    private javax.swing.JTextField firstName;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JTextField lastName;
    private javax.swing.JPasswordField passWord;
    // End of variables declaration//GEN-END:variables
}
