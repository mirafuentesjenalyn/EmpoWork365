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
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

/**
 *
 * @author jenal
 */
public final class EditEmployeeDetails extends javax.swing.JFrame {
    private final MainAdmin mainAdmin;
    private Connection connection;    
    private final List<JobTitle> jobTitles = new ArrayList<>();
    private final List<Role> roles = new ArrayList<>();
    private final List<Department> departments = new ArrayList<>();
    private int employeeId; 
    private String imagePath; 
    
    /**
     * Creates new form SignUp
     * @param mainAdmin
     */
    public EditEmployeeDetails(MainAdmin mainAdmin) {
        this.mainAdmin = mainAdmin;
        
        initComponents();
        this.employeeId = -1;
        this.imagePath = "";

        setTitle("Edit Employee");
        initializeConnection();
        initializeRoleComboBox(); 
        setupJobTitleComboBox();
        populateGenderComboBox();
        initializeDepartmentComboBox();

        loadEmployeeData();
        setImageLabel(imagePath);

    }

     public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId; 
        loadEmployeeData();
    }

    private void initializeConnection() {
        sqlConnector dbConnector = new sqlConnector(); 
        try {
            connection = dbConnector.createConnection(); 
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateGenderComboBox() {
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String selectGenderSQL = "SELECT DISTINCT fld_gender FROM tbl_employees"; // Adjust table/field as needed
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
    
    private void setupJobTitleComboBox() {
        List<JobTitle> jobTitlesList = getJobTitles(); 
        jobTitles.addAll(jobTitlesList);  
        DefaultComboBoxModel<JobTitle> model = new DefaultComboBoxModel<>();

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
                        // Show a message indicating the input is invalid
                        JOptionPane.showMessageDialog(EditEmployeeDetails.this, 
                            "Please select a valid job title from the list.", 
                            "Invalid Input", 
                            JOptionPane.WARNING_MESSAGE);
                        jobTitleTextField.setText(""); // Clear the field
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
    
    public int getSelectedJobTitleId() {
        Object selectedItem = comboBoxJobTitle.getSelectedItem();

        if (selectedItem instanceof JobTitle) {
            return ((JobTitle) selectedItem).getId(); // Valid JobTitle object
        } else {
            // Handle the case when the selected item is a String (e.g., manual input)
            String inputJobTitle = selectedItem.toString();
            JobTitle matchedJobTitle = null;

            for (JobTitle jobTitle : jobTitles) {
                if (jobTitle.getTitle().equalsIgnoreCase(inputJobTitle)) {
                    matchedJobTitle = jobTitle;
                    break;
                }
            }

            if (matchedJobTitle != null) {
                return matchedJobTitle.getId();  // Return matched job title ID
            } else {
                // If no match is found, show a warning message and return an invalid ID
                JOptionPane.showMessageDialog(this, "Please select a valid job title.", "Invalid Job Title", JOptionPane.WARNING_MESSAGE);
                return -1;  // Invalid job title ID
            }
        }
    }
        
   private void initializeDepartmentComboBox() {
        List<Department> dept = getDepartments(); 
        DefaultComboBoxModel<Department> model = new DefaultComboBoxModel<>(); 

        for (Department department : dept) {
            model.addElement(department); 
        }
        comboBoxDepartment.setModel(model); 
        addEnterKeyListener(comboBoxDepartment); 
    }

    
     private List<Department> getDepartments() {
        List<Department> departmen = new ArrayList<>();
        String selectDepartmentsSQL = "SELECT fld_department_id, fld_department_name FROM tbl_department"; 
        try (PreparedStatement pstmt = connection.prepareStatement(selectDepartmentsSQL);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("fld_department_id"); 
                String name = rs.getString("fld_department_name"); 
                departmen.add(new Department(id, name));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching departments: " + ex.getMessage());
        }
        return departmen;
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
    
    private void setImageLabel(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            displayImage(imagePath); 
        }
    }
    
    public boolean isEmailDuplicate(String email) {
        String checkEmailQuery = "SELECT COUNT(*) FROM tbl_employees WHERE fld_email = ? AND fld_employee_id != ?";
        try (PreparedStatement pstmt = connection.prepareStatement(checkEmailQuery)) {
            pstmt.setString(1, email);
            pstmt.setInt(2, employeeId); 
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error checking duplicate email: " + ex.getMessage());
        }
        return false;
    }

 
    private void loadEmployeeData() {
        String selectEmployeeSQL = "SELECT e.fld_first_name, e.fld_last_name, e.fld_email, e.fld_password, "
                                  + "e.fld_gender, e.fld_job_title_id, jt.fld_job_title, "
                                  + "e.fld_department_id, e.fld_role_id, e.fld_image_path, "
                                  + "d.fld_department_name, r.fld_role_name "
                                  + "FROM tbl_employees e "
                                  + "JOIN tbl_department d ON e.fld_department_id = d.fld_department_id "
                                  + "JOIN tbl_roles r ON e.fld_role_id = r.fld_role_id "
                                  + "JOIN tbl_job_titles jt ON e.fld_job_title_id = jt.fld_job_title_id "
                                  + "WHERE e.fld_employee_id = ?"; 

        try (PreparedStatement pstmt = connection.prepareStatement(selectEmployeeSQL)) {
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery(); 

            if (rs.next()) { 
                String firstNameFromDB = rs.getString("fld_first_name");
                String lastNameFromDB = rs.getString("fld_last_name");
                firstName.setText(firstNameFromDB); 
                lastName.setText(lastNameFromDB); 
                eMail.setText(rs.getString("fld_email")); 
                passWord.setText(rs.getString("fld_password")); 

                comboBoxJobTitle.setSelectedItem(new JobTitle(rs.getInt("fld_job_title_id"), rs.getString("fld_job_title")));
                comboBoxDepartment.setSelectedItem(new Department(rs.getInt("fld_department_id"), rs.getString("fld_department_name")));
                comboBoxRole.setSelectedItem(new Role(rs.getInt("fld_role_id"), rs.getString("fld_role_name")));
                
                // Set the image path and display the image
                imagePath = rs.getString("fld_image_path");
                setImageLabel(imagePath);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading employee data: " + ex.getMessage());
        }
    }
    
    private boolean isEmailDuplicate(String email, int employeeId) {
        String query = "SELECT COUNT(*) FROM tbl_employees WHERE fld_email = ? AND fld_employee_id <> ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setInt(2, employeeId); 

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // Return true if the email exists and is not the same as the current employee
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error checking duplicate email: " + ex.getMessage());
        }
        return false; // Return false if no duplicate found
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
        jPanel4 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        imageLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        eMail = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        passWord = new javax.swing.JPasswordField();
        btnClear = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        firstName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        lastName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        comboBoxGender = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        comboBoxJobTitle = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        comboBoxDepartment = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        comboBoxRole = new javax.swing.JComboBox<>();
        btnConfirmEdit = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
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

        jPanel4.setBackground(new java.awt.Color(240, 240, 240));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        jPanel4.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 180, 100, -1));

        jPanel5.setBackground(new java.awt.Color(204, 204, 255));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imageLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel5.add(imageLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 120, 120));

        jPanel4.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 50, 120, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Email");
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 240, 121, -1));

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
        jPanel4.add(eMail, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 270, 246, 45));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Password");
        jPanel4.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 330, 121, -1));

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
        jPanel4.add(passWord, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 360, 246, 45));

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
        jPanel4.add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 180, 100, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("First Name");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 420, 239, -1));

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
        jPanel4.add(firstName, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 450, 246, 45));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Last Name");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 510, 120, -1));

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
        jPanel4.add(lastName, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 540, 246, 45));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Gender");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 240, 239, -1));

        comboBoxGender.setToolTipText("");
        jPanel4.add(comboBoxGender, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 270, 246, 45));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Job Title");
        jPanel4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 330, 239, -1));

        jPanel4.add(comboBoxJobTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 360, 246, 45));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Department");
        jPanel4.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 420, 239, -1));
        jPanel4.add(comboBoxDepartment, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 450, 246, 45));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Role");
        jPanel4.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 510, 246, -1));

        jPanel4.add(comboBoxRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 540, 246, 45));

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
        jPanel4.add(btnConfirmEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 640, 158, 40));

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
        jPanel4.add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 640, 158, 40));

        jLabel2.setBackground(new java.awt.Color(0, 36, 57));
        jLabel2.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 36, 57));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Edit Employee Details");
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, 224, -1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 816, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

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

        getContentPane().add(jPanel7, java.awt.BorderLayout.PAGE_START);

        setSize(new java.awt.Dimension(792, 895));
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
        String email = eMail.getText();

        if (firstName.getText().trim().isEmpty() || lastName.getText().trim().isEmpty() || 
            email.trim().isEmpty() || String.valueOf(passWord.getPassword()).trim().isEmpty() ||
            comboBoxGender.getSelectedItem() == null ||
            comboBoxJobTitle.getSelectedItem() == null || 
            comboBoxDepartment.getSelectedItem() == null || 
            comboBoxRole.getSelectedItem() == null) { 
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Empty Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

         // Check for duplicate email
         if (isEmailDuplicate(email, employeeId)) {
             JOptionPane.showMessageDialog(this, "This email is already associated with another employee.", "Duplicate Email", JOptionPane.ERROR_MESSAGE);
             return;
         }

         // Update query - fix SQL syntax
         String updateUserSQL = "UPDATE tbl_employees SET fld_first_name = ?, fld_last_name = ?, "
                              + "fld_email = ?, fld_password = ?, fld_gender = ?, fld_job_title_id = ?, "
                              + "fld_department_id = ?, fld_role_id = ?, fld_image_path = ? "
                              + "WHERE fld_employee_id = ?"; 
         try (PreparedStatement pstmt = connection.prepareStatement(updateUserSQL)) {
            pstmt.setString(1, firstName.getText()); 
            pstmt.setString(2, lastName.getText());  
            pstmt.setString(3, email);   
            pstmt.setString(4, String.valueOf(passWord.getPassword()));
            pstmt.setString(5, comboBoxGender.getSelectedItem().toString());
            pstmt.setInt(6, getSelectedJobTitleId());
            pstmt.setInt(7, getSelectedDepartmentId());
            pstmt.setInt(8, getSelectedRoleId()); 
            pstmt.setString(9, imagePath); 
            pstmt.setInt(10, getUserIdFromEmployeeId(employeeId));

             // Execute update
             int rowsUpdated = pstmt.executeUpdate(); 
             if (rowsUpdated > 0) { 
                 JOptionPane.showMessageDialog(this, "Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                 if (mainAdmin != null) {
                     mainAdmin.loadEmployeeData(); 
                     mainAdmin.getAttendanceData();
                 }
                 dispose(); 
             } else {
                 JOptionPane.showMessageDialog(this, "No records updated. Please check the data.", "Update Failed", JOptionPane.WARNING_MESSAGE);
             }
         } catch (SQLException e) {
             JOptionPane.showMessageDialog(this, "Error updating employee: " + e.getMessage());
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

            try {
                updateEmployeeImageInDatabase(imagePath, employeeId);
            } catch (IOException ex) {
                Logger.getLogger(EditEmployeeDetails.class.getName()).log(Level.SEVERE, null, ex);
            }
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

    private int getUserIdFromEmployeeId(int employeeId) {
        String query = "SELECT fld_employee_id FROM tbl_employees WHERE fld_employee_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("fld_employee_id");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving employee ID: " + e.getMessage());
        }
        return -1; 
    }
    
    private void resetImageSelection() {
        imagePath = null;
        imageLabel.setIcon(new ImageIcon("src/Users/user.png"));  
    }

    
    private String updateEmployeeImageInDatabase(String imagePath, int userId) throws IOException {
        String updateImageSQL = "UPDATE tbl_employees SET fld_image_path = ? WHERE fld_employee_id = ?";
        String[] acceptedImageExtensions = {".jpg", ".jpeg", ".png"};
       String fileExtension = imagePath.substring(imagePath.lastIndexOf(".")).toLowerCase();
       boolean isImage = Arrays.asList(acceptedImageExtensions).contains(fileExtension);

       if (!isImage) {
           JOptionPane.showMessageDialog(this, "Please select a valid image file (jpg, jpeg, png).", "Invalid File Type", JOptionPane.ERROR_MESSAGE);
           resetImageSelection(); 
       }

       String destinationFolder = "src/Users/";
       String newFileName = "employee_" + System.currentTimeMillis() + fileExtension;
       String destinationPath = destinationFolder + newFileName;

       File sourceFile = new File(imagePath);
       File destinationFile = new File(destinationPath);
        try (PreparedStatement pstmt = connection.prepareStatement(updateImageSQL)) {
            pstmt.setString(1, imagePath);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            destinationFile.getParentFile().mkdirs(); 
            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
           return destinationPath; 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating employee image: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
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
            imageLabel.setIcon(new ImageIcon("src/Users/user.png")); 
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
        comboBoxJobTitle.setSelectedIndex(-1);
        comboBoxDepartment.setSelectedIndex(-1);
        comboBoxRole.setSelectedIndex(-1);
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
            java.util.logging.Logger.getLogger(EditEmployeeDetails.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditEmployeeDetails.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditEmployeeDetails.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditEmployeeDetails.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainAdmin mainAdmin = new MainAdmin(); 
                new EditEmployeeDetails(mainAdmin).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnClose1;
    private javax.swing.JButton btnConfirmEdit;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField lastName;
    private javax.swing.JPasswordField passWord;
    // End of variables declaration//GEN-END:variables
}
