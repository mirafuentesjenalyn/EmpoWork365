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
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

/**
 *
 * @author jenal
 */
public final class SignUp extends javax.swing.JFrame {
    private Connection connection;    
    private static final List<String> jobTitles = new ArrayList<>();
    private String imageLocation;
    private boolean imageSelected = false;
    
    

    /**
     * Creates new form SignUp
     */
    public SignUp() {
        initComponents();
        setTitle("Sign Up");
        initializeConnection();
        populateJobTitles();
        initializeRoleComboBox(); 
        setupComboBoxes();
        setupJobTitleComboBox();
        initializeDepartmentComboBox();
    }

        
    private void setupComboBoxes() {
        populateGenderComboBox();
    }

    private void populateGenderComboBox() {
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String selectGenderSQL = "SELECT DISTINCT fld_gender FROM tbl_users"; // Adjust table/field as needed
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

                comboBoxGender.setModel(new DefaultComboBoxModel<>(genderList.toArray(new String[0])));
                return null;
            }
        };
        worker.execute();
    }

    
    private void setupJobTitleComboBox() {
        comboBoxJobTitle.setModel(new javax.swing.DefaultComboBoxModel<>(jobTitles.toArray(new String[0])));
        comboBoxJobTitle.setEditable(true);

        JTextField jobTitleTextField = (JTextField) comboBoxJobTitle.getEditor().getEditorComponent();
        jobTitleTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String input = jobTitleTextField.getText();  
                filterJobTitles(comboBoxJobTitle, input);  
                jobTitleTextField.setText(input);
                comboBoxJobTitle.getEditor().setItem(input);  
                jobTitleTextField.setCaretPosition(input.length());  
            }
        });
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
        
    private void filterJobTitles(JComboBox<String> comboBox, String input) {
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboBox.getModel();
        model.removeAllElements();  

        for (String jobTitle : jobTitles) {
            if (jobTitle.toLowerCase().contains(input.toLowerCase())) {
                model.addElement(jobTitle);
            }
        }

        if (model.getSize() > 0) {
            comboBox.showPopup();  
        } else {
            comboBox.hidePopup(); 
        }
    }
    
    private void populateJobTitles() {
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                jobTitles.clear(); 
                String selectJobTitlesSQL = "SELECT fld_job_title FROM tbl_job_titles"; 
                try (PreparedStatement pstmt = connection.prepareStatement(selectJobTitlesSQL);
                     ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String jobTitle = rs.getString("fld_job_title");
                        publish(jobTitle); 
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error fetching job titles: " + ex.getMessage());
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                jobTitles.addAll(chunks); 
                comboBoxJobTitle.setModel(new DefaultComboBoxModel<>(jobTitles.toArray(new String[0])));
            }
        };
        worker.execute(); 
    }

    
    private void initializeDepartmentComboBox() {
        List<Department> departments = getDepartments(); 
        DefaultComboBoxModel<Department> model = new DefaultComboBoxModel<>(); 

        for (Department department : departments) {
            model.addElement(department); 
        }
        comboBoxDepartment.setModel(model); 
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
        jLabel3 = new javax.swing.JLabel();
        btnSignIn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        btnCreateAccount = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        firstName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        lastName = new javax.swing.JTextField();
        comboBoxGender = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        comboBoxJobTitle = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        comboBoxDepartment = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        comboBoxRole = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        imageLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        eMail = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        passWord = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(17, 94, 94));

        jPanel1.setBackground(new java.awt.Color(8, 127, 127));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Have Account?");

        btnSignIn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSignIn.setForeground(new java.awt.Color(255, 255, 255));
        btnSignIn.setText("SIGN IN");
        btnSignIn.setBorder(null);
        btnSignIn.setBorderPainted(false);
        btnSignIn.setContentAreaFilled(false);
        btnSignIn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSignIn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSignIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSignInActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 204, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("SIGN UP");

        btnCreateAccount.setBackground(new java.awt.Color(185, 230, 230));
        btnCreateAccount.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btnCreateAccount.setForeground(new java.awt.Color(65, 126, 118));
        btnCreateAccount.setText("CREATE ACCOUNT");
        btnCreateAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateAccountActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(8, 127, 127));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("First Name");

        firstName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        firstName.setForeground(new java.awt.Color(153, 204, 188));
        firstName.setText("Enter First Name");
        firstName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                firstNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                firstNameFocusLost(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Last Name");

        lastName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lastName.setForeground(new java.awt.Color(153, 204, 188));
        lastName.setText("Enter Last Name");
        lastName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                lastNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                lastNameFocusLost(evt);
            }
        });

        comboBoxGender.setBackground(new java.awt.Color(229, 255, 237));
        comboBoxGender.setToolTipText("");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Gender");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Job Title");

        comboBoxJobTitle.setBackground(new java.awt.Color(229, 255, 237));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Department");

        comboBoxDepartment.setBackground(new java.awt.Color(229, 255, 237));
        comboBoxDepartment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxDepartmentActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Role");

        comboBoxRole.setBackground(new java.awt.Color(229, 255, 237));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 119, Short.MAX_VALUE))
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(13, 13, 13))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(comboBoxRole, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(comboBoxDepartment, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(comboBoxJobTitle, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(comboBoxGender, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lastName, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(firstName))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel4)
                .addGap(6, 6, 6)
                .addComponent(firstName, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(6, 6, 6)
                .addComponent(lastName, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel6)
                .addGap(6, 6, 6)
                .addComponent(comboBoxGender, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(comboBoxJobTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(comboBoxDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxRole, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(8, 127, 127));

        jButton2.setText("Add Image");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel5.add(imageLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, 133, 108));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Email");

        eMail.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        eMail.setForeground(new java.awt.Color(153, 204, 188));
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

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Password");

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
        passWord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passWordActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eMail, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(passWord, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(jButton2))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addGap(6, 6, 6)
                .addComponent(eMail, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel7)
                .addGap(12, 12, 12)
                .addComponent(passWord, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(65, 65, 65)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(286, 286, 286)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(50, 50, 50))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel3)
                        .addGap(6, 6, 6)
                        .addComponent(btnSignIn, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnCreateAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(241, 241, 241))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addComponent(btnCreateAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(btnSignIn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30))
        );

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
                        .addGap(385, 385, 385)
                        .addComponent(jLabel1))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(120, 120, 120)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(150, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1)
                .addGap(25, 25, 25)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(67, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(959, 938));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void lastNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lastNameFocusGained
        if (lastName.getText().equals("Enter Last Name")) {
            lastName.setText("");
            lastName.setForeground(new Color(142,122,69));
        }
    }//GEN-LAST:event_lastNameFocusGained

    private void lastNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lastNameFocusLost
        if (lastName.getText().isEmpty()) {
            lastName.setText("Enter Last Name");
            lastName.setForeground(new Color(205,186,136));
        }
    }//GEN-LAST:event_lastNameFocusLost

    private void passWordFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passWordFocusGained
        if (String.valueOf(passWord.getPassword()).equals("Password")) {
            passWord.setText("");
            passWord.setEchoChar('*');
            passWord.setForeground(new Color(142,122,69));
        }
    }//GEN-LAST:event_passWordFocusGained

    private void passWordFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passWordFocusLost
        if (String.valueOf(passWord.getPassword()).isEmpty()) {
            passWord.setEchoChar((char)0);
            passWord.setText("Password");
            passWord.setForeground(new Color(205,186,136));
        }
    }//GEN-LAST:event_passWordFocusLost

    private void passWordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passWordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passWordActionPerformed

    private void btnCreateAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateAccountActionPerformed
       String firstNameInput = firstName.getText().trim();
       String lastNameInput = lastName.getText().trim();
       String emailInput = eMail.getText().trim();
       String passwordInput = new String(passWord.getPassword()).trim();
       String jobTitleInput = (String) comboBoxJobTitle.getSelectedItem();
       String genderInput = (String) comboBoxGender.getSelectedItem();

       Department selectedDepartment = (Department) comboBoxDepartment.getSelectedItem(); // Get selected department
       int departmentId = selectedDepartment != null ? selectedDepartment.getId() : -1; // Use department ID
       Role selectedRole = (Role) comboBoxRole.getSelectedItem(); // Get selected role
       int roleId = selectedRole != null ? selectedRole.getRoleId() : -1; // Use role ID
       String imagePath = addImageToFolder(); // Handle image upload

       // Set the date of employment to the current date if it's empty
       String dateOfEmployment = new SimpleDateFormat("yyyy-MM-dd").format(new Date()); // Use current date in YYYY-MM-DD format

       if (firstNameInput.isEmpty() || lastNameInput.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty() || imagePath == null) {
           JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
           return;
       }

       // Create an instance of SignUpMethod
       SignUpMethod signUpMethod = new SignUpMethod();
       boolean accountCreated = signUpMethod.createAccount(firstNameInput, lastNameInput, emailInput, passwordInput, 
                                                           genderInput, jobTitleInput, departmentId, roleId, 
                                                           dateOfEmployment, imagePath);

       if (accountCreated) {
           JOptionPane.showMessageDialog(this, "Account created successfully!");
       } else {
           JOptionPane.showMessageDialog(this, "Error creating account. Please try again.");
       }
    }//GEN-LAST:event_btnCreateAccountActionPerformed

    private void btnSignInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSignInActionPerformed
        LoginForm signIn = new LoginForm();
        signIn.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnSignInActionPerformed

    private void firstNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_firstNameFocusGained
        if (firstName.getText().equals("Enter First Name")) {
            firstName.setText("");
            firstName.setForeground(new Color(142,122,69));
        }
    }//GEN-LAST:event_firstNameFocusGained

    private void firstNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_firstNameFocusLost
        if (firstName.getText().isEmpty()) {
            firstName.setText("Enter First Name");
            firstName.setForeground(new Color(205,186,136));
        }
    }//GEN-LAST:event_firstNameFocusLost

    private void eMailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_eMailFocusGained
        if (eMail.getText().equals("Enter Email")) {
            eMail.setText("");
            eMail.setForeground(new Color(142,122,69));
        }
    }//GEN-LAST:event_eMailFocusGained

    private void eMailFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_eMailFocusLost
        if (eMail.getText().isEmpty()) {
            eMail.setText("Enter Email");
            eMail.setForeground(new Color(205,186,136));
        }
    }//GEN-LAST:event_eMailFocusLost

    private void ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        selectImage();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void comboBoxDepartmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxDepartmentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboBoxDepartmentActionPerformed

    public void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg", "gif"));
    
        int response = fileChooser.showOpenDialog(null);
        if (response == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imageLocation = selectedFile.getAbsolutePath();
            imageSelected = true;
            displayImage(imageLocation); 
        }
    }
    
     private void displayImage(String imagePath) {
        if (imageLocation != null) {
            ImageIcon originalIcon = new ImageIcon(imageLocation);
            Image originalImage = originalIcon.getImage();
            
            int width = 100;
            int height = 100;
            
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            imageLabel.setIcon(scaledIcon);
        }
    }
    
    private String addImageToFolder() {
        if (!imageSelected) {
            JOptionPane.showMessageDialog(this, "Please select an image first.");
            return null;
        }

        String[] acceptedImageExtensions = {".jpg", ".jpeg", ".png"};
        String fileExtension = imageLocation.substring(imageLocation.lastIndexOf(".")).toLowerCase();
        boolean isImage = Arrays.asList(acceptedImageExtensions).contains(fileExtension);
        if (!isImage) {
            JOptionPane.showMessageDialog(this, "Please select a valid image file (jpg, jpeg, png, gif, bmp).", "Invalid File Type", JOptionPane.ERROR_MESSAGE);
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
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding image: " + e.getMessage());
            return null;
        }
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
            java.util.logging.Logger.getLogger(SignUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SignUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SignUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SignUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SignUp().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreateAccount;
    private javax.swing.JButton btnSignIn;
    private javax.swing.JComboBox<Department> comboBoxDepartment;
    private javax.swing.JComboBox<String> comboBoxGender;
    private javax.swing.JComboBox<String> comboBoxJobTitle;
    private javax.swing.JComboBox<Role> comboBoxRole;
    private javax.swing.JTextField eMail;
    private javax.swing.JTextField firstName;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JTextField lastName;
    private javax.swing.JPasswordField passWord;
    // End of variables declaration//GEN-END:variables
}
