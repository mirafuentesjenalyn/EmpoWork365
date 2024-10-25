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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author jenal
 */
public final class AddAccount extends javax.swing.JFrame {
    private Connection connection;
    private static final List<JobTitle> jobTitles = new ArrayList<>();
    private String imageLocation;
    private boolean imageSelected = false;
    private static final String PASSWORD_PLACEHOLDER = "Password";
    private final MainAdmin mainAdmin;
    
    /**
     * Creates new form SignUp
     * @param mainAdmin
     */
    public AddAccount(MainAdmin mainAdmin) {
        this.mainAdmin = mainAdmin;
        initComponents();
        setTitle("Sign Up");
        initializeConnection();
        initializeRoleComboBox(); 
        setupJobTitleComboBox();
        populateGenderComboBox();
        initializeDepartmentComboBox();
        addEnterKeyListener(comboBoxGender);
        addEnterKeyListener(comboBoxJobTitle);
        addEnterKeyListener(comboBoxDepartment);
        addEnterKeyListener(comboBoxRole);

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
        String[] genderOptions = {"Male", "Female", "Other"};
        comboBoxGender.setModel(new DefaultComboBoxModel<>(genderOptions));
    }

    private void setupJobTitleComboBox() {
       jobTitles.clear(); 

       List<JobTitle> jobTitlesList = getJobTitles(); 
       jobTitles.addAll(jobTitlesList);  

       DefaultComboBoxModel<JobTitle> model = (DefaultComboBoxModel<JobTitle>) comboBoxJobTitle.getModel();
       model.removeAllElements(); 

       for (JobTitle jobTitle : jobTitles) {
           model.addElement(jobTitle);
       }

       comboBoxJobTitle.setModel(model);
       comboBoxJobTitle.setEditable(true);

       JTextField jobTitleTextField = (JTextField) comboBoxJobTitle.getEditor().getEditorComponent();
       jobTitleTextField.addKeyListener(new KeyAdapter() {
           @Override
           public void keyReleased(KeyEvent e) {
               String input = jobTitleTextField.getText();
               filterJobTitles(comboBoxJobTitle, input);
               comboBoxJobTitle.getEditor().setItem(input);
               jobTitleTextField.setCaretPosition(input.length());
           }

           @Override
           public void keyPressed(KeyEvent e) {
               if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                   String selectedItem = jobTitleTextField.getText();
                   JobTitle matchedJobTitle = null;

                   for (JobTitle jobTitle : jobTitles) {
                       if (jobTitle.getTitle().equalsIgnoreCase(selectedItem)) {
                           matchedJobTitle = jobTitle;
                           break;
                       }
                   }

                   if (matchedJobTitle != null) {
                       comboBoxJobTitle.setSelectedItem(matchedJobTitle);
                       jobTitleTextField.setText(matchedJobTitle.getTitle());
                   } else {
                       JOptionPane.showMessageDialog(AddAccount.this, 
                           "Please select a valid job title from the list.", 
                           "Invalid Input", 
                           JOptionPane.WARNING_MESSAGE);
                       jobTitleTextField.setText(""); 
                   }

                   comboBoxJobTitle.hidePopup();
                   comboBoxJobTitle.requestFocusInWindow();
               }
           }
       });
   }

        
    private void filterJobTitles(JComboBox<JobTitle> comboBox, String input) {
       DefaultComboBoxModel<JobTitle> model = (DefaultComboBoxModel<JobTitle>) comboBox.getModel();
       model.removeAllElements();

       if (input.isEmpty()) {
           for (JobTitle jobTitle : jobTitles) {
               model.addElement(jobTitle);
           }
       } else {
           for (JobTitle jobTitle : jobTitles) {
               if (jobTitle.getTitle().toLowerCase().contains(input.toLowerCase())) {
                   model.addElement(jobTitle);
               }
           }
       }

       if (model.getSize() > 0) {
           comboBox.showPopup();
       } else {
           comboBox.hidePopup();
       }
       comboBox.getEditor().setItem(input);
   }

    private List<JobTitle> getJobTitles() {
        List<JobTitle> jobTitlesList = new ArrayList<>();
        String selectJobTitlesSQL = "SELECT fld_job_title_id, fld_job_title FROM tbl_job_titles"; 
        try (PreparedStatement pstmt = connection.prepareStatement(selectJobTitlesSQL);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int jobTitleId = rs.getInt("fld_job_title_id"); 
                String jobTitleName = rs.getString("fld_job_title"); 
                jobTitlesList.add(new JobTitle(jobTitleId, jobTitleName));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching job titles: " + ex.getMessage());
        }
        return jobTitlesList;
    }
    
    private int getJobTitleId() {
        Object selectedItem = comboBoxJobTitle.getSelectedItem();

        if (selectedItem instanceof JobTitle selectedJobTitle) {
            return selectedJobTitle.getId(); 
        }

        return -1; 
    }

    
    private void initializeDepartmentComboBox() {
        List<Department> departments = getDepartments(); 
        DefaultComboBoxModel<Department> model = new DefaultComboBoxModel<>(); 

        for (Department department : departments) {
            model.addElement(department); 
        }
        comboBoxDepartment.setModel(model); 
        addEnterKeyListener(comboBoxDepartment); 

    }

    private List<Department> getDepartments() {
        List<Department> departments = new ArrayList<>();
        String selectDepartmentsSQL = "SELECT fld_department_id, fld_department_name FROM tbl_department"; 
        try (PreparedStatement pstmt = connection.prepareStatement(selectDepartmentsSQL);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("fld_department_id"); 
                String name = rs.getString("fld_department_name"); 
                departments.add(new Department(id, name));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching departments: " + ex.getMessage());
        }
        return departments;
    }

    public int getSelectedDepartmentId() {
        Department selectedDepartment = (Department) comboBoxDepartment.getSelectedItem();
        if (selectedDepartment != null) {
            return selectedDepartment.getId(); 
        }
        return -1; 
    }


    private void initializeRoleComboBox() {
        List<Role> roles = getRoles(); 
        DefaultComboBoxModel<Role> model = new DefaultComboBoxModel<>(); 

        for (Role role : roles) {
            model.addElement(role); 
        }
        comboBoxRole.setModel(model); 
        addEnterKeyListener(comboBoxRole); 
    }


    private List<Role> getRoles() {
        List<Role> roles = new ArrayList<>();
        String selectRolesSQL = "SELECT fld_role_id, fld_role_name FROM tbl_roles"; 
        try (PreparedStatement pstmt = connection.prepareStatement(selectRolesSQL);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int roleId = rs.getInt("fld_role_id"); 
                String roleName = rs.getString("fld_role_name"); 
                roles.add(new Role(roleId, roleName));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching roles: " + ex.getMessage());
        }
        return roles;
    }

    
    public int getSelectedRoleId() {
        Role selectedRole = (Role) comboBoxRole.getSelectedItem();
        if (selectedRole != null) {
            return selectedRole.getRoleId(); 
        }
        return -1; 
    }
    
    private void addEnterKeyListener(JComboBox<?> comboBox) {
        comboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Object selectedItem = comboBox.getEditor().getItem();

                    if (selectedItem != null && !selectedItem.toString().isEmpty()) {
                        comboBox.setSelectedItem(selectedItem);
                        comboBox.getEditor().setItem(selectedItem);
                    }
                    comboBox.hidePopup();
                    comboBox.requestFocusInWindow();
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

        jPanel7 = new javax.swing.JPanel();
        btnClose1 = new javax.swing.JButton();
        btnMin1 = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        imageLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        eMail = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        passWord = new javax.swing.JPasswordField();
        comboBoxRole = new javax.swing.JComboBox<>();
        comboBoxDepartment = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        comboBoxJobTitle = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        comboBoxGender = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        lastName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        firstName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        btnCreateAccount = new javax.swing.JButton();
        btnClear1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

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

        jLabel2.setFont(new java.awt.Font("Perpetua", 1, 28)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("EMPOWER");
        jPanel7.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 40, -1, 60));

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

        getContentPane().add(jPanel7, java.awt.BorderLayout.PAGE_START);

        jPanel1.setBackground(new java.awt.Color(240, 240, 240));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(240, 240, 240));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton2.setBackground(new java.awt.Color(170, 154, 182));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Add Image");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setFocusPainted(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 180, 100, -1));

        jPanel5.setBackground(new java.awt.Color(204, 204, 255));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imageLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel5.add(imageLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 120, 120));

        jPanel4.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 50, 120, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 36, 57));
        jLabel8.setText("Email");
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 430, 121, -1));

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
        jPanel4.add(eMail, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 450, 280, 45));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 36, 57));
        jLabel7.setText("Password");
        jPanel4.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 520, 121, -1));

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
        jPanel4.add(passWord, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 540, 280, 45));

        jPanel4.add(comboBoxRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 540, 240, 45));
        jPanel4.add(comboBoxDepartment, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 450, 240, 45));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 36, 57));
        jLabel11.setText("Role");
        jPanel4.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 520, 240, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 36, 57));
        jLabel10.setText("Department");
        jPanel4.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 430, 118, -1));
        jPanel4.add(comboBoxJobTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 360, 240, 45));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 36, 57));
        jLabel9.setText("Job Title");
        jPanel4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 340, 240, -1));

        comboBoxGender.setToolTipText("");
        jPanel4.add(comboBoxGender, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 280, 240, 45));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 36, 57));
        jLabel6.setText("Gender");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 260, 240, -1));

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
        jPanel4.add(lastName, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 360, 280, 45));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 36, 57));
        jLabel5.setText("Last Name");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 340, 120, -1));

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
        jPanel4.add(firstName, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 280, 280, 45));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 36, 57));
        jLabel4.setText("First Name");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 260, 319, -1));

        btnCreateAccount.setBackground(new java.awt.Color(0, 36, 57));
        btnCreateAccount.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btnCreateAccount.setForeground(new java.awt.Color(255, 255, 255));
        btnCreateAccount.setText("CREATE ACCOUNT");
        btnCreateAccount.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCreateAccount.setFocusPainted(false);
        btnCreateAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateAccountActionPerformed(evt);
            }
        });
        jPanel4.add(btnCreateAccount, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 650, 158, 40));

        btnClear1.setBackground(new java.awt.Color(136, 168, 200));
        btnClear1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnClear1.setForeground(new java.awt.Color(255, 255, 255));
        btnClear1.setText("Clear All");
        btnClear1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClear1.setFocusPainted(false);
        btnClear1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClear1ActionPerformed(evt);
            }
        });
        jPanel4.add(btnClear1, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 180, 100, -1));

        jLabel3.setBackground(new java.awt.Color(0, 36, 57));
        jLabel3.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 36, 57));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Create New Account");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, -1, -1));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 0, 770, 750));

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnMin1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMin1ActionPerformed
        this.setState(JFrame.ICONIFIED);
    }//GEN-LAST:event_btnMin1ActionPerformed

    private void btnClose1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose1ActionPerformed
        System.exit(0); // Close the application
    }//GEN-LAST:event_btnClose1ActionPerformed

    private void btnClear1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClear1ActionPerformed
        clearFields();
    }//GEN-LAST:event_btnClear1ActionPerformed

    private void btnCreateAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateAccountActionPerformed
        String firstNameInput = firstName.getText().trim();
        String lastNameInput = lastName.getText().trim();
        String emailInput = eMail.getText().trim();
        String passwordInput = new String(passWord.getPassword()).trim();
        String genderInput = (String) comboBoxGender.getSelectedItem();
        int jobTitleId = getJobTitleId();
        Department selectedDepartment = (Department) comboBoxDepartment.getSelectedItem();
        int departmentId = selectedDepartment != null ? selectedDepartment.getId() : -1;
        Role selectedRole = (Role) comboBoxRole.getSelectedItem();
        int roleId = selectedRole != null ? selectedRole.getRoleId() : -1;
        String imagePath = addImageToFolder();
        String dateOfEmployment = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        if (firstNameInput.isEmpty() || lastNameInput.isEmpty() || genderInput == null ||
            emailInput.isEmpty() || passwordInput.isEmpty() || imagePath == null ||
            jobTitleId == -1 || departmentId == -1 || roleId == -1) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields and select valid options.", "Missing Information", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isEmailDuplicate(emailInput)) {
            JOptionPane.showMessageDialog(this, "This email is already in use by another user. Please use a different email.", "Duplicate Email", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(emailInput)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (passwordInput.equals(PASSWORD_PLACEHOLDER)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid password.", "Invalid Password", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SignUpMethod signUpMethod = new SignUpMethod();
        boolean accountCreated = signUpMethod.createAccount(firstNameInput, lastNameInput, emailInput, passwordInput,
            genderInput, jobTitleId, departmentId, roleId,
            dateOfEmployment, imagePath);

        if (accountCreated) {
            JOptionPane.showMessageDialog(this, "Account created successfully!");
            mainAdmin.loadEmployeeData();
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error creating account. Please try again.");
        }
    }//GEN-LAST:event_btnCreateAccountActionPerformed

    private void firstNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_firstNameFocusLost
        if (firstName.getText().isEmpty()) {
            firstName.setText("Enter First Name");
            firstName.setForeground(new Color(126,138,150));
        }
    }//GEN-LAST:event_firstNameFocusLost

    private void firstNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_firstNameFocusGained
        if (firstName.getText().equals("Enter First Name")) {
            firstName.setText("");
            firstName.setForeground(new Color(142,122,69));
        }
    }//GEN-LAST:event_firstNameFocusGained

    private void lastNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lastNameFocusLost
        if (lastName.getText().isEmpty()) {
            lastName.setText("Enter Last Name");
            lastName.setForeground(new Color(205,186,136));
        }
    }//GEN-LAST:event_lastNameFocusLost

    private void lastNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lastNameFocusGained
        if (lastName.getText().equals("Enter Last Name")) {
            lastName.setText("");
            lastName.setForeground(new Color(142,122,69));
        }
    }//GEN-LAST:event_lastNameFocusGained

    private void passWordFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passWordFocusLost
        if (String.valueOf(passWord.getPassword()).isEmpty()) {
            passWord.setEchoChar((char)0);
            passWord.setText("Password");
            passWord.setForeground(new Color(205,186,136));
        }
    }//GEN-LAST:event_passWordFocusLost

    private void passWordFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passWordFocusGained
        if (String.valueOf(passWord.getPassword()).equals("Password")) {
            passWord.setText("");
            passWord.setEchoChar('*');
            passWord.setForeground(new Color(142,122,69));
        }
    }//GEN-LAST:event_passWordFocusGained

    private void eMailFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_eMailFocusLost
        if (eMail.getText().isEmpty()) {
            eMail.setText("Enter Email");
            eMail.setForeground(new Color(126,138,150));
        }
    }//GEN-LAST:event_eMailFocusLost

    private void eMailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_eMailFocusGained
        if (eMail.getText().equals("Enter Email")) {
            eMail.setText("");
            eMail.setForeground(new Color(142,122,69));
        }
    }//GEN-LAST:event_eMailFocusGained

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        selectImage();
    }//GEN-LAST:event_jButton2ActionPerformed

    private boolean isEmailDuplicate(String email) {
        String checkEmailSQL = "SELECT COUNT(*) FROM tbl_employees WHERE fld_email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(checkEmailSQL)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; 
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking email duplication: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false; 
    }

    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    
    private void resetImageSelection() {
        imageLocation = null;
        imageSelected = false;
        imageLabel.setIcon(null);  
    }

    
    public void selectImage() {
       JFileChooser fileChooser = new JFileChooser();
       fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg", "gif"));

       int response = fileChooser.showOpenDialog(null);
       if (response == JFileChooser.APPROVE_OPTION) {
           File selectedFile = fileChooser.getSelectedFile();
           imageLocation = selectedFile.getAbsolutePath();
           imageSelected = true;
           displayImage(imageLocation);
       } else {
           resetImageSelection(); 
       }
    }

     private void displayImage(String imageLocation) {
        if (imageLocation != null) {
            ImageIcon originalIcon = new ImageIcon(imageLocation);
            Image originalImage = originalIcon.getImage();
            
            int width = 120;
            int height = 120;
            
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            imageLabel.setIcon(scaledIcon);
        }
    }
    
    private String addImageToFolder() {
       if (!imageSelected) {
           return null;
       }

       String[] acceptedImageExtensions = {".jpg", ".jpeg", ".png"};
       String fileExtension = imageLocation.substring(imageLocation.lastIndexOf(".")).toLowerCase();
       boolean isImage = Arrays.asList(acceptedImageExtensions).contains(fileExtension);

       if (!isImage) {
           JOptionPane.showMessageDialog(this, "Please select a valid image file (jpg, jpeg, png).", "Invalid File Type", JOptionPane.ERROR_MESSAGE);
           resetImageSelection(); 
           return null;
       }

       String destinationFolder = "src/Users/";
       String newFileName = "employee_" + System.currentTimeMillis() + fileExtension;
       String destinationPath = destinationFolder + newFileName;

       File sourceFile = new File(imageLocation);
       File destinationFile = new File(destinationPath);

       try {
           destinationFile.getParentFile().mkdirs(); 
           Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
           return destinationPath; 
       } catch (IOException e) {
           JOptionPane.showMessageDialog(this, "Error adding image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
           resetImageSelection(); 
           return null;
       }
   }

    private void clearFields() {
        firstName.setText("");
        lastName.setText("");
        eMail.setText("");
        passWord.setText("");
        comboBoxGender.setSelectedIndex(-1);
        comboBoxJobTitle.setSelectedIndex(-1);
        comboBoxDepartment.setSelectedIndex(-1);
        comboBoxRole.setSelectedIndex(-1);
        imageLabel.setIcon(null); 
        imageLocation = ""; 
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
            java.util.logging.Logger.getLogger(AddAccount.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddAccount.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddAccount.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddAccount.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            MainAdmin mainAdmin1 = new MainAdmin();
            new AddAccount(mainAdmin1).setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear1;
    private javax.swing.JButton btnClose1;
    private javax.swing.JButton btnCreateAccount;
    private javax.swing.JButton btnMin1;
    private javax.swing.JComboBox<Department> comboBoxDepartment;
    private javax.swing.JComboBox<String> comboBoxGender;
    private javax.swing.JComboBox<JobTitle> comboBoxJobTitle;
    private javax.swing.JComboBox<Role> comboBoxRole;
    private javax.swing.JTextField eMail;
    private javax.swing.JTextField firstName;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField lastName;
    private javax.swing.JPasswordField passWord;
    // End of variables declaration//GEN-END:variables
}
