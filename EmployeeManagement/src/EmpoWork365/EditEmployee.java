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
import javax.imageio.ImageIO;
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
public final class EditEmployee extends javax.swing.JFrame {
    private final MainAdmin mainAdmin;
    private Connection connection;    
    private static final List<JobTitle> jobTitles = new ArrayList<>();
    private String currentUserEmail;
    private String imageLocation;
    private int employeeId; 
    private String imagePath; 
    private boolean isAdminContext;
    private int currentUserId;
    
    /**
     * Creates new form SignUp
     * @param mainAdmin
     * @param isAdminContext
     * @param currentUserId
     */
    public EditEmployee(MainAdmin mainAdmin, boolean isAdminContext, int currentUserId) {
        this.mainAdmin = mainAdmin;
            this.isAdminContext = isAdminContext;
                this.currentUserId = currentUserId;
        initComponents();
        this.employeeId = -1;
        setTitle("Edit Employee");
        initializeConnection();
        initializeRoleComboBox(); 
        setupJobTitleComboBox();
        populateGenderComboBox();
        initializeDepartmentComboBox();

        loadEmployeeData();
        setImageLabel(imagePath);

    }
    public void setUserDetails(String firstname, String lastname, String gender, 
                               String jobtitle, String departmentName, 
                               String roleName, String email, 
                               String password, String imagepath) {
        firstName.setText(firstname);
        lastName.setText(lastname);
        comboBoxGender.setSelectedItem(gender);
        comboBoxJobTitle.setSelectedItem(jobtitle);
        comboBoxDepartment.setSelectedItem(departmentName);
        comboBoxRole.setSelectedItem(roleName);
        eMail.setText(email);
        passWord.setText(password); 
        this.imagePath = imagepath; 
        setImageLabel(imagepath); 
        
        currentUserEmail = email;
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
                        JOptionPane.showMessageDialog(EditEmployee.this, 
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
    
    private void setImageLabel(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            displayImage(imagePath); 
        }
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

                comboBoxGender.setSelectedItem(rs.getString("fld_gender"));

                comboBoxJobTitle.setSelectedItem(new JobTitle(rs.getInt("fld_job_title_id"), rs.getString("fld_job_title")));

                comboBoxDepartment.setSelectedItem(new Department(rs.getInt("fld_department_id"), rs.getString("fld_department_name")));

                int roleId = rs.getInt("fld_role_id");
                for (int i = 0; i < comboBoxRole.getItemCount(); i++) {
                    Role role = (Role) comboBoxRole.getItemAt(i);
                    if (role.getRoleId() == roleId) {
                        comboBoxRole.setSelectedItem(role);
                        break;
                    }
                }
                String imagePath = rs.getString("fld_image_path");
                this.imageLocation = imagePath;
                setImageLabel(imagePath); 
            } 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading employee data: " + e.getMessage());
        }
    }
    
    private boolean isEmailDuplicateForEmployee(String email, int employeeId) {
        String query = "SELECT fld_employee_id FROM tbl_employees WHERE fld_email = ? AND fld_employee_id != ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setInt(2, employeeId); 
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking for duplicate email: " + e.getMessage());
        }
        return false;
    }

    private boolean isEmailDuplicateForLoggedInUser(String email, int currentUserId) {
        if (email.equals(currentUserEmail)) {
            return false;
        }

        String query = "SELECT fld_employee_id FROM tbl_employees WHERE fld_email = ? AND fld_employee_id != ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setInt(2, currentUserId); 
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking for duplicate email: " + e.getMessage());
        }
        return false;
    }

    private boolean isDataUnchanged(int targetEmployeeId) {
        String query = "SELECT fld_first_name, fld_last_name, fld_email, fld_password, fld_gender, "
                     + "fld_job_title_id, fld_department_id, fld_role_id, fld_image_path FROM tbl_employees WHERE fld_employee_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, targetEmployeeId); 
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String dbFirstName = rs.getString("fld_first_name");
                String dbLastName = rs.getString("fld_last_name");
                String dbEmail = rs.getString("fld_email");
                String dbPassword = rs.getString("fld_password");
                String dbGender = rs.getString("fld_gender");
                int dbJobTitleId = rs.getInt("fld_job_title_id");
                int dbDepartmentId = rs.getInt("fld_department_id");
                int dbRoleId = rs.getInt("fld_role_id");
                String dbImagePath = rs.getString("fld_image_path");
                
                boolean unchanged = (
                    firstName.getText().equals(dbFirstName) &&
                    lastName.getText().equals(dbLastName) &&
                    eMail.getText().equals(dbEmail) &&
                    String.valueOf(passWord.getPassword()).equals(dbPassword) &&
                    comboBoxGender.getSelectedItem() != null &&
                    comboBoxGender.getSelectedItem().toString().equals(dbGender) &&
                    comboBoxJobTitle.getSelectedItem() != null &&
                    ((JobTitle) comboBoxJobTitle.getSelectedItem()).getId() == dbJobTitleId &&
                    comboBoxDepartment.getSelectedItem() != null &&
                    ((Department) comboBoxDepartment.getSelectedItem()).getId() == dbDepartmentId &&
                    comboBoxRole.getSelectedItem() != null &&
                    ((Role) comboBoxRole.getSelectedItem()).getRoleId() == dbRoleId &&
                    (dbImagePath != null && dbImagePath.equals(imageLocation)) || (dbImagePath == null && imageLocation == null)
                );
                return unchanged;
            }
        } catch (SQLException e) {
        }
        return false; 
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
        jLabel2 = new javax.swing.JLabel();
        btnConfirmEdit = new javax.swing.JButton();
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
        btnClear = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(17, 94, 94));

        jPanel1.setBackground(new java.awt.Color(8, 127, 127));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 204, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("EDIT PERSONAL DETAILS");

        btnConfirmEdit.setBackground(new java.awt.Color(185, 230, 230));
        btnConfirmEdit.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btnConfirmEdit.setForeground(new java.awt.Color(65, 126, 118));
        btnConfirmEdit.setText("CONFIRM");
        btnConfirmEdit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfirmEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmEditActionPerformed(evt);
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
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton2.setText("Add Image");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 126, -1, -1));

        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imageLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel5.add(imageLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 140, 120));

        jPanel4.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(47, 0, -1, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Email");
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 165, 121, -1));

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
        jPanel4.add(eMail, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 191, 246, 45));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Password");
        jPanel4.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 248, 121, -1));

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
        jPanel4.add(passWord, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 280, 246, 45));

        btnClear.setBackground(new java.awt.Color(71, 146, 146));
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("Clear All");
        btnClear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        jPanel4.add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(138, 370, 100, -1));

        btnCancel.setBackground(new java.awt.Color(185, 230, 230));
        btnCancel.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btnCancel.setForeground(new java.awt.Color(65, 126, 118));
        btnCancel.setText("CANCEL");
        btnCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(88, 88, 88)
                .addComponent(btnConfirmEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(87, 87, 87))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(65, 65, 65)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConfirmEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(76, 76, 76))
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
                .addContainerGap(45, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(959, 938));
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

        // Check for empty fields
        if (firstName.getText().trim().isEmpty() || lastName.getText().trim().isEmpty() || 
            email.trim().isEmpty() || String.valueOf(passWord.getPassword()).trim().isEmpty() ||
            comboBoxGender.getSelectedItem() == null ||
            comboBoxJobTitle.getSelectedItem() == null || 
            comboBoxDepartment.getSelectedItem() == null || 
            comboBoxRole.getSelectedItem() == null) { 
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Empty Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check for email duplication based on context
        if (isAdminContext) {
            if (isEmailDuplicateForEmployee(email, employeeId)) {
                JOptionPane.showMessageDialog(this, "Email already exists. Please use a different email.");
                return; 
            }
        } else {
            // Use the currentUserId to check for duplication for the logged-in user
            if (isEmailDuplicateForLoggedInUser(email, currentUserId)) {
                JOptionPane.showMessageDialog(this, "Email already exists. Please use a different email.");
                return; 
            }
        }

        // Check for unchanged data
        if (isDataUnchanged(isAdminContext ? employeeId : currentUserId)) {
            JOptionPane.showMessageDialog(this, "No changes detected. Please modify the fields before saving.", "No Changes", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Prepare SQL statement based on context
        String updateEmployeeSQL;
        int targetEmployeeId;

        if (isAdminContext) {
            // Updating another employee as admin
            updateEmployeeSQL = "UPDATE tbl_employees SET fld_first_name = ?, fld_last_name = ?, fld_email = ?, "
                              + "fld_password = ?, fld_gender = ?, fld_job_title_id = ?, fld_department_id = ?, "
                              + "fld_role_id = ?, fld_image_path = ? WHERE fld_employee_id = ?";
            targetEmployeeId = employeeId;  // Use the selected employee's ID
        } else {
            // Updating the logged-in user
            updateEmployeeSQL = "UPDATE tbl_employees SET fld_first_name = ?, fld_last_name = ?, fld_email = ?, "
                              + "fld_password = ?, fld_gender = ?, fld_job_title_id = ?, fld_department_id = ?, "
                              + "fld_role_id = ?, fld_image_path = ? WHERE fld_employee_id = ?";
            targetEmployeeId = currentUserId;  // Use the current logged-in user's ID
        }

        // Execute the update
        try (PreparedStatement pstmt = connection.prepareStatement(updateEmployeeSQL)) {
            pstmt.setString(1, firstName.getText()); 
            pstmt.setString(2, lastName.getText());  
            pstmt.setString(3, email);   
            pstmt.setString(4, String.valueOf(passWord.getPassword()));
            pstmt.setString(5, comboBoxGender.getSelectedItem().toString());
            pstmt.setInt(6, getSelectedJobTitleId());
            pstmt.setInt(7, getSelectedDepartmentId());
            pstmt.setInt(8, getSelectedRoleId()); 
            pstmt.setString(9, imageLocation);
            pstmt.setInt(10, targetEmployeeId);  // Use the appropriate ID

            int updatedRows = pstmt.executeUpdate();
            if (updatedRows > 0) {
                JOptionPane.showMessageDialog(this, "Employee updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Refresh employee data in MainAdmin if needed
                if (mainAdmin != null && isAdminContext) {
                    mainAdmin.loadEmployeeData(); // Reload employee data in MainAdmin
                }
                dispose(); // Close the EditEmployee window
            } else {
                JOptionPane.showMessageDialog(this, "No employee found to update.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating employee: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
            imageLocation = selectedFile.getAbsolutePath(); 

            displayImage(imageLocation);

            updateEmployeeImageInDatabase(imageLocation, employeeId);

        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearFields();
    }//GEN-LAST:event_btnClearActionPerformed

//    private int getUserIdFromEmployeeId(int employeeId) {
//        String query = "SELECT fld_employee_id FROM tbl_employees WHERE fld_employee_id = ?";
//        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
//            pstmt.setInt(1, employeeId);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                return rs.getInt("fld_employee_id");
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error retrieving employee ID: " + e.getMessage());
//        }
//        return -1; 
//    }

    private void updateEmployeeImageInDatabase(String imagePath, int employeeId) {
        String updateImageSQL = "UPDATE tbl_employees SET fld_image_path = ? WHERE fld_employee_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateImageSQL)) {
            pstmt.setString(1, imagePath);
            pstmt.setInt(2, employeeId);
            pstmt.executeUpdate(); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating employee image: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void displayImage(String imagePath) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(imagePath));
            if (img != null) {
                int width = 100; 
                int height = 100; 
                Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImg));
            } else {
                throw new IOException("Failed to load image: " + imagePath);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error displaying image: " + e.getMessage());
            imageLabel.setIcon(new ImageIcon("path/to/default/image.png"));
        }
    }

    
    public void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg", "gif"));
    
        int response = fileChooser.showOpenDialog(null);
        if (response == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imageLocation = selectedFile.getAbsolutePath();
            displayImage(imageLocation); 
        }
    }

    
//    private String addImageToFolder() {
//        if (!imageSelected) {
//            JOptionPane.showMessageDialog(this, "Please select an image first.");
//            return null;
//        }
//
//        String[] acceptedImageExtensions = {".jpg", ".jpeg", ".png"};
//        String fileExtension = imageLocation.substring(imageLocation.lastIndexOf(".")).toLowerCase();
//        boolean isImage = Arrays.asList(acceptedImageExtensions).contains(fileExtension);
//        if (!isImage) {
//            JOptionPane.showMessageDialog(this, "Please select a valid image file (jpg, jpeg, png, gif, bmp).", "Invalid File Type", JOptionPane.ERROR_MESSAGE);
//            return null;
//        }
//    
//        String destinationFolder = "src/Users/";
//        String newFileName = "employee_" + System.currentTimeMillis() + fileExtension;
//        String destinationPath = destinationFolder + newFileName;
//
//        File sourceFile = new File(imageLocation);
//        File destinationFile = new File(destinationPath);
//
//        try {
//            destinationFile.getParentFile().mkdirs();
//
//            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//            return destinationPath;
//        } catch (IOException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Error adding image: " + e.getMessage());
//            return null;
//        }
//    }
//    
//    private void updateImage(File selectedFile) {
//        try {
//            File destinationFile = new File("path/to/destination/" + selectedFile.getName()); // Update with actual destination path
//            Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//            ImageIcon icon = new ImageIcon(destinationFile.getAbsolutePath());
//            Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Adjust size as needed
//            imageLabel.setIcon(new ImageIcon(img));
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog(this, "Error updating image: " + e.getMessage());
//        }
//    }

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
            java.util.logging.Logger.getLogger(EditEmployee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditEmployee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditEmployee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditEmployee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                          boolean isAdminContext = true; // Set based on your logic
            int currentUserId = 1; 
                MainAdmin mainAdmin = new MainAdmin(); 
                new EditEmployee(mainAdmin, isAdminContext, currentUserId).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnConfirmEdit;
    private javax.swing.JComboBox<Department> comboBoxDepartment;
    private javax.swing.JComboBox<String> comboBoxGender;
    private javax.swing.JComboBox<JobTitle> comboBoxJobTitle;
    private javax.swing.JComboBox<Role> comboBoxRole;
    private javax.swing.JTextField eMail;
    private javax.swing.JTextField firstName;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
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
