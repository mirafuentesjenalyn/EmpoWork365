/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package EmpoWork365;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
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

    /**
     * Creates new form SignUp
     * @param mainAdmin
     * @param mainEmployee
     */
    public EditUserDetails(MainAdmin mainAdmin, MainEmployee mainEmployee) {
        this.userUpdateListener = mainAdmin;
        
        initComponents();
        this.userId = -1;
        setTitle("Edit Employee");
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
    
private void saveChanges() {
    // Ensure the loggedInUser is available before proceeding
    if (loggedInUser != null) {
        // Create a new instance of UserAuthenticate with existing data
        UserAuthenticate updatedUser = new UserAuthenticate(
            loggedInUser.getId(), // Pass the existing ID
            firstName.getText(),
            lastName.getText(),
            eMail.getText(),
            String.valueOf(passWord.getPassword()), // Only update if needed
            comboBoxGender.getSelectedItem().toString(),
            loggedInUser.getJobtitle(), // Keep existing job title
            loggedInUser.getDepartmentName(), // Keep existing department name
            loggedInUser.getRoleName(), // Keep existing role name
            imagePath // Updated image path if changed
        );

        // Call notify to update the MainAdmin with the updated user
        notifyUserUpdated(); // Notify MainAdmin of the changes
    } else {
        JOptionPane.showMessageDialog(this, "User is not logged in.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

//    public void refreshUserDetails() {
//      if (loggedInUser != null) {
//            UpdateUserDetails(loggedInUser); // Refresh user details from the logged-in user
//        }
//    }


    
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
        SwingWorker<Void, String> worker = new SwingWorker<>() {
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

                comboBoxGender.setModel(new DefaultComboBoxModel<>(genderList.toArray(String[]::new)));
                return null;
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

                // Debugging output
                System.out.println("Original First Name: " + originalFirstName + " | Current First Name: " + firstName.getText());
                System.out.println("Original Last Name: " + originalLastName + " | Current Last Name: " + lastName.getText());
                System.out.println("Original Email: " + originalEmail + " | Current Email: " + eMail.getText());
                System.out.println("Original Password: " + originalPassword + " | Current Password: " + String.valueOf(passWord.getPassword()));
                System.out.println("Original Gender: " + originalGender + " | Current Gender: " + comboBoxGender.getSelectedItem());
                System.out.println("Original Image Path: " + originalImagePath + " | Current Image Path: " + imagePath);

                boolean isFirstNameChanged = !firstName.getText().equals(originalFirstName);
                boolean isLastNameChanged = !lastName.getText().equals(originalLastName);
                boolean isEmailChanged = !eMail.getText().equals(originalEmail);
                boolean isPasswordChanged = !String.valueOf(passWord.getPassword()).equals(originalPassword);
                boolean isGenderChanged = !comboBoxGender.getSelectedItem().toString().equals(originalGender);

                boolean isImagePathChanged = !imagePath.equals(originalImagePath);

                // Debug output for image change detection
                System.out.println("Image Path Changed: " + isImagePathChanged);
                System.out.println("isFirstNameChanged: " + isFirstNameChanged);
                System.out.println("isLastNameChanged: " + isLastNameChanged);
                System.out.println("isEmailChanged: " + isEmailChanged);
                System.out.println("isPasswordChanged: " + isPasswordChanged);
                System.out.println("isGenderChanged: " + isGenderChanged);

                return !(isFirstNameChanged || isLastNameChanged || isEmailChanged || 
                         isPasswordChanged || isGenderChanged || isImagePathChanged);
            } else {
                System.out.println("No employee found with ID: " + targetEmployeeId);
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
        jPanel7 = new javax.swing.JPanel();
        btnClose1 = new javax.swing.JButton();
        btnMin1 = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addContainerGap(105, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(0, 36, 57));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnClose1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/close.png"))); // NOI18N
        btnClose1.setContentAreaFilled(false);
        btnClose1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClose1.setFocusable(false);
        btnClose1.setRequestFocusEnabled(false);
        btnClose1.setRolloverEnabled(false);
        btnClose1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClose1ActionPerformed(evt);
            }
        });
        jPanel7.add(btnClose1, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 50, 50, 40));

        btnMin1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/minimize.png"))); // NOI18N
        btnMin1.setContentAreaFilled(false);
        btnMin1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMin1.setFocusable(false);
        btnMin1.setRequestFocusEnabled(false);
        btnMin1.setRolloverEnabled(false);
        btnMin1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMin1ActionPerformed(evt);
            }
        });
        jPanel7.add(btnMin1, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 50, 50, 40));

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/logo.png"))); // NOI18N
        jPanel7.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, 50));

        jLabel3.setFont(new java.awt.Font("Perpetua", 1, 28)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("EMPOWER");
        jPanel7.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 40, -1, 60));

        jLabel17.setFont(new java.awt.Font("Viner Hand ITC", 1, 24)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Work");
        jPanel7.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 40, 70, 60));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("for 365");
        jPanel7.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 50, 60, 40));

        jSeparator1.setBackground(new java.awt.Color(240, 240, 240));
        jSeparator1.setForeground(new java.awt.Color(240, 240, 240));
        jSeparator1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel7.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 780, -1));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(796, 855));
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
        JFileChooser fileChooser = new JFileChooser(); 
        fileChooser.setDialogTitle("Select Employee Image");

        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile(); 
            imagePath = selectedFile.getAbsolutePath(); 

            displayImage(imagePath);

            updateEmployeeImageInDatabase(imagePath, userId);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearFields();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnClose1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose1ActionPerformed
        System.exit(0); // Close the application
    }//GEN-LAST:event_btnClose1ActionPerformed

    private void btnMin1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMin1ActionPerformed
        this.setState(JFrame.ICONIFIED);
    }//GEN-LAST:event_btnMin1ActionPerformed


    private void updateEmployeeImageInDatabase(String imagePath, int userId) {
        String updateImageSQL = "UPDATE tbl_employees SET fld_image_path = ? WHERE fld_employee_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateImageSQL)) {
            pstmt.setString(1, imagePath);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate(); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating employee image: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayImage(String imagePath) {
        BufferedImage img = null;
        try {
            // Ensure the path is valid
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                img = ImageIO.read(imageFile);
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
            imageLabel.setIcon(new ImageIcon("src/Users/")); 
        }
    }

    public void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg", "gif"));

        int response = fileChooser.showOpenDialog(null);
        if (response == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imagePath = selectedFile.getAbsolutePath(); // Update the image path
            displayImage(imagePath); // Display the selected image
        }
    }

    
    private void clearFields() {
        firstName.setText("");
        lastName.setText("");
        eMail.setText("");
        passWord.setText("");
        comboBoxGender.setSelectedIndex(-1);
        imageLabel.setIcon(null); 
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
    private javax.swing.JButton btnClose1;
    private javax.swing.JButton btnConfirmEdit;
    private javax.swing.JButton btnMin1;
    private javax.swing.JComboBox<String> comboBoxGender;
    private javax.swing.JTextField eMail;
    private javax.swing.JTextField firstName;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
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
    private javax.swing.JPanel jPanel7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField lastName;
    private javax.swing.JPasswordField passWord;
    // End of variables declaration//GEN-END:variables
}
