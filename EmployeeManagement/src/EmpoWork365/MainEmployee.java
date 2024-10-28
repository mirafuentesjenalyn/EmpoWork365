/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package EmpoWork365;

import com.toedter.calendar.JMonthChooser;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JSpinner;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

/**
 *
 * @author jenal
 */
public class MainEmployee extends javax.swing.JFrame implements UserUpdateListener, LeaveSubmissionListener {
    private UserAuthenticate loggedInUser;
    private EditUserDetails editUserDetails;
    private Connection connection;
    private boolean hasClockedIn = false;
    private boolean hasClockedOut = false;
    private JDatePickerImpl datePickerStart;
    private JSpinner yearSpinner; 
    private final int REGULAR_HOURS_PER_MONTH;
    private static final int TOTAL_SICK_LEAVE = 5; 
    private static final int TOTAL_EMERGENCY_LEAVE = 5; 
    private static final int TOTAL_VACATION_LEAVE = 15; 
    public static MainEmployee instance;
    private UserAuthenticate user;
    private int totalAbsences;

    
    /**
     * Creates new form Login
     */
    public MainEmployee() {
        setUndecorated(true);
        setResizable(false);
        TitleBar titleBar = new TitleBar(this);

        titleBar.setPreferredSize(new Dimension(1000, 83)); // Adjust height as needed

        // Add title bar and content panel to the frame
        setLayout(new BorderLayout());
        add(titleBar, BorderLayout.NORTH);

        setSize(1000, 800);
        setLocationRelativeTo(null);

        
        ImageIcon icon = IconLoader.getIcon();
        Image img = icon.getImage();
        
        setIconImage(img);
        
        
        initComponents();
        this.REGULAR_HOURS_PER_MONTH  = 160;
        instance = this;
        
        this.loggedInUser = user;
        setUserDetails(user, totalAbsences);
        loadPayrollDetails();
        searchEmployeeLeave();
        setupDatePickers();
        initializeComboBoxPresentAbsent();
        setupMonthChooserListener(monthChooser);
        yearSpinner = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(Calendar.getInstance().get(Calendar.YEAR), 2000, 2100, 1));

        addButtonHoverEffect(btnHome);
        addButtonHoverEffect(btnAttSum);
        addButtonHoverEffect(btnPayroll);
        addButtonHoverEffect(btnLeaveSum);

        try {
            sqlConnector connector = new sqlConnector();
            this.connection = connector.createConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to the database: " + e.getMessage());
        }
        
