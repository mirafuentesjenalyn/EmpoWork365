/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package EmpoWork365;

import java.sql.Connection;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author jenal
 */
public final class MainAdmin extends javax.swing.JFrame implements UserUpdateListener{
    private UserAuthenticate loggedInUser;
    private EditUserDetails editUserDetails;
    private Connection connection;
    private final int REGULAR_HOURS_PER_MONTH;


    public static MainAdmin instance;
    private int columnIndex;
    /**
     * Creates new form Login
     */
    public MainAdmin() {
        this.REGULAR_HOURS_PER_MONTH  = 160;
        initComponents();
        instance = this;

        addButtonHoverEffect(btnHome);
        addButtonHoverEffect(btnEmpMan);
        addButtonHoverEffect(btnAttSum);
        addButtonHoverEffect(btnPayroll);
        addButtonHoverEffect(btnLeaveSum);

        try {
              sqlConnector connector = new sqlConnector();
              this.connection = connector.createConnection(); 
              loadEmployeeData();
              getAttendanceData();
              loadEmployeeLeave();
          } catch (SQLException e) {
              JOptionPane.showMessageDialog(this, "Failed to connect to the database: " + e.getMessage());
        }
        
        searchNameTxt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                performSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                performSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                performSearch();
            }
        });

        
        searchNameTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
    }
    
        @Override
        public void onUserUpdated(UserAuthenticate updatedUser) {
            setUserDetails(updatedUser);
        }
        
        public void setAuthenticatedUser(UserAuthenticate loggedInUser) {
            this.loggedInUser = loggedInUser;
            setUserDetails(loggedInUser); 
            loadEmployeeLeave();
        }

    public void loadEmployeeData() {
        try {
            EmployeeMethod employeeMethod = new EmployeeMethod(connection);
            DefaultTableModel model = employeeMethod.getEmployeeData();
            setTableModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load employee data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
       
    void getAttendanceData() {
        try {
            EmployeeMethod employeeMethod = new EmployeeMethod(connection);
            DefaultTableModel model = employeeMethod.getAttendanceData();
            setTableAttendance(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load employee data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setTableModel(DefaultTableModel model) {
        DefaultTableModel nonEditableModel = new DefaultTableModel(model.getDataVector(), getColumnNames(model)) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        jTable1.setModel(nonEditableModel);
    }
    
    private void setTableAttendance(DefaultTableModel model) {
        DefaultTableModel nonEditableModel = new DefaultTableModel(model.getDataVector(), getColumnNames(model)) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        jTable2.setModel(nonEditableModel);
    }
    
    public void loadEmployeeLeave() {
        try {
            EmployeeMethod employeeMethod = new EmployeeMethod(connection);
            DefaultTableModel model = employeeMethod.getRequestsData();
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No leave applications found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                setLeaveTableModel(model);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading leave applications: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setLeaveTableModel(DefaultTableModel model) {
        leaveTable.setModel(model);
        leaveTable.setRowHeight(40); // Set a minimum height for each row

//        leaveTable.getColumnModel().getColumn(2).setCellRenderer(new MultiLineCellRenderer());
        leaveTable.getColumnModel().getColumn(4).setCellRenderer(new MultiLineCellRenderer());
        leaveTable.getColumnModel().getColumn(5).setCellRenderer(new MultiLineCellRenderer());

        leaveTable.getColumnModel().getColumn(0).setPreferredWidth(30);  // "Application ID" column
        leaveTable.getColumnModel().getColumn(1).setPreferredWidth(80); // "Application" column
        leaveTable.getColumnModel().getColumn(2).setPreferredWidth(120); // "Name" column
        leaveTable.getColumnModel().getColumn(3).setPreferredWidth(80); // "Leave Request" column
        leaveTable.getColumnModel().getColumn(4).setPreferredWidth(95); // "Leave Type" column
        leaveTable.getColumnModel().getColumn(5).setPreferredWidth(200); // "Reason" column
        leaveTable.getColumnModel().getColumn(6).setPreferredWidth(75); // "Status" column
        leaveTable.getColumnModel().getColumn(7).setPreferredWidth(200); // "Action" column

        leaveTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        leaveTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));
        leaveTable.revalidate();
        leaveTable.repaint();
    }

    void approveLeave(int leaveId) {
        try {
            EmployeeMethod employeeMethod = new EmployeeMethod(connection);
            employeeMethod.updateLeaveStatus(leaveId, "Approved");
            loadEmployeeLeave(); // Refresh the leave table
            JOptionPane.showMessageDialog(this, "Leave approved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error approving leave: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Reject leave action
    void rejectLeave(int leaveId) {
        try {
            EmployeeMethod employeeMethod = new EmployeeMethod(connection);
            employeeMethod.updateLeaveStatus(leaveId, "Rejected");
            loadEmployeeLeave(); // Refresh the leave table
            JOptionPane.showMessageDialog(this, "Leave rejected successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error rejecting leave: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private Vector<String> getColumnNames(DefaultTableModel model) {
        Vector<String> columnNames = new Vector<>();
        for (int i = 0; i < model.getColumnCount(); i++) {
            columnNames.add(model.getColumnName(i)); // Use getColumnName instead
        }
        return columnNames;
    }


    private void addButtonHoverEffect(javax.swing.JButton button) {
        button.setOpaque(true);
        button.setBackground(new Color(8,127,127));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(17,94,94));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(8,127,127));
            }
        });
    }
    
    private ImageIcon resizeImage(String imagePath, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image originalImage = originalIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return new ImageIcon(resizedImage);
    }

       
    private void setUserDetails(UserAuthenticate user) {
        if (loggedInUser != null) {
            fullName.setText(loggedInUser.getFirstname().toUpperCase() + " " + loggedInUser.getLastname().toUpperCase());
            userWelcome.setText(loggedInUser.getFirstname());
            userJobTitle.setText(loggedInUser.getJobtitle().toUpperCase());
            userRole.setText(loggedInUser.getRoleName().toUpperCase() + " PROFILE");
            employeeIdLabel.setText(String.valueOf(loggedInUser.getId()));
            ImageIcon userImage = resizeImage(loggedInUser.getImagepath(), 100, 100);
            userImageIcon.setIcon(userImage);
            firstNameLabel.setText(loggedInUser.getFirstname().toUpperCase());
            lastNameLabel.setText(loggedInUser.getLastname().toUpperCase());
            genderLabel.setText(loggedInUser.getGender().toUpperCase());
            emailLabel.setText(loggedInUser.getEmail().toUpperCase());
            departmentLabel.setText(loggedInUser.getDepartmentName().toUpperCase());
        }
    }

    public void setLoggedInUser(UserAuthenticate authenticatedUser) {
        this.loggedInUser = authenticatedUser; 
    }
    
    public void performSearch() {
        String searchTerm = searchNameTxt.getText().trim();
        if (searchTerm.isEmpty() || searchTerm.equals("Search")) {
        loadEmployeeData();
        getAttendanceData();
        } else {
            searchAndDisplayEmployees(searchTerm); 
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

        jPanel5 = new javax.swing.JPanel();
        sideBar = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnHome = new javax.swing.JButton();
        btnEmpMan = new javax.swing.JButton();
        btnAttSum = new javax.swing.JButton();
        btnLeaveSum = new javax.swing.JButton();
        btnPayroll = new javax.swing.JButton();
        fullName = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        userImageIcon = new javax.swing.JLabel();
        logout = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        home = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        userWelcome = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        employeeIdLabel = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        firstNameLabel = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        lastNameLabel = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        emailLabel = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        userJobTitle = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        departmentLabel = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        genderLabel = new javax.swing.JLabel();
        btnEditProfile = new javax.swing.JButton();
        userRole = new javax.swing.JLabel();
        employeeManagement = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        searchBar = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        searchNameTxt = new javax.swing.JTextField();
        searchBarEmployeeName = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        btnDelete = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        trackingAttendance = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        leave = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        leaveTable = new javax.swing.JTable();
        managePayroll = new javax.swing.JPanel();
        payrollManagementTitle = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        incomeTaxLabel = new javax.swing.JLabel();
        SSSTextField = new javax.swing.JTextField();
        philHealthTextField = new javax.swing.JTextField();
        pagibigLabel = new javax.swing.JLabel();
        unpaidLeaveLabel = new javax.swing.JLabel();
        SSSLabel = new javax.swing.JLabel();
        unpaidLeaveTextField = new javax.swing.JTextField();
        totalDeducLabel = new javax.swing.JLabel();
        philHealthLabel = new javax.swing.JLabel();
        pagibigTextField = new javax.swing.JTextField();
        incomeTaxTextField = new javax.swing.JTextField();
        overtimeHours_Label = new javax.swing.JLabel();
        total_hours_worked_Label = new javax.swing.JLabel();
        HrsMonthTextField = new javax.swing.JTextField();
        totalHrsWorkedTextField = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        rateHourTextField = new javax.swing.JTextField();
        getIdPayroll = new javax.swing.JTextField();
        rateperHour_Label = new javax.swing.JLabel();
        emp_idLabel = new javax.swing.JLabel();
        totalSalaryPerMonthLabel = new javax.swing.JLabel();
        totalDeducTextField = new javax.swing.JTextField();
        totalSalaryPerMonthTextField = new javax.swing.JTextField();
        overtimeHrsTextField = new javax.swing.JTextField();
        unpaidLeaveLabel1 = new javax.swing.JLabel();
        leaveBalanceTextField = new javax.swing.JTextField();
        unpaidLeaveLabel2 = new javax.swing.JLabel();
        sickLeaveTextField = new javax.swing.JTextField();
        unpaidLeaveLabel3 = new javax.swing.JLabel();
        emergencyLeaveTextField = new javax.swing.JTextField();
        thirteenthMonthPayLabel = new javax.swing.JLabel();
        thirteenthMonthPayTextField = new javax.swing.JTextField();
        netSalaryTextField = new javax.swing.JTextField();
        netSalaryLabel = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        departmentTextField = new javax.swing.JTextField();
        departmentNameLabel = new javax.swing.JLabel();
        jobTitleLabel = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        emailTextField = new javax.swing.JTextField();
        genderTextField = new javax.swing.JTextField();
        jobTitleTextField = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        userImagePayroll = new javax.swing.JLabel();
        btnClear2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sideBar.setBackground(new java.awt.Color(8, 127, 127));
        sideBar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(8, 127, 127));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(8, 127, 127));

        btnHome.setBackground(new java.awt.Color(102, 102, 102));
        btnHome.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnHome.setForeground(new java.awt.Color(255, 255, 255));
        btnHome.setBorder(null);
        btnHome.setContentAreaFilled(false);
        btnHome.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHome.setHideActionText(true);
        btnHome.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHome.setLabel("      Home");
        btnHome.setMargin(new java.awt.Insets(50, 30, 20, 15));
        btnHome.setPreferredSize(new java.awt.Dimension(210, 30));
        btnHome.setRequestFocusEnabled(false);
        btnHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeActionPerformed(evt);
            }
        });

        btnEmpMan.setBackground(new java.awt.Color(102, 102, 102));
        btnEmpMan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnEmpMan.setForeground(new java.awt.Color(255, 255, 255));
        btnEmpMan.setBorder(null);
        btnEmpMan.setContentAreaFilled(false);
        btnEmpMan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEmpMan.setHideActionText(true);
        btnEmpMan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEmpMan.setLabel("      Employee Management");
        btnEmpMan.setMargin(new java.awt.Insets(50, 30, 20, 15));
        btnEmpMan.setPreferredSize(new java.awt.Dimension(210, 30));
        btnEmpMan.setRequestFocusEnabled(false);
        btnEmpMan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmpManActionPerformed(evt);
            }
        });

        btnAttSum.setBackground(new java.awt.Color(102, 102, 102));
        btnAttSum.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnAttSum.setForeground(new java.awt.Color(255, 255, 255));
        btnAttSum.setBorder(null);
        btnAttSum.setContentAreaFilled(false);
        btnAttSum.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAttSum.setHideActionText(true);
        btnAttSum.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAttSum.setLabel("      Attendance Summary");
        btnAttSum.setMargin(new java.awt.Insets(50, 30, 20, 15));
        btnAttSum.setPreferredSize(new java.awt.Dimension(210, 30));
        btnAttSum.setRequestFocusEnabled(false);
        btnAttSum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAttSumActionPerformed(evt);
            }
        });

        btnLeaveSum.setBackground(new java.awt.Color(102, 102, 102));
        btnLeaveSum.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnLeaveSum.setForeground(new java.awt.Color(255, 255, 255));
        btnLeaveSum.setBorder(null);
        btnLeaveSum.setContentAreaFilled(false);
        btnLeaveSum.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLeaveSum.setHideActionText(true);
        btnLeaveSum.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLeaveSum.setLabel("      Leave Summary");
        btnLeaveSum.setMargin(new java.awt.Insets(50, 30, 20, 15));
        btnLeaveSum.setPreferredSize(new java.awt.Dimension(210, 30));
        btnLeaveSum.setRequestFocusEnabled(false);
        btnLeaveSum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeaveSumActionPerformed(evt);
            }
        });

        btnPayroll.setBackground(new java.awt.Color(102, 102, 102));
        btnPayroll.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnPayroll.setForeground(new java.awt.Color(255, 255, 255));
        btnPayroll.setBorder(null);
        btnPayroll.setContentAreaFilled(false);
        btnPayroll.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPayroll.setHideActionText(true);
        btnPayroll.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPayroll.setLabel("      Payroll Management");
        btnPayroll.setMargin(new java.awt.Insets(50, 30, 20, 15));
        btnPayroll.setPreferredSize(new java.awt.Dimension(210, 30));
        btnPayroll.setRequestFocusEnabled(false);
        btnPayroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPayrollActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnHome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnEmpMan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnAttSum, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnLeaveSum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnPayroll, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnHome, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEmpMan, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnAttSum, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnLeaveSum, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnPayroll, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(76, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 204, 210, 280));

        fullName.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        fullName.setForeground(new java.awt.Color(255, 255, 255));
        fullName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fullName.setText("NAME");
        jPanel3.add(fullName, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 204, -1));

        jPanel1.setBackground(new java.awt.Color(8, 127, 127));

        userImageIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        userImageIcon.setMaximumSize(new java.awt.Dimension(100, 100));
        userImageIcon.setMinimumSize(new java.awt.Dimension(100, 100));
        userImageIcon.setPreferredSize(new java.awt.Dimension(100, 100));
        jPanel1.add(userImageIcon);

        jPanel3.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 150, -1));

        logout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/logout.png"))); // NOI18N
        logout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutMouseClicked(evt);
            }
        });
        jPanel3.add(logout, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 680, -1, -1));

        sideBar.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 800));

        home.setBackground(new java.awt.Color(255, 249, 249));

        jPanel4.setBackground(new java.awt.Color(218, 248, 240));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(48, 77, 71));
        jLabel1.setText("Hi,");

        userWelcome.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        userWelcome.setForeground(new java.awt.Color(48, 77, 71));
        userWelcome.setText("user");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(15, 86, 86));
        jLabel6.setText("USER ID:");

        employeeIdLabel.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        employeeIdLabel.setForeground(new java.awt.Color(15, 86, 86));
        employeeIdLabel.setText("Number");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(userWelcome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(employeeIdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(445, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(userWelcome))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(employeeIdLabel))
                .addGap(32, 32, 32))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(48, 77, 71));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("FIRST NAME");

        firstNameLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        firstNameLabel.setText("FIRST NAME");

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(48, 77, 71));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText("LAST NAME");

        lastNameLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lastNameLabel.setText("LAST NAME");

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(48, 77, 71));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel27.setText("EMAIL");

        emailLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        emailLabel.setText("EMAIL");

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(48, 77, 71));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel28.setText("POSITION");

        userJobTitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        userJobTitle.setText("JOB TITLE");

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(48, 77, 71));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel29.setText("DEPARTMENT");

        departmentLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        departmentLabel.setText("DEPARTMENT");

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(48, 77, 71));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel33.setText("GENDER");

        genderLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        genderLabel.setText("GENDER");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(firstNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(userJobTitle, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(genderLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(emailLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lastNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(departmentLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(firstNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lastNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(genderLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(userJobTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(departmentLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        btnEditProfile.setBackground(new java.awt.Color(8, 127, 127));
        btnEditProfile.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEditProfile.setForeground(new java.awt.Color(255, 255, 255));
        btnEditProfile.setText("EDIT PROFILE");
        btnEditProfile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditProfileActionPerformed(evt);
            }
        });

        userRole.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        userRole.setForeground(new java.awt.Color(9, 53, 53));
        userRole.setText("Role");

        javax.swing.GroupLayout homeLayout = new javax.swing.GroupLayout(home);
        home.setLayout(homeLayout);
        homeLayout.setHorizontalGroup(
            homeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeLayout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(homeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, homeLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(userRole, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEditProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(1262, Short.MAX_VALUE))
            .addGroup(homeLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        homeLayout.setVerticalGroup(
            homeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(homeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnEditProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(userRole))
                .addGap(18, 18, 18)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(227, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab1", home);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Employee Management");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Email", "Gender", "Job Title", "Department", "Date of Employment"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setMinWidth(30);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(30);
            jTable1.getColumnModel().getColumn(3).setMinWidth(70);
            jTable1.getColumnModel().getColumn(3).setMaxWidth(70);
        }

        btnAdd.setBackground(new java.awt.Color(8, 127, 127));
        btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setText("Add");
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(1, 113, 132)));

        searchNameTxt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        searchNameTxt.setForeground(new java.awt.Color(0, 102, 102));
        searchNameTxt.setText("Search");
        searchNameTxt.setBorder(null);
        searchNameTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchNameTxtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchNameTxtFocusLost(evt);
            }
        });

        searchBarEmployeeName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/search.png"))); // NOI18N
        searchBarEmployeeName.setBorder(null);
        searchBarEmployeeName.setContentAreaFilled(false);
        searchBarEmployeeName.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        searchBarEmployeeName.setFocusPainted(false);
        searchBarEmployeeName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBarEmployeeNameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchNameTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchBarEmployeeName)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(searchNameTxt)
                    .addComponent(searchBarEmployeeName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(54, 54, 54))
        );

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Name:");

        btnDelete.setBackground(new java.awt.Color(255, 102, 102));
        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("Delete");
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnEdit.setText("Edit");
        btnEdit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout searchBarLayout = new javax.swing.GroupLayout(searchBar);
        searchBar.setLayout(searchBarLayout);
        searchBarLayout.setHorizontalGroup(
            searchBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchBarLayout.createSequentialGroup()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(285, Short.MAX_VALUE))
        );
        searchBarLayout.setVerticalGroup(
            searchBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchBarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, searchBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(searchBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(searchBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout employeeManagementLayout = new javax.swing.GroupLayout(employeeManagement);
        employeeManagement.setLayout(employeeManagementLayout);
        employeeManagementLayout.setHorizontalGroup(
            employeeManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(employeeManagementLayout.createSequentialGroup()
                .addGroup(employeeManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(employeeManagementLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(employeeManagementLayout.createSequentialGroup()
                        .addGap(268, 268, 268)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 787, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 1039, Short.MAX_VALUE))
        );
        employeeManagementLayout.setVerticalGroup(
            employeeManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(employeeManagementLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(190, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab2", employeeManagement);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Track Attendance");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Job Title", "Department", "Time-In", "Time-Out", "Date"
            }
        ));
        jScrollPane2.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setMinWidth(30);
            jTable2.getColumnModel().getColumn(0).setMaxWidth(30);
        }

        javax.swing.GroupLayout trackingAttendanceLayout = new javax.swing.GroupLayout(trackingAttendance);
        trackingAttendance.setLayout(trackingAttendanceLayout);
        trackingAttendanceLayout.setHorizontalGroup(
            trackingAttendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(trackingAttendanceLayout.createSequentialGroup()
                .addGap(273, 273, 273)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(trackingAttendanceLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 783, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1217, Short.MAX_VALUE))
        );
        trackingAttendanceLayout.setVerticalGroup(
            trackingAttendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(trackingAttendanceLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 677, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab3", trackingAttendance);

        leave.setBackground(new java.awt.Color(255, 249, 249));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Leave Request Summary");

        leaveTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Applciation ID", "Date Applied", "Name", "Leave Request", "Leave Type", "Reason", "Status", "Action"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(leaveTable);

        javax.swing.GroupLayout leaveLayout = new javax.swing.GroupLayout(leave);
        leave.setLayout(leaveLayout);
        leaveLayout.setHorizontalGroup(
            leaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leaveLayout.createSequentialGroup()
                .addGroup(leaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(leaveLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 765, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(leaveLayout.createSequentialGroup()
                        .addGap(285, 285, 285)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(1229, Short.MAX_VALUE))
        );
        leaveLayout.setVerticalGroup(
            leaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leaveLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 660, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab1", leave);

        payrollManagementTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        payrollManagementTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        payrollManagementTitle.setText("Payroll Management");

        incomeTaxLabel.setText("Income Tax:");

        pagibigLabel.setText("Pag-IBIG Fund:");

        unpaidLeaveLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        unpaidLeaveLabel.setText("Unpaid Leave:");

        SSSLabel.setText("Social Security System:");

        totalDeducLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        totalDeducLabel.setText("Total Deduction:");

        philHealthLabel.setText("Philhealth:");

        overtimeHours_Label.setText("Overtime Hours");

        total_hours_worked_Label.setText("Total Hours Worked");

        HrsMonthTextField.setEditable(false);

        jLabel19.setText("Hours/Month");

        getIdPayroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getIdPayrollActionPerformed(evt);
            }
        });

        rateperHour_Label.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        rateperHour_Label.setText("Rate/Hour:");

        emp_idLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        emp_idLabel.setText("Employee ID:");

        totalSalaryPerMonthLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        totalSalaryPerMonthLabel.setText("Total Salary/Month:");

        unpaidLeaveLabel1.setText("Leave Balance:");

        unpaidLeaveLabel2.setText("Sick Leave:");

        unpaidLeaveLabel3.setText("Emergency Leave:");

        thirteenthMonthPayLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        thirteenthMonthPayLabel.setText("13th Month Pay:");

        netSalaryTextField.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        netSalaryLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        netSalaryLabel.setText("Net Salary:");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SSSLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pagibigLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(incomeTaxLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(unpaidLeaveLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(unpaidLeaveLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(unpaidLeaveLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(emergencyLeaveTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                            .addComponent(sickLeaveTextField)
                            .addComponent(leaveBalanceTextField)
                            .addComponent(incomeTaxTextField)
                            .addComponent(pagibigTextField)
                            .addComponent(SSSTextField, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(unpaidLeaveLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(philHealthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(totalSalaryPerMonthLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                                        .addComponent(rateperHour_Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(emp_idLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(totalDeducLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(overtimeHours_Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(total_hours_worked_Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(totalDeducTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                                            .addComponent(rateHourTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(getIdPayroll, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(totalSalaryPerMonthTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(HrsMonthTextField)
                                            .addComponent(totalHrsWorkedTextField)
                                            .addComponent(overtimeHrsTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)))
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addGap(59, 59, 59)
                                        .addComponent(philHealthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(unpaidLeaveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                                .addComponent(netSalaryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(netSalaryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(thirteenthMonthPayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(thirteenthMonthPayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emp_idLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(getIdPayroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rateperHour_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rateHourTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalSalaryPerMonthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalSalaryPerMonthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HrsMonthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(total_hours_worked_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalHrsWorkedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(overtimeHours_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(overtimeHrsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalDeducLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalDeducTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(philHealthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(philHealthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SSSLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SSSTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pagibigLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pagibigTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(incomeTaxLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(incomeTaxTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(unpaidLeaveLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unpaidLeaveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(leaveBalanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unpaidLeaveLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sickLeaveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unpaidLeaveLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emergencyLeaveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unpaidLeaveLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(thirteenthMonthPayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(thirteenthMonthPayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(netSalaryTextField)
                    .addComponent(netSalaryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        departmentTextField.setEditable(false);

        departmentNameLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        departmentNameLabel.setText("Department:");

        jobTitleLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jobTitleLabel.setText("Job Title:");

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel32.setText("Gender:");

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel31.setText("Email:");

        nameLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        nameLabel.setText("Name:");

        nameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameTextFieldActionPerformed(evt);
            }
        });

        emailTextField.setEditable(false);

        genderTextField.setEditable(false);

        jobTitleTextField.setEditable(false);

        jPanel8.setBackground(new java.awt.Color(204, 204, 255));

        userImagePayroll.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        userImagePayroll.setMaximumSize(new java.awt.Dimension(100, 100));
        userImagePayroll.setMinimumSize(new java.awt.Dimension(100, 100));
        userImagePayroll.setPreferredSize(new java.awt.Dimension(100, 100));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(userImagePayroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(userImagePayroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nameLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jobTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(genderTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                            .addComponent(emailTextField)
                            .addComponent(nameTextField)
                            .addComponent(jobTitleTextField)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(departmentNameLabel)
                        .addGap(5, 5, 5)
                        .addComponent(departmentTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(110, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(93, 93, 93))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(genderTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jobTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jobTitleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(departmentTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(departmentNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        btnClear2.setBackground(new java.awt.Color(71, 146, 146));
        btnClear2.setForeground(new java.awt.Color(204, 255, 255));
        btnClear2.setText("Clear All");
        btnClear2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClear2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClear2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout managePayrollLayout = new javax.swing.GroupLayout(managePayroll);
        managePayroll.setLayout(managePayrollLayout);
        managePayrollLayout.setHorizontalGroup(
            managePayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(managePayrollLayout.createSequentialGroup()
                .addGroup(managePayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(managePayrollLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(managePayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(managePayrollLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(managePayrollLayout.createSequentialGroup()
                                .addGap(183, 183, 183)
                                .addComponent(btnClear2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(managePayrollLayout.createSequentialGroup()
                        .addGap(274, 274, 274)
                        .addComponent(payrollManagementTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(1233, Short.MAX_VALUE))
        );
        managePayrollLayout.setVerticalGroup(
            managePayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(managePayrollLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(payrollManagementTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(managePayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(managePayrollLayout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnClear2))
                    .addGroup(managePayrollLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab4", managePayroll);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2214, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(sideBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(4, 4, 4)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 840, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addGap(40, 40, 40)
                            .addComponent(sideBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 840, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -40, 1000, -1));

        setSize(new java.awt.Dimension(1016, 809));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    
    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        jTabbedPane1.setSelectedIndex(0);
    }//GEN-LAST:event_btnHomeActionPerformed

    private void btnEmpManActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmpManActionPerformed
        jTabbedPane1.setSelectedIndex(1); 
    }//GEN-LAST:event_btnEmpManActionPerformed

    private void btnAttSumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAttSumActionPerformed
        jTabbedPane1.setSelectedIndex(2);
    }//GEN-LAST:event_btnAttSumActionPerformed

    private void btnPayrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayrollActionPerformed
        jTabbedPane1.setSelectedIndex(4);
    }//GEN-LAST:event_btnPayrollActionPerformed

    private void searchNameTxtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchNameTxtFocusGained
        if (searchNameTxt.getText().equals("Search")) {
            searchNameTxt.setText("");
            searchNameTxt.setForeground(new Color(142,122,69));
        } 
    }//GEN-LAST:event_searchNameTxtFocusGained

    private void searchNameTxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchNameTxtFocusLost
        if (searchNameTxt.getText().isEmpty()) {
            searchNameTxt.setText("Search");
            searchNameTxt.setForeground(new Color(205,186,136));
        }
    }//GEN-LAST:event_searchNameTxtFocusLost

    private void searchBarEmployeeNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBarEmployeeNameActionPerformed
            performSearch();
    }//GEN-LAST:event_searchBarEmployeeNameActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) {
            int employeeId = (Integer) jTable1.getValueAt(selectedRow, 0); 
            int response = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this employee?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);

            if (response == JOptionPane.YES_OPTION) {
                EmployeeMethod employeeMethod = new EmployeeMethod(connection);

                if (employeeMethod.deleteEmployeeById(employeeId)) {
                    JOptionPane.showMessageDialog(this, "Employee deleted successfully.");
                    loadEmployeeData();
                    getAttendanceData();
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting employee.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        AddAccount addAcccount = new AddAccount(this);
        addAcccount.setVisible(true);
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
    int selectedRow = jTable1.getSelectedRow();

        if (selectedRow != -1) {
            int employeeId = (int) jTable1.getValueAt(selectedRow, 0); 

            EditEmployeeDetails editForm = new EditEmployeeDetails(this);
            editForm.setEmployeeId(employeeId); 
            editForm.setVisible(true); 
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to edit.");
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseClicked
        int response = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to log out?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            try {
                LoginForm callLoginForm = new LoginForm();
                callLoginForm.setVisible(true);
                this.dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error occurred while logging out: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_logoutMouseClicked

    private void btnEditProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditProfileActionPerformed
        if (editUserDetails == null) {
            editUserDetails = new EditUserDetails(this, null);
        }
        editUserDetails.setUserUpdateListener(this); 
        editUserDetails.UpdateUserDetails(loggedInUser);
        editUserDetails.setVisible(true);
    }//GEN-LAST:event_btnEditProfileActionPerformed

    private void getIdPayrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getIdPayrollActionPerformed
        String employeeIdText = getIdPayroll.getText();

           try {
               int employeeId = Integer.parseInt(employeeIdText);

               EmployeeMethod employeeMethod = new EmployeeMethod(connection);
               Employee employee = employeeMethod.getEmployeeIdById(employeeId); 

               if (employee != null) {
                   updateEmployeeDetails(employee);
               } else {   
                   JOptionPane.showMessageDialog(this, "No employee found with the given ID.", "Employee Not Found", JOptionPane.WARNING_MESSAGE);
               }

           } catch (NumberFormatException e) {
               JOptionPane.showMessageDialog(this, "Invalid Employee ID format. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
           } catch (SQLException e) {
               JOptionPane.showMessageDialog(this, "Error fetching employee data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
           }
    }//GEN-LAST:event_getIdPayrollActionPerformed

    private void nameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameTextFieldActionPerformed
        String name = nameTextField.getText().trim();  

        if (name.length() >= 2) {
            try {
                EmployeeMethod employeeMethod = new EmployeeMethod(connection);
                Employee employee = employeeMethod.getEmployeeByName(name);

                if (employee != null) {
                    getIdPayroll.setText(String.valueOf(employee.getEmployeeId())); 
                    updateEmployeeDetails(employee); 
                } else {
                    JOptionPane.showMessageDialog(this, "No employee found with the given name.", "Employee Not Found", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error fetching employee data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a name to search.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_nameTextFieldActionPerformed

    private void btnClear2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClear2ActionPerformed
        clearFields();
    }//GEN-LAST:event_btnClear2ActionPerformed

    private void btnLeaveSumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeaveSumActionPerformed
        jTabbedPane1.setSelectedIndex(3);
    }//GEN-LAST:event_btnLeaveSumActionPerformed

    private void updateEmployeeDetails(Employee employee) {
        String FullName = employee.getFirstname() + " " + employee.getLastname();
        String imagePath = employee.getImagePath();

        nameTextField.setText(FullName);
        emailTextField.setText(employee.getEmail());           
        genderTextField.setText(employee.getGender());       
        jobTitleTextField.setText(employee.getJobtitle());     
        departmentTextField.setText(employee.getDepartmentName()); 
        double ratePerHour = employee.getRatePerHour();
        rateHourTextField.setText(String.format("%.2f hrs", ratePerHour));                
        HrsMonthTextField.setText(String.format("%.2f hrs", (double) REGULAR_HOURS_PER_MONTH));
        ImageIcon originalIcon = new ImageIcon(imagePath);
        int width = 100;
        int height = 100; 
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        userImagePayroll.setIcon(new ImageIcon(scaledImage));

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1; 
        int year = calendar.get(Calendar.YEAR);
        displayTotalHoursWorked(employee.getEmployeeId(), month, year, ratePerHour);
    }
    
    private void displayTotalHoursWorked(int employeeId, int month, int year, double ratePerHour) {
        AttendanceMethod attendanceMethod = new AttendanceMethod(connection);
        try {
            double totalHours = attendanceMethod.getTotalHoursWorkedInMonth(employeeId, month, year);
            totalHrsWorkedTextField.setText(String.format("%.2f hrs", totalHours));

            double overtimeHours = calculateOvertimeHours(totalHours);
            overtimeHrsTextField.setText(String.format("%.2f hrs", overtimeHours));

            double totalSalary = calculateTotalSalary(totalHours, ratePerHour, overtimeHours);
            totalSalaryPerMonthTextField.setText(formatCurrency(totalSalary));

            double netSalary = calculateNetSalary(totalSalary);
            netSalaryTextField.setText(formatCurrency(netSalary));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving total hours worked: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private double calculateOvertimeHours(double totalHoursWorked) {
        double overtimeHours = totalHoursWorked - REGULAR_HOURS_PER_MONTH ;
        return Math.max(overtimeHours, 0); 
    }

    private double calculateTotalSalary(double totalHoursWorked, double ratePerHour, double overtimeHours) {
        double regularSalary = ratePerHour * REGULAR_HOURS_PER_MONTH;
        double overtimeRate = ratePerHour * 1.5; 
        double overtimeSalary = overtimeRate * overtimeHours; 
        double totalSalary = regularSalary + overtimeSalary; 

        return totalSalary; 
    }
    
    private double calculateNetSalary(double totalSalary) {
        double philHealthDeduction = calculatePhilHealthDeduction(totalSalary);
        double sssDeduction = calculateSSSDeduction(totalSalary);
        double pagIbigDeduction = calculatePagIbigDeduction(totalSalary);
        double incomeTax = calculateIncomeTax(totalSalary);
        double totalDeductions = philHealthDeduction + sssDeduction + pagIbigDeduction + incomeTax;

        philHealthTextField.setText(formatCurrency(philHealthDeduction));
        SSSTextField.setText(formatCurrency(sssDeduction));
        pagibigTextField.setText(formatCurrency(pagIbigDeduction));
        incomeTaxTextField.setText(formatCurrency(incomeTax));
        totalDeducTextField.setText(formatCurrency(totalDeductions));

        return totalSalary - totalDeductions;
    }

    private double calculatePhilHealthDeduction(double totalSalary) {
        return totalSalary * 0.01; 
    }

    private double calculateSSSDeduction(double totalSalary) {
        return totalSalary * 0.02; 
    }

    private double calculatePagIbigDeduction(double totalSalary) {
        return totalSalary <= 200 ? totalSalary * 0.01 : totalSalary * 0.02; // 1% or 2%
    }

    private double calculateIncomeTax(double salary) {
        if (salary <= 250000) {
            return 0; // No tax for salaries <= PHP 250,000
        } else if (salary <= 400000) {
            return (salary - 250000) * 0.15; // 15% tax on income between PHP 250,001 and PHP 400,000
        } else if (salary <= 800000) {
            return 22500 + (salary - 400000) * 0.20; // 20% tax on income between PHP 400,001 and PHP 800,000
        } else if (salary <= 2000000) {
            return 102500 + (salary - 800000) * 0.25; // 25% tax on income between PHP 800,001 and PHP 2,000,000
        } else if (salary <= 8000000) {
            return 402500 + (salary - 2000000) * 0.30; // 30% tax on income between PHP 2,000,001 and PHP 8,000,000
        } else {
            return 1802500 + (salary - 8000000) * 0.35; // 35% tax on income above PHP 8,000,000
        }
    }


    private String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("en", "PH")).format(amount);
    }
    
    public void searchAndDisplayEmployees(String searchTerm) {
        EmployeeMethod employeeMethod = new EmployeeMethod(connection);
        List<Employee> employeeList = employeeMethod.searchEmployeeMethod(searchTerm);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Employee ID", "Full Name", "Email", "Gender", "Job Title", "Department", "Date of Employment"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Employee employee : employeeList) {
            String FullName = employee.getFirstname() + " " + employee.getLastname();
            model.addRow(new Object[]{
                employee.getEmployeeId(),
                FullName,
                employee.getEmail(),
                employee.getGender(),
                employee.getJobtitle(),
                employee.getDepartmentName(),
                employee.getDateOfEmployment()
            });
        }
        jTable1.setModel(model);
    }

    
    private void clearFields() {
        getIdPayroll.setText("");
        rateHourTextField.setText("");
        totalSalaryPerMonthTextField.setText("");
        HrsMonthTextField.setText("");
        totalHrsWorkedTextField.setText("");
        overtimeHrsTextField.setText("");
        totalDeducTextField.setText("");
        philHealthTextField.setText("");
        SSSTextField.setText("");
        pagibigTextField.setText("");
        incomeTaxTextField.setText("");
        unpaidLeaveTextField.setText("");
        leaveBalanceTextField.setText("");
        sickLeaveTextField.setText("");
        emergencyLeaveTextField.setText("");
        thirteenthMonthPayTextField.setText("");
        netSalaryTextField.setText("");
        nameTextField.setText("");
        emailTextField.setText("");
        genderTextField.setText("");
        jobTitleTextField.setText("");
        departmentTextField.setText("");
        userImagePayroll.setIcon(null);

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
            java.util.logging.Logger.getLogger(MainAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainAdmin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField HrsMonthTextField;
    private javax.swing.JLabel SSSLabel;
    private javax.swing.JTextField SSSTextField;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAttSum;
    private javax.swing.JButton btnClear2;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnEditProfile;
    private javax.swing.JButton btnEmpMan;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnLeaveSum;
    private javax.swing.JButton btnPayroll;
    private javax.swing.JLabel departmentLabel;
    private javax.swing.JLabel departmentNameLabel;
    private javax.swing.JTextField departmentTextField;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JTextField emailTextField;
    private javax.swing.JTextField emergencyLeaveTextField;
    private javax.swing.JLabel emp_idLabel;
    private javax.swing.JLabel employeeIdLabel;
    private javax.swing.JPanel employeeManagement;
    private javax.swing.JLabel firstNameLabel;
    private javax.swing.JLabel fullName;
    private javax.swing.JLabel genderLabel;
    private javax.swing.JTextField genderTextField;
    private javax.swing.JTextField getIdPayroll;
    private javax.swing.JPanel home;
    private javax.swing.JLabel incomeTaxLabel;
    private javax.swing.JTextField incomeTaxTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel jobTitleLabel;
    private javax.swing.JTextField jobTitleTextField;
    private javax.swing.JLabel lastNameLabel;
    private javax.swing.JPanel leave;
    private javax.swing.JTextField leaveBalanceTextField;
    private javax.swing.JTable leaveTable;
    private javax.swing.JLabel logout;
    private javax.swing.JPanel managePayroll;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel netSalaryLabel;
    private javax.swing.JTextField netSalaryTextField;
    private javax.swing.JLabel overtimeHours_Label;
    private javax.swing.JTextField overtimeHrsTextField;
    private javax.swing.JLabel pagibigLabel;
    private javax.swing.JTextField pagibigTextField;
    private javax.swing.JLabel payrollManagementTitle;
    private javax.swing.JLabel philHealthLabel;
    private javax.swing.JTextField philHealthTextField;
    private javax.swing.JTextField rateHourTextField;
    private javax.swing.JLabel rateperHour_Label;
    private javax.swing.JPanel searchBar;
    private javax.swing.JButton searchBarEmployeeName;
    private javax.swing.JTextField searchNameTxt;
    private javax.swing.JTextField sickLeaveTextField;
    private javax.swing.JPanel sideBar;
    private javax.swing.JLabel thirteenthMonthPayLabel;
    private javax.swing.JTextField thirteenthMonthPayTextField;
    private javax.swing.JLabel totalDeducLabel;
    private javax.swing.JTextField totalDeducTextField;
    private javax.swing.JTextField totalHrsWorkedTextField;
    private javax.swing.JLabel totalSalaryPerMonthLabel;
    private javax.swing.JTextField totalSalaryPerMonthTextField;
    private javax.swing.JLabel total_hours_worked_Label;
    private javax.swing.JPanel trackingAttendance;
    private javax.swing.JLabel unpaidLeaveLabel;
    private javax.swing.JLabel unpaidLeaveLabel1;
    private javax.swing.JLabel unpaidLeaveLabel2;
    private javax.swing.JLabel unpaidLeaveLabel3;
    private javax.swing.JTextField unpaidLeaveTextField;
    private javax.swing.JLabel userImageIcon;
    private javax.swing.JLabel userImagePayroll;
    private javax.swing.JLabel userJobTitle;
    private javax.swing.JLabel userRole;
    private javax.swing.JLabel userWelcome;
    // End of variables declaration//GEN-END:variables

    private static class DefaultTableModelImpl extends DefaultTableModel {

        private final DefaultTableModel model;

        public DefaultTableModelImpl(Object[] columnNames, int rowCount, DefaultTableModel model) {
            super(columnNames, rowCount);
            this.model = model;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            // Allow button cells to be editable
            return column >= model.getColumnCount() - 2; // Allow editing only for last two columns
        }
    }
}