        searchNameTxt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                performSearchSafely();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                performSearchSafely();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                performSearchSafely();
            }
        });

        searchNameTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearchSafely();
                }
            }
        });

    }
    
    public void performSearchSafely() {
        try {
            performSearch(); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error while performing search: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public MainEmployee(UserAuthenticate loggedInUser) throws SQLException {
        this(); 
        this.loggedInUser = loggedInUser; 
        loadPayrollDetails();
        loadEmployeeAttendanceById();
        loadEmployeeLeave();
        checkAttendanceStatus(); 
    }

    @Override
    public void onUserUpdated(UserAuthenticate updatedUser) {
        EmployeeMethod employeeMethod = new EmployeeMethod(connection);

        // Create an Employee object based on the updatedUser's ID
        Employee employee = new Employee();
        employee.setEmployeeId(updatedUser.getId());

        // Get total absences for the updated employee
        int totalAbsent = employeeMethod.getTotalAbsences(employee);

        // Update user details with the total absences
        setUserDetails(updatedUser, totalAbsent);
    }

 
    @Override
    public void onLeaveSubmitted() {
        try { 
            loadEmployeeLeave();
        } catch (SQLException ex) {
            Logger.getLogger(MainEmployee.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public void setAuthenticatedUser(UserAuthenticate loggedInUser) throws SQLException {
        this.loggedInUser = loggedInUser;

        // Create Employee object
        Employee employee = new Employee();
        employee.setEmployeeId(loggedInUser.getId());

        EmployeeMethod employeeMethod = new EmployeeMethod(connection);
        int totalAbsent= employeeMethod.getTotalAbsences(employee);

        setUserDetails(loggedInUser, totalAbsent);
        loadEmployeeAttendanceById();
        loadEmployeeLeave(); 
        checkAttendanceStatus(); 
    }
    
    private void setUserDetails(UserAuthenticate user, int totalAbsences) {
        if (user != null) {
            System.out.println("Setting user details for: " + user.getFirstname() + " " + user.getLastname());
            fullName.setText(user.getFirstname().toUpperCase() + " " + user.getLastname().toUpperCase());
            userWelcome.setText(user.getFirstname() + "!");
            userJobTitle.setText(user.getJobtitle().toUpperCase());
            userRole.setText(user.getRoleName().toUpperCase() + " PROFILE");
            employeeIdLabel.setText(String.valueOf(user.getId()));
            ImageIcon userImage = resizeImage(user.getImagepath(), 100, 100);
            userImageIcon.setIcon(userImage);
            firstNameLabel.setText(user.getFirstname().toUpperCase());
            lastNameLabel.setText(user.getLastname().toUpperCase());
            genderLabel.setText(user.getGender().toUpperCase());
            emailLabel.setText(user.getEmail().toUpperCase());
            departmentLabel.setText(user.getDepartmentName().toUpperCase());

            totalCountAbsent.setText(String.valueOf(totalAbsences));
        } else {
            System.out.println("User is null");
        }
    }

    private void loadEmployeeAttendanceById() {
        if (loggedInUser == null) {
            return;
        }

        try {
            int employeeId = loggedInUser.getId();
            EmployeeMethod employeeMethod = new EmployeeMethod(connection);
            DefaultTableModel model = employeeMethod.getAttendanceDataById(employeeId);
            setTableModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load employee attendance data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void setTableModel(DefaultTableModel model) {
        DefaultTableModel nonEditableModel = new DefaultTableModel(model.getDataVector(), getColumnNames(model)) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        jTable2.setModel(nonEditableModel);
    }
      
    private void loadEmployeeLeave() throws SQLException {
        int employeeId = loggedInUser.getId();
        EmployeeMethod employeeMethod = new EmployeeMethod(connection);
        var model = employeeMethod.viewLeaveApplications(employeeId);
        setTableModel(model, leaveTable);
    }
    
        
    private void searchEmployeeLeave() {
        try {
            if (loggedInUser == null) {
                return;
            }
        
            int employeeId = loggedInUser.getId();
            String searchTerm = searchNameTxt.getText(); 

            EmployeeMethod employeeMethod = new EmployeeMethod(connection);
            var model = employeeMethod.searchLeaveApplications(employeeId, searchTerm);

            setTableModel(model, leaveTable); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load employee data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void performSearch() throws SQLException {
        String searchTerm = searchNameTxt.getText().trim();
        if (searchTerm.isEmpty() || searchTerm.equals("Search")) {
        loadEmployeeLeave();
        } else {
            searchEmployeeLeave(); 
        }
    }
        
    private void setTableModel(DefaultTableModel model, javax.swing.JTable leaveTable) {
        DefaultTableModel nonEditableModel = new DefaultTableModel(model.getDataVector(), getColumnNames(model)) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        leaveTable.setModel(nonEditableModel);
    }
      
    private Vector<String> getColumnNames(DefaultTableModel model) {
        Vector<String> columnNames = new Vector<>();
        for (int i = 0; i < model.getColumnCount(); i++) {
            columnNames.add(model.getColumnName(i));
        }
        return columnNames;
    }
    
    private void addButtonHoverEffect(javax.swing.JButton button) {
        button.setOpaque(true);
        button.setBackground(new Color(10,60,89));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0,36,57));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(10,60,89));
            }
        });
    }
    
    private ImageIcon resizeImage(String imagePath, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image originalImage = originalIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return new ImageIcon(resizedImage);
    }
   
    public void setLoggedInUser(UserAuthenticate user) {
        this.loggedInUser = user; 
    }


    private void checkAttendanceStatus() {
        if (loggedInUser == null) {
            return;
        }
        AttendanceMethod attendanceMethod = new AttendanceMethod(connection);
        hasClockedIn = attendanceMethod.hasClockedIn(loggedInUser.getId());
        hasClockedOut = attendanceMethod.hasClockedOut(loggedInUser.getId());
        updateAttendanceButtons(); 
    }

    private void updateAttendanceButtons() {
           btnTimeIn.setEnabled(!hasClockedIn); 
           btnTimeOut.setEnabled(hasClockedIn && !hasClockedOut);
    }
    
    public void recordTimeIn() {
        try {
            AttendanceMethod attendanceMethod = new AttendanceMethod(connection);
            attendanceMethod.recordTimeIn(loggedInUser.getId()); 
            hasClockedIn = true;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
            String formattedTimeIn = sdf.format(new java.util.Date());

            JOptionPane.showMessageDialog(this, "Time In recorded: " + formattedTimeIn, "Success", JOptionPane.INFORMATION_MESSAGE);
            updateAttendanceButtons();
             loadEmployeeAttendanceById(); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error recording Time In: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void recordTimeOut() {
        try {
            AttendanceMethod attendanceMethod = new AttendanceMethod(connection);
            attendanceMethod.recordTimeOut(loggedInUser.getId()); 
            hasClockedOut = true;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
            String formattedTimeIn = sdf.format(new java.util.Date());

            JOptionPane.showMessageDialog(this, "Time Out recorded: " + formattedTimeIn, "Success", JOptionPane.INFORMATION_MESSAGE);
            updateAttendanceButtons();
            loadEmployeeAttendanceById();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error recording Time Out: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        jPanel11 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        sideBar = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnHome = new javax.swing.JButton();
        btnAttSum = new javax.swing.JButton();
        btnLeaveSum = new javax.swing.JButton();
        btnPayroll = new javax.swing.JButton();
        fullName = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        userImageIcon = new javax.swing.JLabel();
        logout = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        home = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
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
        attendanceLabel = new javax.swing.JLabel();
        userRole = new javax.swing.JLabel();
        btnEditProfile = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnTimeIn = new javax.swing.JButton();
        btnTimeOut = new javax.swing.JButton();
        btnLeave = new javax.swing.JButton();
        trackingAttendance = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        comboBoxPresentAbsent = new javax.swing.JComboBox<>();
        datePanelPicker = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        totalCountAbsentLabel = new javax.swing.JLabel();
        totalCountAbsent = new javax.swing.JLabel();
        leave = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        searchNameTxt = new javax.swing.JTextField();
        searchBarEmployeeName = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        leaveTable = new javax.swing.JTable();
        jPanel17 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        managePayroll = new javax.swing.JPanel();
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
        totalDeducTextField = new javax.swing.JTextField();
        overtimeHrsTextField = new javax.swing.JTextField();
        leaveBalanceLabel = new javax.swing.JLabel();
        leaveBalanceTextField = new javax.swing.JTextField();
        unpaidLeaveLabel2 = new javax.swing.JLabel();
        sickLeaveTextField = new javax.swing.JTextField();
        unpaidLeaveLabel3 = new javax.swing.JLabel();
        emergencyLeaveTextField = new javax.swing.JTextField();
        thirteenthMonthPayLabel = new javax.swing.JLabel();
        thirteenthMonthPayTextField = new javax.swing.JTextField();
        netSalaryTextField = new javax.swing.JTextField();
        netSalaryLabel = new javax.swing.JLabel();
        vacationLeaveLabel = new javax.swing.JLabel();
        vacationLeaveTextField = new javax.swing.JTextField();
        totalSalaryPerMonthLabel = new javax.swing.JLabel();
        totalSalaryPerMonthTextField = new javax.swing.JTextField();
        totalAbsenceLabel = new javax.swing.JLabel();
        totalAbsenceTextField = new javax.swing.JTextField();
        unusedLeaveLabel = new javax.swing.JLabel();
        unusedLeaveTextField = new javax.swing.JTextField();
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
        monthChooser = new com.toedter.calendar.JMonthChooser();
        btnReceiptPayroll = new javax.swing.JButton();
        jPanel18 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        jPanel11.setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sideBar.setBackground(new java.awt.Color(10, 60, 89));
        sideBar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(10, 60, 89));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(10, 60, 89));

        btnHome.setBackground(new java.awt.Color(102, 102, 102));
        btnHome.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnHome.setForeground(new java.awt.Color(255, 255, 255));
        btnHome.setText("      Home");
        btnHome.setBorder(null);
        btnHome.setContentAreaFilled(false);
        btnHome.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHome.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHome.setRequestFocusEnabled(false);
        btnHome.setRolloverEnabled(false);
        btnHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeActionPerformed(evt);
            }
        });

        btnAttSum.setBackground(new java.awt.Color(102, 102, 102));
        btnAttSum.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnAttSum.setForeground(new java.awt.Color(255, 255, 255));
        btnAttSum.setText("      Attendance Summary");
        btnAttSum.setBorder(null);
        btnAttSum.setContentAreaFilled(false);
        btnAttSum.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAttSum.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAttSum.setRequestFocusEnabled(false);
        btnAttSum.setRolloverEnabled(false);
        btnAttSum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAttSumActionPerformed(evt);
            }
        });

        btnLeaveSum.setBackground(new java.awt.Color(102, 102, 102));
        btnLeaveSum.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnLeaveSum.setForeground(new java.awt.Color(255, 255, 255));
        btnLeaveSum.setText("      Leave Summary");
        btnLeaveSum.setBorder(null);
        btnLeaveSum.setContentAreaFilled(false);
        btnLeaveSum.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLeaveSum.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLeaveSum.setRequestFocusEnabled(false);
        btnLeaveSum.setRolloverEnabled(false);
        btnLeaveSum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeaveSumActionPerformed(evt);
            }
        });

        btnPayroll.setBackground(new java.awt.Color(102, 102, 102));
        btnPayroll.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnPayroll.setForeground(new java.awt.Color(255, 255, 255));
        btnPayroll.setText("      Access Payroll");
        btnPayroll.setBorder(null);
        btnPayroll.setContentAreaFilled(false);
        btnPayroll.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPayroll.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPayroll.setRequestFocusEnabled(false);
        btnPayroll.setRolloverEnabled(false);
        btnPayroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPayrollActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnAttSum, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
            .addComponent(btnHome, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnLeaveSum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnPayroll, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnHome, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAttSum, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLeaveSum, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnPayroll, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(68, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 204, 210, 230));

        fullName.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        fullName.setForeground(new java.awt.Color(255, 255, 255));
        fullName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fullName.setText("NAME");
        jPanel3.add(fullName, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 204, -1));

        jPanel1.setBackground(new java.awt.Color(10, 60, 89));

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
        jPanel3.add(logout, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 610, -1, -1));

        sideBar.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 740));

        jPanel5.add(sideBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 740));

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));

        home.setBackground(new java.awt.Color(255, 255, 255));
        home.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel13.setBackground(new java.awt.Color(64, 116, 147));

        jLabel1.setBackground(new java.awt.Color(192, 213, 249));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(192, 213, 249));
        jLabel1.setText("Hi,");

        userWelcome.setBackground(new java.awt.Color(192, 213, 249));
        userWelcome.setFont(new java.awt.Font("Arial", 1, 30)); // NOI18N
        userWelcome.setForeground(new java.awt.Color(192, 213, 249));
        userWelcome.setText("user");

        jLabel6.setBackground(new java.awt.Color(192, 213, 249));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(192, 213, 249));
        jLabel6.setText("USER ID:");

        employeeIdLabel.setBackground(new java.awt.Color(192, 213, 249));
        employeeIdLabel.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        employeeIdLabel.setForeground(new java.awt.Color(192, 213, 249));
        employeeIdLabel.setText("Number");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(userWelcome, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(6, 6, 6)
                        .addComponent(employeeIdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(461, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(83, Short.MAX_VALUE)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(userWelcome))
                .addGap(6, 6, 6)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(employeeIdLabel))
                .addGap(29, 29, 29))
        );

        home.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 790, 170));

        jPanel7.setBackground(new java.awt.Color(240, 240, 240));

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
                .addGap(17, 17, 17)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(firstNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(userJobTitle, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(genderLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(emailLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lastNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(departmentLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(24, Short.MAX_VALUE))
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
                .addGap(20, 20, 20))
        );

        home.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 410, -1, -1));

        attendanceLabel.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        attendanceLabel.setForeground(new java.awt.Color(9, 53, 53));
        attendanceLabel.setText("DAILY WORK LOG");
        home.add(attendanceLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(55, 200, 410, 42));

        userRole.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        userRole.setForeground(new java.awt.Color(9, 53, 53));
        userRole.setText("ROLE");
        home.add(userRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 370, 397, -1));

        btnEditProfile.setBackground(new java.awt.Color(8, 127, 127));
        btnEditProfile.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEditProfile.setForeground(new java.awt.Color(255, 255, 255));
        btnEditProfile.setText("EDIT PROFILE");
        btnEditProfile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditProfile.setFocusable(false);
        btnEditProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditProfileActionPerformed(evt);
            }
        });
        home.add(btnEditProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 350, 151, 45));

        jSeparator1.setBackground(new java.awt.Color(20, 165, 165));
        jSeparator1.setForeground(new java.awt.Color(20, 165, 165));
        home.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 319, 692, 10));

        btnTimeIn.setBackground(new java.awt.Color(34, 165, 102));
        btnTimeIn.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnTimeIn.setForeground(new java.awt.Color(255, 255, 255));
        btnTimeIn.setText("Time In");
        btnTimeIn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTimeIn.setFocusable(false);
        btnTimeIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimeInActionPerformed(evt);
            }
        });
        home.add(btnTimeIn, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 250, 116, 43));

        btnTimeOut.setBackground(new java.awt.Color(165, 61, 33));
        btnTimeOut.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnTimeOut.setForeground(new java.awt.Color(255, 255, 255));
        btnTimeOut.setText("Time Out");
        btnTimeOut.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTimeOut.setFocusable(false);
        btnTimeOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimeOutActionPerformed(evt);
            }
        });
        home.add(btnTimeOut, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 250, 111, 43));

        btnLeave.setBackground(new java.awt.Color(235, 168, 80));
        btnLeave.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnLeave.setForeground(new java.awt.Color(255, 255, 255));
        btnLeave.setText("Leave");
        btnLeave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLeave.setFocusable(false);
        btnLeave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeaveActionPerformed(evt);
            }
        });
        home.add(btnLeave, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 250, 150, 43));

        jTabbedPane1.addTab("tab1", home);

        trackingAttendance.setBackground(new java.awt.Color(255, 255, 255));
        trackingAttendance.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel14.setBackground(new java.awt.Color(64, 116, 147));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Track Attendance");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(233, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(251, 251, 251))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(75, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        trackingAttendance.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 790, 130));

        comboBoxPresentAbsent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxPresentAbsentActionPerformed(evt);
            }
        });
        trackingAttendance.add(comboBoxPresentAbsent, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 160, -1, 30));

        datePanelPicker.setBackground(new java.awt.Color(204, 204, 255));

        javax.swing.GroupLayout datePanelPickerLayout = new javax.swing.GroupLayout(datePanelPicker);
        datePanelPicker.setLayout(datePanelPickerLayout);
        datePanelPickerLayout.setHorizontalGroup(
            datePanelPickerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 35, Short.MAX_VALUE)
        );
        datePanelPickerLayout.setVerticalGroup(
            datePanelPickerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        trackingAttendance.add(datePanelPicker, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 160, -1, -1));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Time-In", "Time-Out", "Date"
            }
        ));
        jScrollPane2.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setMinWidth(30);
            jTable2.getColumnModel().getColumn(0).setMaxWidth(30);
        }

        trackingAttendance.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 214, 772, 550));

        totalCountAbsentLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        totalCountAbsentLabel.setForeground(new java.awt.Color(10, 60, 89));
        totalCountAbsentLabel.setText("Total Count of Absence:");
        trackingAttendance.add(totalCountAbsentLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, -1, -1));

        totalCountAbsent.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        totalCountAbsent.setForeground(new java.awt.Color(10, 60, 89));
        totalCountAbsent.setText("jLabel");
        trackingAttendance.add(totalCountAbsent, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 160, -1, -1));

        jTabbedPane1.addTab("tab3", trackingAttendance);

        leave.setBackground(new java.awt.Color(255, 255, 255));
        leave.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(1, 113, 132)));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        jPanel6.add(searchNameTxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 220, 30));

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
        jPanel6.add(searchBarEmployeeName, new org.netbeans.lib.awtextra.AbsoluteConstraints(223, 7, 50, -1));

        leave.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 160, -1, 43));

        leaveTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Application ID", "Start Date", "Leave Type", "Reason", "Status", "Date Requested"
            }
        ));
        jScrollPane1.setViewportView(leaveTable);

        leave.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 228, 770, 540));

        jPanel17.setBackground(new java.awt.Color(64, 116, 147));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Leave Request Summary");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap(233, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(251, 251, 251))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap(75, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        leave.add(jPanel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 790, 130));

        jTabbedPane1.addTab("tab1", leave);

        managePayroll.setBackground(new java.awt.Color(255, 255, 255));
        managePayroll.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        incomeTaxLabel.setText("Income Tax:");

        SSSTextField.setEditable(false);

        philHealthTextField.setEditable(false);

        pagibigLabel.setText("Pag-IBIG:");

        unpaidLeaveLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        unpaidLeaveLabel.setText("Unpaid Leave:");

        SSSLabel.setText("Social Security Sytem:");

        unpaidLeaveTextField.setEditable(false);

        totalDeducLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        totalDeducLabel.setText("Total Deduction:");

        philHealthLabel.setText("Philhealth:");

        pagibigTextField.setEditable(false);

        incomeTaxTextField.setEditable(false);

        overtimeHours_Label.setText("Overtime Hours");

        total_hours_worked_Label.setText("Total Hours Worked");

        HrsMonthTextField.setEditable(false);

        totalHrsWorkedTextField.setEditable(false);

        jLabel19.setText("Hours/Month");

        rateHourTextField.setEditable(false);

        getIdPayroll.setEditable(false);

        rateperHour_Label.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        rateperHour_Label.setText("Rate/Hour:");

        emp_idLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        emp_idLabel.setText("Employee ID:");

        totalDeducTextField.setEditable(false);
        totalDeducTextField.setBackground(new java.awt.Color(255, 206, 206));
        totalDeducTextField.setForeground(new java.awt.Color(0, 36, 57));

        overtimeHrsTextField.setEditable(false);

        leaveBalanceLabel.setText("Leave Balance:");

        leaveBalanceTextField.setEditable(false);

        unpaidLeaveLabel2.setText("Sick Leave (5):");

        sickLeaveTextField.setEditable(false);

        unpaidLeaveLabel3.setText("Emergency Leave (5):");

        emergencyLeaveTextField.setEditable(false);

        thirteenthMonthPayLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        thirteenthMonthPayLabel.setText("13th Month Pay:");

        thirteenthMonthPayTextField.setEditable(false);

        netSalaryTextField.setEditable(false);
        netSalaryTextField.setBackground(new java.awt.Color(201, 225, 255));
        netSalaryTextField.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        netSalaryTextField.setForeground(new java.awt.Color(0, 36, 57));

        netSalaryLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        netSalaryLabel.setText("Net Salary:");

        vacationLeaveLabel.setText("Vacation Leave (15):");

        vacationLeaveTextField.setEditable(false);

        totalSalaryPerMonthLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        totalSalaryPerMonthLabel.setText("Total Salary/Month:");

        totalSalaryPerMonthTextField.setEditable(false);

        totalAbsenceLabel.setText("Total Absence:");

        totalAbsenceTextField.setEditable(false);

        unusedLeaveLabel.setText("Unused Leave:");

        unusedLeaveTextField.setEditable(false);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(emp_idLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(getIdPayroll, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(rateperHour_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(rateHourTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(totalSalaryPerMonthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(totalSalaryPerMonthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(HrsMonthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(total_hours_worked_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(totalHrsWorkedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(overtimeHours_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(overtimeHrsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(totalDeducLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(totalDeducTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(philHealthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(philHealthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(SSSLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(SSSTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(pagibigLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(pagibigTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(incomeTaxLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(incomeTaxTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(unpaidLeaveLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(unpaidLeaveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(leaveBalanceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(leaveBalanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(unpaidLeaveLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(sickLeaveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(unpaidLeaveLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(emergencyLeaveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(vacationLeaveLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(vacationLeaveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(thirteenthMonthPayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(thirteenthMonthPayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(netSalaryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(netSalaryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(totalAbsenceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(totalAbsenceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(unusedLeaveLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(unusedLeaveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(emp_idLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(getIdPayroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rateperHour_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rateHourTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(totalSalaryPerMonthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalSalaryPerMonthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(HrsMonthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(total_hours_worked_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(totalHrsWorkedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(overtimeHours_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(overtimeHrsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(totalDeducLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalDeducTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(unpaidLeaveLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unpaidLeaveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(totalAbsenceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalAbsenceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(unpaidLeaveLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sickLeaveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(unpaidLeaveLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emergencyLeaveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vacationLeaveLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vacationLeaveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(leaveBalanceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(leaveBalanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(unusedLeaveLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unusedLeaveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(thirteenthMonthPayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(thirteenthMonthPayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(netSalaryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(netSalaryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        managePayroll.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, -1, 680));

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        departmentTextField.setEditable(false);
        jPanel10.add(departmentTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 390, 260, -1));

        departmentNameLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        departmentNameLabel.setText("Department:");
        jPanel10.add(departmentNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 390, 90, 26));

        jobTitleLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jobTitleLabel.setText("Job Title:");
        jPanel10.add(jobTitleLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 90, 26));

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel32.setText("Gender:");
        jPanel10.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 90, 26));

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel31.setText("Email:");
        jPanel10.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, 90, 26));

        nameLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        nameLabel.setText("Name:");
        jPanel10.add(nameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 90, 26));

        nameTextField.setEditable(false);
        jPanel10.add(nameTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 230, 260, -1));

        emailTextField.setEditable(false);
        jPanel10.add(emailTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 270, 260, -1));

        genderTextField.setEditable(false);
        jPanel10.add(genderTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 310, 260, -1));

        jobTitleTextField.setEditable(false);
        jPanel10.add(jobTitleTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 350, 260, -1));

        jPanel8.setBackground(new java.awt.Color(204, 204, 255));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        userImagePayroll.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        userImagePayroll.setMaximumSize(new java.awt.Dimension(100, 100));
        userImagePayroll.setMinimumSize(new java.awt.Dimension(100, 100));
        userImagePayroll.setPreferredSize(new java.awt.Dimension(100, 100));
        jPanel8.add(userImagePayroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 105, 105));

        jPanel10.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, -1, -1));

        btnClear2.setBackground(new java.awt.Color(71, 146, 146));
        btnClear2.setForeground(new java.awt.Color(204, 255, 255));
        btnClear2.setText("Clear All");
        btnClear2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClear2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClear2ActionPerformed(evt);
            }
        });
        jPanel10.add(btnClear2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 20));
        jPanel10.add(monthChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 4, 120, -1));

        btnReceiptPayroll.setText("Print");
        btnReceiptPayroll.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReceiptPayroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReceiptPayrollActionPerformed(evt);
            }
        });
        jPanel10.add(btnReceiptPayroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 40, 120, 20));

        managePayroll.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 130, 364, 430));

        jPanel18.setBackground(new java.awt.Color(64, 116, 147));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Access Payroll");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                .addContainerGap(233, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(251, 251, 251))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                .addContainerGap(77, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addContainerGap())
        );

        managePayroll.add(jPanel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 790, 110));

        jTabbedPane1.addTab("tab4", managePayroll);

        jPanel4.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -40, 790, 820));

        jPanel5.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, -40, 790, 780));

        jPanel11.add(jPanel5, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel11, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    
    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        jTabbedPane1.setSelectedIndex(0);
        
    }//GEN-LAST:event_btnHomeActionPerformed

    private void btnAttSumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAttSumActionPerformed
        jTabbedPane1.setSelectedIndex(1);
        
    }//GEN-LAST:event_btnAttSumActionPerformed

    private void btnPayrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayrollActionPerformed
        jTabbedPane1.setSelectedIndex(3);
       
       
    }//GEN-LAST:event_btnPayrollActionPerformed

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

    private void btnTimeInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimeInActionPerformed
        if (loggedInUser == null) {
            JOptionPane.showMessageDialog(this, "User is not authenticated. Please log in again.", "Error", JOptionPane.ERROR_MESSAGE);
            return; 
        }
        if (!hasClockedIn) {
            recordTimeIn(); 
        } else {
            JOptionPane.showMessageDialog(this, "You have already clocked in.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnTimeInActionPerformed

    private void btnTimeOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimeOutActionPerformed
        if (loggedInUser == null) {
            JOptionPane.showMessageDialog(this, "User is not authenticated. Please log in again.", "Error", JOptionPane.ERROR_MESSAGE);
            return; 
        }
        if (!hasClockedOut) {
            recordTimeOut(); 
        } else {
            JOptionPane.showMessageDialog(this, "You have already clocked out.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnTimeOutActionPerformed

    private void btnEditProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditProfileActionPerformed
        if (editUserDetails == null) {
            editUserDetails = new EditUserDetails(null, this);
        }
        editUserDetails.setUserUpdateListener(this); 
        editUserDetails.UpdateUserDetails(loggedInUser);
        editUserDetails.setVisible(true);
    }//GEN-LAST:event_btnEditProfileActionPerformed

    private void btnLeaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeaveActionPerformed
        LeaveForm leaveForm = new LeaveForm(loggedInUser);
        leaveForm.setLeaveSubmissionListener(this); 
        leaveForm.setVisible(true);
    }//GEN-LAST:event_btnLeaveActionPerformed

    private void btnLeaveSumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeaveSumActionPerformed
        jTabbedPane1.setSelectedIndex(2);
    }//GEN-LAST:event_btnLeaveSumActionPerformed

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
        performSearchSafely();
    }//GEN-LAST:event_searchBarEmployeeNameActionPerformed
    
    private void clearDatePicker() {
        datePickerStart.getModel().setValue(null); // Reset the date picker model
    }
    
    private void setupDatePickers() {
        UtilDateModel modelStart = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        JDatePanelImpl datePanelStart = new JDatePanelImpl(modelStart, p);
        datePickerStart = new JDatePickerImpl(datePanelStart, new MainEmployee.DateLabelFormatter());

        if (datePanelPicker != null) {
            datePanelPicker.setLayout(new java.awt.BorderLayout());
            datePanelPicker.add(datePickerStart, java.awt.BorderLayout.CENTER);
            datePanelPicker.revalidate();  
            datePanelPicker.repaint();
        }
    }

    private String getMonthName(int month) {
        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        return monthNames[month - 1]; // Adjust for zero-based index (1-12)
    }
    
    public class DateLabelFormatter extends javax.swing.JFormattedTextField.AbstractFormatter {
        private final String datePattern = "MMMM dd, yyyy";
        private final java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws java.text.ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) throws java.text.ParseException {
            if (value != null) {
                java.util.Calendar cal = (java.util.Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "Select Date";
        }
    }
    
    private void initializeComboBoxPresentAbsent() {
        comboBoxPresentAbsent.addItem("All");
        comboBoxPresentAbsent.addItem("Present");
        comboBoxPresentAbsent.addItem("Incomplete");
        comboBoxPresentAbsent.addItem("Absent");
        
        comboBoxPresentAbsent.addActionListener(e -> fetchDataBasedOnStatus());
    }

    private void fetchDataBasedOnStatus() {
        if (loggedInUser == null) {
            return; 
        }

        String selectedStatus = (String) comboBoxPresentAbsent.getSelectedItem(); 
        Date selectedDate = (Date) datePickerStart.getModel().getValue();

        if (selectedStatus != null) {
            // If no date is selected, fetch data based on the status only
            if (selectedDate != null) {
                // Fetch attendance data based on the selected status and date
                fetchDataBasedOnDateAndStatus(selectedDate, selectedStatus);
            } else {
                // Fetch all attendance records for the selected status
                fetchDataBasedOnStatusOnly(selectedStatus);
            }
        }
   }
    
    private void fetchDataBasedOnDateAndStatus(Date selectedDate, String selectedStatus) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(selectedDate);
            int employeeId = loggedInUser.getId();

            EmployeeMethod employeeMethod = new EmployeeMethod(connection);
            DefaultTableModel modelTable = employeeMethod.getFilteredAttendanceByDateAndStatus(employeeId, formattedDate, selectedStatus);

            setTableModel(modelTable, jTable2);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to filter attendance data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
   private void fetchDataBasedOnStatusOnly(String selectedStatus) {
        try {
            int employeeId = loggedInUser.getId();
            EmployeeMethod employeeMethod = new EmployeeMethod(connection);
            DefaultTableModel modelTable;

            // Check for the "All" status
            if ("All".equals(selectedStatus)) {
                // Fetch all attendance records without filtering by status
                modelTable = employeeMethod.getAllAttendanceRecords(employeeId); // Implement this method in EmployeeMethod
            } else {
                modelTable = employeeMethod.getAttendanceByStatus(employeeId, selectedStatus); // Existing method for Present, Absent, Incomplete
            }

            setTableModel(modelTable, jTable2);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to filter attendance data by status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void comboBoxPresentAbsentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxPresentAbsentActionPerformed
        if (loggedInUser == null) {
            return; 
        }

        String selectedStatus = (String) comboBoxPresentAbsent.getSelectedItem();

        if (selectedStatus == null || selectedStatus.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a status (Present, Absent, or Incomplete).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Clear the date if necessary
        if (datePickerStart.getModel().getValue() == null) {
            clearDatePicker(); // Reset date picker if needed
        }

        Date selectedDate = (Date) datePickerStart.getModel().getValue();
        if (selectedDate != null) {
            fetchDataBasedOnDateAndStatus(selectedDate, selectedStatus);
        } else {
            fetchDataBasedOnStatusOnly(selectedStatus);
        }
    }//GEN-LAST:event_comboBoxPresentAbsentActionPerformed

    private void btnClear2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClear2ActionPerformed
        clearFields();
    }//GEN-LAST:event_btnClear2ActionPerformed

    private void btnReceiptPayrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceiptPayrollActionPerformed
        Receipt receiptFrame = new Receipt();
        Map<String, String> payrollDetails = getPayrollDetailsForReceipt();
        
        int selectedMonth = monthChooser.getMonth() + 1; // Adjust to 1-based month
        payrollDetails.put("Month", getMonthName(selectedMonth));
        
        receiptFrame.setPayrollDetails(payrollDetails);

        receiptFrame.setVisible(true);
    }//GEN-LAST:event_btnReceiptPayrollActionPerformed

    private Map<String, String> getPayrollDetailsForReceipt() {
        Map<String, String> payrollDetails = new HashMap<>();

        payrollDetails.put("Employee ID", getIdPayroll.getText());
        payrollDetails.put("Full Name", nameTextField.getText());
        payrollDetails.put("Email", emailTextField.getText());
        payrollDetails.put("Job Title", jobTitleTextField.getText());
        payrollDetails.put("Department", departmentTextField.getText());
        payrollDetails.put("Total Salary", totalSalaryPerMonthTextField.getText());
        payrollDetails.put("Rate per Hour", rateHourTextField.getText());
        payrollDetails.put("Regular Hours/Month", HrsMonthTextField.getText());
        payrollDetails.put("Total Hours Worked", totalHrsWorkedTextField.getText());
        payrollDetails.put("Overtime Hours", overtimeHrsTextField.getText());
        payrollDetails.put("Total Deductions", totalDeducTextField.getText());
        payrollDetails.put("PhilHealth Deduction", philHealthTextField.getText());
        payrollDetails.put("SSS Deduction", SSSTextField.getText());
        payrollDetails.put("Pag-IBIG Deduction", pagibigTextField.getText());
        payrollDetails.put("Income Tax", incomeTaxTextField.getText());
        payrollDetails.put("Unpaid Leave Cost", unpaidLeaveTextField.getText());
        payrollDetails.put("Leave Balance", leaveBalanceTextField.getText());
        payrollDetails.put("Unused Leave", unusedLeaveTextField.getText());
        payrollDetails.put("13th Month Pay", thirteenthMonthPayTextField.getText());
        payrollDetails.put("Net Salary", netSalaryTextField.getText());

        return payrollDetails;
    }
    
    private void loadPayrollDetails() {
        if (loggedInUser == null) {
            return;
        }

        try {
            int employeeId = loggedInUser.getId(); 
            EmployeeMethod employeeMethod = new EmployeeMethod(connection);

            Employee currentUser = employeeMethod.getLoggedInUser(employeeId); 

            if (currentUser != null) {
                getIdPayroll.setText(String.valueOf(employeeId));
                String FullName = currentUser.getFirstname() + " " + currentUser.getLastname();
                nameTextField.setText(FullName);
                emailTextField.setText(currentUser.getEmail());
                genderTextField.setText(currentUser.getGender());
                rateHourTextField.setText(String.valueOf(currentUser.getRatePerHour()));

                jobTitleTextField.setText(currentUser.getJobtitle());
                departmentTextField.setText(currentUser.getDepartmentName());
                HrsMonthTextField.setText(REGULAR_HOURS_PER_MONTH + " hrs");

                int selectedMonth = monthChooser.getMonth(); // Convert to 1-based month
                int selectedYear = (int) yearSpinner.getValue();
                String imagePath = currentUser.getImagePath();

                if (selectedMonth == 12) {
                    updateDecemberDetails(currentUser, selectedYear);
                } else {
                    updateRegularPayroll(currentUser, selectedMonth + 1, selectedYear, currentUser.getRatePerHour());
                }

                // Update image if available
                if (imagePath != null && !imagePath.isEmpty()) {
                    ImageIcon originalIcon = new ImageIcon(imagePath);
                    int width = 100;
                    int height = 100;
                    Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    userImagePayroll.setIcon(new ImageIcon(scaledImage));
                } else {
                    userImagePayroll.setIcon(new ImageIcon("src/Users/user.png"));
                }

            } else {
                JOptionPane.showMessageDialog(this, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading payroll details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private Employee getEmployee() {
        Employee employee = null;

        String employeeIdText = getIdPayroll.getText().trim();
        if (!employeeIdText.isEmpty()) {
            try {
                int employeeId = Integer.parseInt(employeeIdText);
                EmployeeMethod employeeMethod = new EmployeeMethod(connection);
                employee = employeeMethod.getEmployeeIdById(employeeId);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid Employee ID format.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } 
        // Otherwise, retrieve employee by name if nameTextField is not empty
        else if (!nameTextField.getText().trim().isEmpty()) {
            String name = nameTextField.getText().trim();
            EmployeeMethod employeeMethod = new EmployeeMethod(connection);
            try {
                employee = employeeMethod.getEmployeeByName(name);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        return employee;
    }
    
    private void setupMonthChooserListener(JMonthChooser monthChooser) {
        monthChooser.addPropertyChangeListener((java.beans.PropertyChangeEvent evt) -> {
            if ("month".equals(evt.getPropertyName())) {
                int selectedMonth = monthChooser.getMonth() + 1; 
                int selectedYear = (int) yearSpinner.getValue(); 

                loadPayrollDetails(); 

                Employee employee = getEmployee(); 

                if (employee == null) {
                    JOptionPane.showMessageDialog(null, "Employee data is null", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    clearDetails(false);
                    updateEmployeeDetailsForSelectedMonth(selectedMonth, selectedYear);
                } catch (SQLException ex) {
                    Logger.getLogger(MainAdmin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }


    private void clearDetails(boolean isDecember) {
        totalSalaryPerMonthTextField.setText("");
        totalHrsWorkedTextField.setText("");
        overtimeHrsTextField.setText("");
        totalDeducTextField.setText("");
        philHealthTextField.setText("");
        SSSTextField.setText("");
        pagibigTextField.setText("");
        incomeTaxTextField.setText("");
        netSalaryTextField.setText("");
        unpaidLeaveTextField.setText("");
        totalAbsenceTextField.setText("");
        sickLeaveTextField.setText("");
        emergencyLeaveTextField.setText("");
        vacationLeaveTextField.setText("");
        leaveBalanceTextField.setText("");
        unusedLeaveTextField.setText("");
        thirteenthMonthPayTextField.setText("");
        
        if (isDecember) {
            // Make these fields visible in December
            unpaidLeaveTextField.setEnabled(true);
            totalAbsenceTextField.setEnabled(true);
            sickLeaveTextField.setEnabled(true);
            emergencyLeaveTextField.setEnabled(true);
            vacationLeaveTextField.setEnabled(true);
            leaveBalanceTextField.setEnabled(true);
            unusedLeaveTextField.setEnabled(true);
            thirteenthMonthPayTextField.setEnabled(true);
        } else {
            // Hide these fields for all months except December
            unpaidLeaveTextField.setEnabled(false);
            totalAbsenceTextField.setEnabled(false);
            sickLeaveTextField.setEnabled(false);
            emergencyLeaveTextField.setEnabled(false);
            vacationLeaveTextField.setEnabled(false);
            leaveBalanceTextField.setEnabled(false);
            unusedLeaveTextField.setEnabled(false);
            thirteenthMonthPayTextField.setEnabled(false);
        }
    }

    private void updateEmployeeDetailsForSelectedMonth(int selectedMonth, int selectedYear) throws SQLException {     
        System.out.println("update employee details: " + selectedMonth + ", Year: " + selectedYear);

        Employee employee = getEmployee(); 
        if (employee != null) {
            loadPayrollDetails(); 


            if (selectedMonth == 12) {
                clearDetails(true); 
                updateDecemberDetails(employee, selectedYear); 
            } else {
                clearDetails(false); 
                updateRegularPayroll(employee, selectedMonth, selectedYear, employee.getRatePerHour());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Employee data is null", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRegularPayroll(Employee employee, int month, int year, BigDecimal ratePerHour) {
        // Calculate total hours worked using BigDecimal
        BigDecimal totalHoursWorked = calculateTotalHoursWorked(employee, month, year).setScale(4, RoundingMode.HALF_UP);
        totalHrsWorkedTextField.setText(String.format("%.2f hrs", totalHoursWorked.doubleValue())); // Display as double

        // Calculate overtime hours
        BigDecimal overtimeHours = calculateOvertimeHours(totalHoursWorked).setScale(4, RoundingMode.HALF_UP);
        overtimeHrsTextField.setText(String.format("%.2f hrs", overtimeHours.doubleValue())); // Display as double

        // Calculate total salary using BigDecimal
        BigDecimal totalSalary = calculateTotalSalary(ratePerHour, overtimeHours).setScale(4, RoundingMode.HALF_UP);
        totalSalaryPerMonthTextField.setText(formatCurrency(totalSalary));
        
        // Calculate net salary without unpaid leave cost for months January to November
        BigDecimal netSalary = calculateNetSalary(totalSalary, BigDecimal.ZERO, BigDecimal.ZERO, false).setScale(4, RoundingMode.HALF_UP);
        netSalaryTextField.setText(formatCurrency(netSalary));


        updateDeductionUI(totalSalary, BigDecimal.ZERO);
    }

    private void updateDecemberDetails(Employee employee, int year) throws SQLException {
        System.out.println("Update December details: " + employee.getEmployeeId() + ", Year: " + year);

        EmployeeMethod employeeOption = new EmployeeMethod(connection);
        AttendanceMethod attendanceMethod = new AttendanceMethod(connection);

        // Fetch total hours worked in December using BigDecimal
        BigDecimal totalHoursWorked = BigDecimal.valueOf(attendanceMethod.getTotalHoursWorkedInMonth(employee.getEmployeeId(), 12, year))
                                                .setScale(4, RoundingMode.HALF_UP);
        totalHrsWorkedTextField.setText(String.format("%.2f hrs", totalHoursWorked.doubleValue())); // Display as double

        // Calculate overtime hours
        BigDecimal overtimeHours = calculateOvertimeHours(totalHoursWorked).setScale(4, RoundingMode.HALF_UP);
        overtimeHrsTextField.setText(String.format("%.2f hrs", overtimeHours.doubleValue())); // Display as double

        BigDecimal ratePerHour = employee.getRatePerHour(); // Get rate per hour as BigDecimal
        BigDecimal totalSalary = calculateTotalSalary(ratePerHour, overtimeHours).setScale(4, RoundingMode.HALF_UP);
        totalSalaryPerMonthTextField.setText(formatCurrency(totalSalary));

        // Calculate total absences
        int totalAbsence = employeeOption.getTotalAbsences(employee);
        totalAbsenceTextField.setText(String.valueOf(totalAbsence)); // Display total absences

        // Calculate unpaid leave cost, including total absences
        BigDecimal unpaidLeaveCost = calculateUnpaidLeave(ratePerHour, totalAbsence).setScale(4, RoundingMode.HALF_UP);
        unpaidLeaveTextField.setText(formatCurrency(unpaidLeaveCost));

        updateDeductionUI(totalSalary, unpaidLeaveCost);
        
        // Remaining leave days and leave balance
        Map<String, Integer> remainingLeaveDays = employeeOption.getRemainingLeaveDays(employee.getEmployeeId());
        sickLeaveTextField.setText(String.valueOf(remainingLeaveDays.getOrDefault("Sick Leave", TOTAL_SICK_LEAVE)));
        emergencyLeaveTextField.setText(String.valueOf(remainingLeaveDays.getOrDefault("Emergency Leave", TOTAL_EMERGENCY_LEAVE)));
        vacationLeaveTextField.setText(String.valueOf(remainingLeaveDays.getOrDefault("Vacation Leave", TOTAL_VACATION_LEAVE)));

        // Calculate total leave balance
        int totalLeaveBalance = remainingLeaveDays.getOrDefault("Sick Leave", TOTAL_SICK_LEAVE)
                                    + remainingLeaveDays.getOrDefault("Emergency Leave", TOTAL_EMERGENCY_LEAVE)
                                    + remainingLeaveDays.getOrDefault("Vacation Leave", TOTAL_VACATION_LEAVE);
        leaveBalanceTextField.setText(totalLeaveBalance + " days");

        BigDecimal unusedLeaveCost = calculateUnusedLeave(employee).setScale(4, RoundingMode.HALF_UP);
        unusedLeaveTextField.setText(formatCurrency(unusedLeaveCost));

        // Calculate basic salary for the year and 13th month pay
        BigDecimal basicSalaryForYear = ratePerHour.multiply(BigDecimal.valueOf(REGULAR_HOURS_PER_MONTH)).multiply(BigDecimal.valueOf(12)).setScale(4, RoundingMode.HALF_UP);
        BigDecimal thirteenthMonthPay = basicSalaryForYear.divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP).setScale(4, RoundingMode.HALF_UP);
        thirteenthMonthPayTextField.setText(formatCurrency(thirteenthMonthPay));

        // Calculate net salary including unpaid leave cost
        BigDecimal netSalary = calculateNetSalary(totalSalary, unpaidLeaveCost, unusedLeaveCost, true).add(thirteenthMonthPay).setScale(4, RoundingMode.HALF_UP);
        netSalaryTextField.setText(formatCurrency(netSalary));
    }

    private BigDecimal calculateUnusedLeave(Employee employee) throws SQLException {
        EmployeeMethod employeeMethod = new EmployeeMethod(connection);
        Map<String, Integer> remainingLeaveDays = employeeMethod.getRemainingLeaveDays(employee.getEmployeeId());

        // Fetch leave balances or use defaults if not found
        int sickLeaveBalance = remainingLeaveDays.getOrDefault("Sick Leave", TOTAL_SICK_LEAVE);
        int emergencyLeaveBalance = remainingLeaveDays.getOrDefault("Emergency Leave", TOTAL_EMERGENCY_LEAVE);
        int vacationLeaveBalance = remainingLeaveDays.getOrDefault("Vacation Leave", TOTAL_VACATION_LEAVE);

        // Total unused leave balance
        int unusedLeaveBalance = sickLeaveBalance + emergencyLeaveBalance + vacationLeaveBalance;

        // Calculate unused leave benefit (7 hours per day, excluding lunch)
        BigDecimal dailyRate = employee.getRatePerHour().multiply(BigDecimal.valueOf(7)).setScale(4, RoundingMode.HALF_UP);
        return dailyRate.multiply(BigDecimal.valueOf(unusedLeaveBalance)).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalHoursWorked(Employee employee, int month, int year) {
        AttendanceMethod attendanceMethod = new AttendanceMethod(connection);
        BigDecimal totalHours = BigDecimal.ZERO;

        try {
            // Get total hours worked, which should be fetched in a way that respects the 20 workdays rule
            totalHours = BigDecimal.valueOf(attendanceMethod.getTotalHoursWorkedInWorkdays(employee.getEmployeeId(), month, year));
        } catch (SQLException ex) {
            Logger.getLogger(MainAdmin.class.getName()).log(Level.SEVERE, null, ex);
        }

        return totalHours;
    }

    private BigDecimal calculateOvertimeHours(BigDecimal totalHoursWorked) {
        BigDecimal overtimeHours = totalHoursWorked.subtract(BigDecimal.valueOf(REGULAR_HOURS_PER_MONTH)).setScale(4, RoundingMode.HALF_UP);
        return overtimeHours.max(BigDecimal.ZERO); // Ensure no negative overtime
    }


    private BigDecimal calculateTotalSalary(BigDecimal ratePerHour, BigDecimal overtimeHours) {
        BigDecimal regularSalary = ratePerHour.multiply(BigDecimal.valueOf(REGULAR_HOURS_PER_MONTH)).setScale(4, RoundingMode.HALF_UP);

        BigDecimal overtimeRate = ratePerHour.multiply(BigDecimal.valueOf(1.5)).setScale(4, RoundingMode.HALF_UP); 
        BigDecimal overtimeSalary = overtimeRate.multiply(overtimeHours).setScale(4, RoundingMode.HALF_UP);

        BigDecimal totalSalary = regularSalary.add(overtimeSalary).setScale(4, RoundingMode.HALF_UP);

        return totalSalary;
    }

    private void updateDeductionUI(BigDecimal totalSalary, BigDecimal unpaidLeaveCost) {
        BigDecimal philHealthDeduction = calculatePhilHealthDeduction(totalSalary);
        BigDecimal sssDeduction = calculateSSSDeduction(totalSalary);
        BigDecimal pagibigDeduction = calculatePagIbigDeduction(totalSalary);
        BigDecimal incomeTax = calculateIncomeTax(totalSalary);

        BigDecimal totalDeductions = philHealthDeduction.add(sssDeduction).add(pagibigDeduction).add(incomeTax).add(unpaidLeaveCost).setScale(4, RoundingMode.HALF_UP);

        philHealthTextField.setText(formatCurrency(philHealthDeduction));
        SSSTextField.setText(formatCurrency(sssDeduction));
        pagibigTextField.setText(formatCurrency(pagibigDeduction));
        incomeTaxTextField.setText(formatCurrency(incomeTax));
        totalDeducTextField.setText(formatCurrency(totalDeductions));
    }
    
    private BigDecimal calculateTotalDeductions(BigDecimal totalSalary) {
        BigDecimal philHealthDeduction = calculatePhilHealthDeduction(totalSalary);
        BigDecimal sssDeduction = calculateSSSDeduction(totalSalary);
        BigDecimal pagibigDeduction = calculatePagIbigDeduction(totalSalary);
        BigDecimal incomeTax = calculateIncomeTax(totalSalary);

        // Sum up all deductions
        return philHealthDeduction.add(sssDeduction).add(pagibigDeduction).add(incomeTax).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateNetSalary(BigDecimal totalSalary, BigDecimal unpaidLeaveCost, BigDecimal unusedLeaveCost, boolean isDecember) {
        // Calculate total deductions
        BigDecimal totalDeductions = calculateTotalDeductions(totalSalary); // Get base deductions without unpaid leave

        // If it's December, include the unpaid leave cost in the total deductions
        if (isDecember) {
            totalDeductions = totalDeductions.add(unpaidLeaveCost).setScale(4, RoundingMode.HALF_UP);
        }

        // Calculate net salary
        BigDecimal netSalary = totalSalary.subtract(totalDeductions).add(unusedLeaveCost).setScale(4, RoundingMode.HALF_UP); // Add unused leave cost

        // Validate net salary
        if (netSalary.compareTo(BigDecimal.ZERO) < 0) {
            System.err.println("Error: Negative net salary calculated!");
        }

        return netSalary;
    }
    
    private BigDecimal calculatePhilHealthDeduction(BigDecimal totalSalary) {
        return totalSalary.multiply(BigDecimal.valueOf(0.01)).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateSSSDeduction(BigDecimal totalSalary) {
        return totalSalary.multiply(BigDecimal.valueOf(0.02)).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePagIbigDeduction(BigDecimal totalSalary) {
        return totalSalary.compareTo(BigDecimal.valueOf(200.0)) <= 0
            ? totalSalary.multiply(BigDecimal.valueOf(0.01)).setScale(4, RoundingMode.HALF_UP)
            : totalSalary.multiply(BigDecimal.valueOf(0.02)).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateIncomeTax(BigDecimal salary) {
        if (salary.compareTo(BigDecimal.valueOf(250000.00)) <= 0) return BigDecimal.ZERO;
        else if (salary.compareTo(BigDecimal.valueOf(400000.00)) <= 0)
            return (salary.subtract(BigDecimal.valueOf(250000.00))).multiply(BigDecimal.valueOf(0.15)).setScale(4, RoundingMode.HALF_UP);
        else if (salary.compareTo(BigDecimal.valueOf(800000.00)) <= 0)
            return BigDecimal.valueOf(22500).add((salary.subtract(BigDecimal.valueOf(400000.00))).multiply(BigDecimal.valueOf(0.20))).setScale(4, RoundingMode.HALF_UP);
        else if (salary.compareTo(BigDecimal.valueOf(2000000.00)) <= 0)
            return BigDecimal.valueOf(102500).add((salary.subtract(BigDecimal.valueOf(800000.00))).multiply(BigDecimal.valueOf(0.25))).setScale(4, RoundingMode.HALF_UP);
        else if (salary.compareTo(BigDecimal.valueOf(8000000.00)) <= 0)
            return BigDecimal.valueOf(402500).add((salary.subtract(BigDecimal.valueOf(2000000.00))).multiply(BigDecimal.valueOf(0.30))).setScale(4, RoundingMode.HALF_UP);
        else
            return BigDecimal.valueOf(1802500).add((salary.subtract(BigDecimal.valueOf(8000000.00))).multiply(BigDecimal.valueOf(0.35))).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateUnpaidLeave(BigDecimal ratePerHour, int totalAbsences) {
        final BigDecimal WORK_HOURS_PER_DAY = BigDecimal.valueOf(7.0); // 8 hours minus 1-hour lunch break

        // Calculate unpaid leave cost by considering only 7 work hours per day
        BigDecimal totalUnpaidLeaveCost = ratePerHour.multiply(WORK_HOURS_PER_DAY)
                                                     .multiply(BigDecimal.valueOf(totalAbsences))
                                                     .setScale(4, RoundingMode.HALF_UP);
        return totalUnpaidLeaveCost;
    }

    private String formatCurrency(BigDecimal amount) {
        return NumberFormat.getCurrencyInstance(new Locale("en", "PH")).format(amount.setScale(4, RoundingMode.HALF_UP));
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
        totalAbsenceTextField.setText("");
        vacationLeaveTextField.setText("");
        unusedLeaveTextField.setText("");
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
        userImagePayroll.setIcon(new ImageIcon("src/icon/user.png"));
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
            java.util.logging.Logger.getLogger(MainEmployee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainEmployee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainEmployee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainEmployee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>


        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainEmployee().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField HrsMonthTextField;
    private javax.swing.JLabel SSSLabel;
    private javax.swing.JTextField SSSTextField;
    private javax.swing.JLabel attendanceLabel;
    private javax.swing.JButton btnAttSum;
    private javax.swing.JButton btnClear2;
    private javax.swing.JButton btnEditProfile;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnLeave;
    private javax.swing.JButton btnLeaveSum;
    private javax.swing.JButton btnPayroll;
    private javax.swing.JButton btnReceiptPayroll;
    private javax.swing.JButton btnTimeIn;
    private javax.swing.JButton btnTimeOut;
    private javax.swing.JComboBox<String> comboBoxPresentAbsent;
    private javax.swing.JPanel datePanelPicker;
    private javax.swing.JLabel departmentLabel;
    private javax.swing.JLabel departmentNameLabel;
    private javax.swing.JTextField departmentTextField;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JTextField emailTextField;
    private javax.swing.JTextField emergencyLeaveTextField;
    private javax.swing.JLabel emp_idLabel;
    private javax.swing.JLabel employeeIdLabel;
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
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
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
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel jobTitleLabel;
    private javax.swing.JTextField jobTitleTextField;
    private javax.swing.JLabel lastNameLabel;
    private javax.swing.JPanel leave;
    private javax.swing.JLabel leaveBalanceLabel;
    private javax.swing.JTextField leaveBalanceTextField;
    private javax.swing.JTable leaveTable;
    private javax.swing.JLabel logout;
    private javax.swing.JPanel managePayroll;
    private com.toedter.calendar.JMonthChooser monthChooser;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel netSalaryLabel;
    private javax.swing.JTextField netSalaryTextField;
    private javax.swing.JLabel overtimeHours_Label;
    private javax.swing.JTextField overtimeHrsTextField;
    private javax.swing.JLabel pagibigLabel;
    private javax.swing.JTextField pagibigTextField;
    private javax.swing.JLabel philHealthLabel;
    private javax.swing.JTextField philHealthTextField;
    private javax.swing.JTextField rateHourTextField;
    private javax.swing.JLabel rateperHour_Label;
    private javax.swing.JButton searchBarEmployeeName;
    private javax.swing.JTextField searchNameTxt;
    private javax.swing.JTextField sickLeaveTextField;
    private javax.swing.JPanel sideBar;
    private javax.swing.JLabel thirteenthMonthPayLabel;
    private javax.swing.JTextField thirteenthMonthPayTextField;
    private javax.swing.JLabel totalAbsenceLabel;
    private javax.swing.JTextField totalAbsenceTextField;
    private javax.swing.JLabel totalCountAbsent;
    private javax.swing.JLabel totalCountAbsentLabel;
    private javax.swing.JLabel totalDeducLabel;
    private javax.swing.JTextField totalDeducTextField;
    private javax.swing.JTextField totalHrsWorkedTextField;
    private javax.swing.JLabel totalSalaryPerMonthLabel;
    private javax.swing.JTextField totalSalaryPerMonthTextField;
    private javax.swing.JLabel total_hours_worked_Label;
    private javax.swing.JPanel trackingAttendance;
    private javax.swing.JLabel unpaidLeaveLabel;
    private javax.swing.JLabel unpaidLeaveLabel2;
    private javax.swing.JLabel unpaidLeaveLabel3;
    private javax.swing.JTextField unpaidLeaveTextField;
    private javax.swing.JLabel unusedLeaveLabel;
    private javax.swing.JTextField unusedLeaveTextField;
    private javax.swing.JLabel userImageIcon;
    private javax.swing.JLabel userImagePayroll;
    private javax.swing.JLabel userJobTitle;
    private javax.swing.JLabel userRole;
    private javax.swing.JLabel userWelcome;
    private javax.swing.JLabel vacationLeaveLabel;
    private javax.swing.JTextField vacationLeaveTextField;
    // End of variables declaration//GEN-END:variables
}
