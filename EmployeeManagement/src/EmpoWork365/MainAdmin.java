/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package EmpoWork365;

import com.toedter.calendar.JMonthChooser;
import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableModel;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

/**
 *
 * @author jenal
 */
public final class MainAdmin extends javax.swing.JFrame implements UserUpdateListener{
    private UserAuthenticate loggedInUser;
    private EditUserDetails editUserDetails;
    private Connection connection;
    private static final int REGULAR_HOURS_PER_MONTH = 160; 
    private static final int TOTAL_SICK_LEAVE = 5; 
    private static final int TOTAL_EMERGENCY_LEAVE = 5; 
    private static final int TOTAL_VACATION_LEAVE = 15; 
    private JDatePickerImpl datePickerStart;  
    private JSpinner yearSpinner; 
    private int mouseX, mouseY;


    public static MainAdmin instance;

    /**
     * Creates new form Login
     */
    public MainAdmin() {
        setUndecorated(true);
        setResizable(false);  
        ImageIcon icon = IconLoader.getIcon();
        Image img = icon.getImage();
        setIconImage(img);
        
        initComponents();
        instance = this;
        initializeComboBox();
        initializeComboBoxPresentAbsent();
        setupDatePickers();
        setupKeyBindings();
        setupMonthChooserListener(monthChooser);

        yearSpinner = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(Calendar.getInstance().get(Calendar.YEAR), 2000, 2100, 1));
    
        addButtonHoverEffect(btnHome);
        addButtonHoverEffect(btnEmpMan);
        addButtonHoverEffect(btnAttSum);
        addButtonHoverEffect(btnPayroll);
        addButtonHoverEffect(btnLeaveSum);

        try {
            sqlConnector connector = new sqlConnector();
            this.connection = connector.createConnection();  

            if (connection != null) {
                 // Load data only if the connection is successful
                loadEmployeeData();    // Load employee data
                getAttendanceData();   // Load attendance data
                loadEmployeeLeave();   // Load leave data
            } else {
                throw new SQLException("Failed to establish database connection.");
            }
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
        
        jTabbedPane1.setUI(new BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int runCount, int maxTabHeight) {
                return 0; // This hides the tab header area
            }
        });
        
        jPanel4.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                // Store the initial position when the mouse is pressed
                mouseX = evt.getX();
                mouseY = evt.getY();
            }
        });
        
        jPanel4.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                // When the mouse is dragged, move the JFrame accordingly
                int x = evt.getXOnScreen();
                int y = evt.getYOnScreen();
                setLocation(x - mouseX, y - mouseY);
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
    
    private void setupDatePickers() {
        UtilDateModel modelStart = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        JDatePanelImpl datePanelStart = new JDatePanelImpl(modelStart, p);
        datePickerStart = new JDatePickerImpl(datePanelStart, new DateLabelFormatter());

        if (datePanelPicker != null) {
            datePanelPicker.setLayout(new java.awt.BorderLayout());
            datePanelPicker.add(datePickerStart, java.awt.BorderLayout.CENTER);
            datePanelPicker.revalidate();  
            datePanelPicker.repaint();
        }
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
   
    private void loadEmployeeLeave() {
        try {
            if (connection == null) {
                System.out.println("Connection is null in loadEmployeeLeave.");
                return;
            }

            String selectedStatus = (String) comboBoxSelectStatus.getSelectedItem();

            String[] columnNames = {
                "ID", "Application", "Name", "Leave Request", "Leave Type", 
                "Reason", "Status", "Action"
            };

            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            String query = "SELECT la.fld_application_id, "
                         + "e.fld_employee_id, "
                         + "CONCAT('ID', ' ', e.fld_employee_id, ':', e.fld_first_name, ' ', e.fld_last_name) AS full_name, "
                         + "lt.fld_leave_type_name, "
                         + "la.fld_date_leave_request, "
                         + "la.fld_status, "
                         + "la.fld_reason, "
                         + "la.fld_request_date "
                         + "FROM tbl_leave_applications la "
                         + "INNER JOIN tbl_employees e ON la.fld_employee_id = e.fld_employee_id "
                         + "INNER JOIN tbl_leave_types lt ON la.fld_leave_type_id = lt.fld_leave_type_id "
                         + "WHERE ";

            if (null == selectedStatus) {
                query += "false";  
            } else 
            switch (selectedStatus) {
                case "All" -> query += "true";  // Select all records
                case "Pending" -> query += "la.fld_status = 'Pending'";
                case "Approved" -> query += "la.fld_status = 'Approved'";
                case "Rejected" -> query += "la.fld_status = 'Rejected'";
                default -> query += "false";  // Invalid status, no records should be returned
            }

            query += " ORDER BY la.fld_request_date DESC";

            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Date requestDate = resultSet.getDate("fld_request_date");
                    Date leaveRequestDate = resultSet.getDate("fld_date_leave_request");

                    String formattedRequestDate = (requestDate != null) ? dateFormatter.format(requestDate) : "N/A";
                    String formattedLeaveRequestDate = (leaveRequestDate != null) ? dateFormatter.format(leaveRequestDate) : "N/A";

                    Object[] row = {
                        resultSet.getInt("fld_application_id"),
                        formattedRequestDate,  
                        resultSet.getString("full_name"),
                        formattedLeaveRequestDate,  
                        resultSet.getString("fld_leave_type_name"),
                        resultSet.getString("fld_reason"),
                        resultSet.getString("fld_status"),
                        "Approve / Reject" 
                    };
                    model.addRow(row);
                }
                setLeaveTableModel(model);
            } 
        } catch (HeadlessException | SQLException e) {

        }
    }

    private void setLeaveTableModel(DefaultTableModel model) {
        leaveTable.setModel(model);
        leaveTable.setRowHeight(40); 

        for (int i = 0; i < leaveTable.getColumnCount(); i++) {
            leaveTable.getColumnModel().getColumn(i).setCellRenderer(new MultiLineCellRenderer());
        }
        leaveTable.getColumnModel().getColumn(0).setPreferredWidth(40);  // "Application ID" column
        leaveTable.getColumnModel().getColumn(1).setPreferredWidth(80); // "Application" column
        leaveTable.getColumnModel().getColumn(2).setPreferredWidth(100); // "Name" column
        leaveTable.getColumnModel().getColumn(3).setPreferredWidth(80); // "Leave Request" column
        leaveTable.getColumnModel().getColumn(4).setPreferredWidth(95); // "Leave Type" column
        leaveTable.getColumnModel().getColumn(5).setPreferredWidth(180); // "Reason" column
        leaveTable.getColumnModel().getColumn(6).setPreferredWidth(75); // "Status" column

        String selectedStatus = (String) comboBoxSelectStatus.getSelectedItem();
        if (!"Pending".equals(selectedStatus)) {
            leaveTable.getColumnModel().getColumn(7).setMinWidth(0);
            leaveTable.getColumnModel().getColumn(7).setMaxWidth(0);
            leaveTable.getColumnModel().getColumn(7).setWidth(0);
        } else {
            leaveTable.getColumnModel().getColumn(7).setMinWidth(180);  
            leaveTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));
            leaveTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        }
    }

    void approveLeave(int leaveId) {
        try {
            EmployeeMethod employeeMethod = new EmployeeMethod(connection);
            employeeMethod.updateLeaveStatus(leaveId, "Approved"); 
            loadEmployeeLeave(); 
            JOptionPane.showMessageDialog(this, "Leave approved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error approving leave: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void rejectLeave(int leaveId) {
        try {
            EmployeeMethod employeeMethod = new EmployeeMethod(connection);
            employeeMethod.updateLeaveStatus(leaveId, "Rejected");  
            loadEmployeeLeave();
            JOptionPane.showMessageDialog(this, "Leave rejected successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error rejecting leave: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
        button.setBackground(new Color(0,36,57));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(33,77,104));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0,36,57));
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
    
    public void searchAndDisplayEmployees(String searchTerm) {
        EmployeeMethod employeeMethod = new EmployeeMethod(connection);
        List<Employee> employeeList = employeeMethod.searchEmployeeMethod(searchTerm);

        // Create the table model with the specified column names
        DefaultTableModel model = new DefaultTableModel(new Object[]{
            "Employee ID", "Full Name", "Email", "Gender", "Job Title", "Department", "Date of Employment"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells non-editable
            }
        };

        // Iterate through the employee list and add rows to the table model
        for (Employee employee : employeeList) {
            String FullName = employee.getFirstname() + " " + employee.getLastname(); // Adjusted getter methods
            model.addRow(new Object[]{
                employee.getEmployeeId(),
                FullName,
                employee.getEmail(),
                employee.getGender(),
                employee.getJobtitle(), // Ensure this matches your Employee class
                employee.getDepartmentName(),
                employee.getDateOfEmployment()
            });
        }

        // Set the model to the JTable
        jTable1.setModel(model);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
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
        jLabel10 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        home = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        btnMin1 = new javax.swing.JButton();
        btnClose1 = new javax.swing.JButton();
        userWelcome = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
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
        jPanel14 = new javax.swing.JPanel();
        btnMin2 = new javax.swing.JButton();
        btnClose2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        searchBar = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        searchNameTxt = new javax.swing.JTextField();
        searchBarEmployeeName = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        btnDelete = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        trackingAttendance = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        btnMin3 = new javax.swing.JButton();
        btnClose3 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        datePanelPicker = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        comboBoxPresentAbsent = new javax.swing.JComboBox<>();
        leave = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        btnMin4 = new javax.swing.JButton();
        btnClose4 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        leaveTable = new javax.swing.JTable();
        comboBoxSelectStatus = new javax.swing.JComboBox<>();
        managePayroll = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        btnMin5 = new javax.swing.JButton();
        btnClose5 = new javax.swing.JButton();
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
        btnChangeRatePerHour = new javax.swing.JButton();
        monthChooser = new com.toedter.calendar.JMonthChooser();
        btnReceiptPayroll = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setBounds(new java.awt.Rectangle(200, 200, 200, 200));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setPreferredSize(new java.awt.Dimension(1000, 800));

        jPanel5.setBackground(new java.awt.Color(0, 36, 57));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sideBar.setBackground(new java.awt.Color(0, 36, 57));
        sideBar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(0, 36, 57));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 36, 57));

        btnHome.setBackground(new java.awt.Color(33, 77, 104));
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

        btnEmpMan.setBackground(new java.awt.Color(33, 77, 104));
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

        btnAttSum.setBackground(new java.awt.Color(33, 77, 104));
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

        btnLeaveSum.setBackground(new java.awt.Color(33, 77, 104));
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

        btnPayroll.setBackground(new java.awt.Color(33, 77, 104));
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

        jPanel1.setBackground(new java.awt.Color(0, 36, 57));

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
        jPanel3.add(logout, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 580, -1, -1));

        sideBar.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, -1, 750));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/logo.png"))); // NOI18N
        sideBar.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, 40));

        jLabel4.setFont(new java.awt.Font("Perpetua", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("EMPOWER");
        sideBar.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, -1, 60));

        jLabel9.setFont(new java.awt.Font("Viner Hand ITC", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Work");
        sideBar.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 70, 70, -1));

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("for 365");
        sideBar.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, 70, 30));

        jPanel5.add(sideBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 980));

        home.setBackground(new java.awt.Color(255, 255, 255));
        home.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel13.setBackground(new java.awt.Color(5, 52, 80));

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

        userWelcome.setFont(new java.awt.Font("Arial", 1, 30)); // NOI18N
        userWelcome.setForeground(new java.awt.Color(192, 213, 249));
        userWelcome.setText("user");

        jLabel1.setFont(new java.awt.Font("Arial", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(192, 213, 249));
        jLabel1.setText("Hi,");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(192, 213, 249));
        jLabel6.setText("USER ID:");

        employeeIdLabel.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        employeeIdLabel.setForeground(new java.awt.Color(192, 213, 249));
        employeeIdLabel.setText("Number");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(userWelcome, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(employeeIdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(340, 340, 340)
                .addComponent(btnMin1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnClose1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(61, Short.MAX_VALUE)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnClose1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnMin1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(userWelcome))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(employeeIdLabel))
                        .addGap(11, 11, 11))))
        );

        home.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 790, 130));

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

        home.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 262, -1, -1));

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
        home.add(btnEditProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(581, 199, 151, 45));

        userRole.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        userRole.setForeground(new java.awt.Color(9, 53, 53));
        userRole.setText("Role");
        home.add(userRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(56, 222, 490, -1));

        jTabbedPane1.addTab("tab1", home);

        employeeManagement.setBackground(new java.awt.Color(255, 255, 255));
        employeeManagement.setPreferredSize(new java.awt.Dimension(900, 938));
        employeeManagement.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel14.setBackground(new java.awt.Color(5, 52, 80));

        btnMin2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/minimize.png"))); // NOI18N
        btnMin2.setContentAreaFilled(false);
        btnMin2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMin2.setFocusable(false);
        btnMin2.setRequestFocusEnabled(false);
        btnMin2.setRolloverEnabled(false);
        btnMin2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMin2ActionPerformed(evt);
            }
        });

        btnClose2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/close.png"))); // NOI18N
        btnClose2.setContentAreaFilled(false);
        btnClose2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClose2.setFocusable(false);
        btnClose2.setRequestFocusEnabled(false);
        btnClose2.setRolloverEnabled(false);
        btnClose2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClose2ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Employee Management");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(205, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(116, 116, 116)
                .addComponent(btnMin2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnClose2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(62, Short.MAX_VALUE)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnClose2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMin2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18))
        );

        employeeManagement.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 790, 130));

        searchBar.setBackground(new java.awt.Color(255, 255, 255));

        btnAdd.setBackground(new java.awt.Color(8, 127, 127));
        btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setText("Add");
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.setFocusable(false);
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

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

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Name:");

        btnDelete.setBackground(new java.awt.Color(255, 102, 102));
        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("Delete");
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelete.setFocusable(false);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnEdit.setText("Edit");
        btnEdit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEdit.setFocusable(false);
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
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 133, Short.MAX_VALUE)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        searchBarLayout.setVerticalGroup(
            searchBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchBarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(searchBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnEdit, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAdd, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(searchBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        employeeManagement.add(searchBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 760, -1));

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

        employeeManagement.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 760, 590));

        jTabbedPane1.addTab("tab2", employeeManagement);

        trackingAttendance.setBackground(new java.awt.Color(255, 255, 255));
        trackingAttendance.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel15.setBackground(new java.awt.Color(5, 52, 80));

        btnMin3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/minimize.png"))); // NOI18N
        btnMin3.setContentAreaFilled(false);
        btnMin3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMin3.setFocusable(false);
        btnMin3.setRequestFocusEnabled(false);
        btnMin3.setRolloverEnabled(false);
        btnMin3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMin3ActionPerformed(evt);
            }
        });

        btnClose3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/close.png"))); // NOI18N
        btnClose3.setContentAreaFilled(false);
        btnClose3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClose3.setFocusable(false);
        btnClose3.setRequestFocusEnabled(false);
        btnClose3.setRolloverEnabled(false);
        btnClose3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClose3ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Track Attendance");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(199, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(102, 102, 102)
                .addComponent(btnMin3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnClose3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(62, Short.MAX_VALUE)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnClose3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMin3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18))
        );

        trackingAttendance.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 790, 130));

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

        trackingAttendance.add(datePanelPicker, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 180, -1, -1));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Job Title", "Department", "Time-In", "Time-Out", "Date", "Status"
            }
        ));
        jScrollPane2.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setMinWidth(30);
            jTable2.getColumnModel().getColumn(0).setMaxWidth(30);
        }

        trackingAttendance.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 770, 620));

        comboBoxPresentAbsent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxPresentAbsentActionPerformed(evt);
            }
        });
        trackingAttendance.add(comboBoxPresentAbsent, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 180, -1, 30));

        jTabbedPane1.addTab("tab3", trackingAttendance);

        leave.setBackground(new java.awt.Color(255, 255, 255));
        leave.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel16.setBackground(new java.awt.Color(5, 52, 80));

        btnMin4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/minimize.png"))); // NOI18N
        btnMin4.setContentAreaFilled(false);
        btnMin4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMin4.setFocusable(false);
        btnMin4.setRequestFocusEnabled(false);
        btnMin4.setRolloverEnabled(false);
        btnMin4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMin4ActionPerformed(evt);
            }
        });

        btnClose4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/close.png"))); // NOI18N
        btnClose4.setContentAreaFilled(false);
        btnClose4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClose4.setFocusable(false);
        btnClose4.setRequestFocusEnabled(false);
        btnClose4.setRolloverEnabled(false);
        btnClose4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClose4ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Leave Request Summary");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap(226, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(122, 122, 122)
                .addComponent(btnMin4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnClose4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap(62, Short.MAX_VALUE)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnClose4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMin4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18))
        );

        leave.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 790, 130));

        jScrollPane3.setBackground(new java.awt.Color(255, 255, 255));

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
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(leaveTable);

        leave.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 765, 620));

        comboBoxSelectStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxSelectStatusActionPerformed(evt);
            }
        });
        leave.add(comboBoxSelectStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 180, 145, -1));

        jTabbedPane1.addTab("tab1", leave);

        managePayroll.setBackground(new java.awt.Color(255, 255, 255));
        managePayroll.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel17.setBackground(new java.awt.Color(5, 52, 80));

        btnMin5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/minimize.png"))); // NOI18N
        btnMin5.setContentAreaFilled(false);
        btnMin5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMin5.setFocusable(false);
        btnMin5.setRequestFocusEnabled(false);
        btnMin5.setRolloverEnabled(false);
        btnMin5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMin5ActionPerformed(evt);
            }
        });

        btnClose5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/close.png"))); // NOI18N
        btnClose5.setContentAreaFilled(false);
        btnClose5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClose5.setFocusable(false);
        btnClose5.setRequestFocusEnabled(false);
        btnClose5.setRolloverEnabled(false);
        btnClose5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClose5ActionPerformed(evt);
            }
        });

        payrollManagementTitle.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        payrollManagementTitle.setForeground(new java.awt.Color(255, 255, 255));
        payrollManagementTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        payrollManagementTitle.setText("Payroll Management");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap(231, Short.MAX_VALUE)
                .addComponent(payrollManagementTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(110, 110, 110)
                .addComponent(btnMin5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnClose5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap(62, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnClose5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMin5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(payrollManagementTitle, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18))
        );

        managePayroll.add(jPanel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 790, 130));

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
                .addGap(19, 19, 19)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(thirteenthMonthPayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(thirteenthMonthPayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(netSalaryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(netSalaryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );

        managePayroll.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, -1, 670));

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
        jPanel8.add(userImagePayroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel10.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 110, -1, 100));

        btnClear2.setBackground(new java.awt.Color(71, 146, 146));
        btnClear2.setForeground(new java.awt.Color(204, 255, 255));
        btnClear2.setText("Clear All");
        btnClear2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClear2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClear2ActionPerformed(evt);
            }
        });
        jPanel10.add(btnClear2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 120, 20));

        btnChangeRatePerHour.setBackground(new java.awt.Color(76, 122, 172));
        btnChangeRatePerHour.setForeground(new java.awt.Color(255, 255, 255));
        btnChangeRatePerHour.setText("Change rate/hr");
        btnChangeRatePerHour.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnChangeRatePerHour.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeRatePerHourActionPerformed(evt);
            }
        });
        jPanel10.add(btnChangeRatePerHour, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 120, 20));
        jPanel10.add(monthChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 4, 120, -1));

        btnReceiptPayroll.setText("Print");
        btnReceiptPayroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReceiptPayrollActionPerformed(evt);
            }
        });
        jPanel10.add(btnReceiptPayroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 40, 120, 20));

        managePayroll.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 180, 364, 430));

        jTabbedPane1.addTab("tab4", managePayroll);

        jPanel5.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(214, -47, -1, 1080));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 1000, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 980, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 980, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 860));

        pack();
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

    private void btnClear2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClear2ActionPerformed
        clearFields();
    }//GEN-LAST:event_btnClear2ActionPerformed

    private void btnLeaveSumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeaveSumActionPerformed
        jTabbedPane1.setSelectedIndex(3);
    }//GEN-LAST:event_btnLeaveSumActionPerformed

    
    private void btnChangeRatePerHourActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeRatePerHourActionPerformed
        try {
            int employeeId = Integer.parseInt(getIdPayroll.getText());
            EmployeeMethod employeeMethod = new EmployeeMethod(connection);
            Employee employees = employeeMethod.getEmployeeIdById(employeeId);

            if (employees != null) {
                String rateText = rateHourTextField.getText().replace(" hrs", "").trim(); // Remove 'hrs' and trim spaces
                double newRatePerHour = Double.parseDouble(rateText);  

                String jobTitle = employees.getJobtitle(); 
                employeeMethod.updateRatePerHourInDatabase(jobTitle, newRatePerHour);

                JOptionPane.showMessageDialog(this, "Rate updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                Employee updatedEmployee = employeeMethod.getEmployeeIdById(employeeId);
                updateEmployeeDetails(updatedEmployee);
            } else {
                JOptionPane.showMessageDialog(this, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid employee ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching employee data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } 
    }//GEN-LAST:event_btnChangeRatePerHourActionPerformed

    private void initializeComboBox() {
        comboBoxSelectStatus.addItem("All");
        comboBoxSelectStatus.addItem("Pending");
        comboBoxSelectStatus.addItem("Approved");
        comboBoxSelectStatus.addItem("Rejected");

        comboBoxSelectStatus.addActionListener((ActionEvent evt) -> {
            comboBoxSelectStatusActionPerformed(evt);
        });
    }

    private void comboBoxSelectStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxSelectStatusActionPerformed
        loadEmployeeLeave();
    }//GEN-LAST:event_comboBoxSelectStatusActionPerformed

    private void initializeComboBoxPresentAbsent() {
        comboBoxPresentAbsent.addItem("All");
        comboBoxPresentAbsent.addItem("Present");
        comboBoxPresentAbsent.addItem("Incomplete");
        comboBoxPresentAbsent.addItem("Absent");
    }

    private void comboBoxPresentAbsentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxPresentAbsentActionPerformed
        if (datePickerStart == null) {
            return;
        }

        String selectedStatus = (String) comboBoxPresentAbsent.getSelectedItem(); 
        java.util.Date selectedDate = (java.util.Date) datePickerStart.getModel().getValue(); 

        if (selectedDate == null || selectedDate.toString().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a date.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            EmployeeMethod employeeMethod = new EmployeeMethod(connection);
            DefaultTableModel model;

            java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

            model = employeeMethod.getAttendanceDataByDateAndStatus(sqlDate, selectedStatus); 

            setTableAttendance(model); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load attendance data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_comboBoxPresentAbsentActionPerformed

    private void btnReceiptPayrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceiptPayrollActionPerformed
        Receipt receiptFrame = new Receipt();

        Employee employee = getEmployee(); 

        if (employee == null) {
            JOptionPane.showMessageDialog(null, "Please select an employee.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int selectedMonth = monthChooser.getMonth() + 1;  
        getMonthName(selectedMonth);

        BigDecimal ratePerHour = employee.getRatePerHour();
        BigDecimal totalHoursWorked = calculateTotalHoursWorked(employee, selectedMonth, (int) yearSpinner.getValue());
        BigDecimal overtimeHours = calculateOvertimeHours(totalHoursWorked);
        BigDecimal totalSalary = calculateTotalSalary(employee.getRatePerHour(), overtimeHours); 

        BigDecimal philHealthDeduction = calculatePhilHealthDeduction(totalSalary);
        BigDecimal sssDeduction = calculateSSSDeduction(totalSalary);
        BigDecimal pagibigDeduction = calculatePagIbigDeduction(totalSalary);
        BigDecimal incomeTaxDeduction = calculateIncomeTax(totalSalary);

        BigDecimal totalDeductions = philHealthDeduction.add(sssDeduction).add(pagibigDeduction).add(incomeTaxDeduction);

        // Assuming 0 unpaid leave for January-November
        BigDecimal unpaidLeaveCost = BigDecimal.ZERO; 
        // Calculate net salary after deductions
        BigDecimal netSalary = calculateNetSalary(totalSalary, unpaidLeaveCost); 

        BigDecimal unusedLeave = BigDecimal.ZERO;
        BigDecimal thirteenthMonthPay = BigDecimal.ZERO;
        if (selectedMonth == 12) {
            BigDecimal basicSalaryForYear = ratePerHour.multiply(BigDecimal.valueOf(REGULAR_HOURS_PER_MONTH)).multiply(BigDecimal.valueOf(12));
            thirteenthMonthPay = basicSalaryForYear.divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);

            try {
                unusedLeave = calculateUnusedLeave(employee); 
            } catch (SQLException ex) {
                Logger.getLogger(MainAdmin.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        receiptFrame.setPayrollDetails(employee, totalSalary, ratePerHour, totalHoursWorked, 
                                   overtimeHours, totalDeductions, netSalary, unusedLeave, 
                                   thirteenthMonthPay, selectedMonth);


        receiptFrame.setVisible(true);
    }//GEN-LAST:event_btnReceiptPayrollActionPerformed

    private void btnMin1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMin1ActionPerformed
        this.setState(JFrame.ICONIFIED);
    }//GEN-LAST:event_btnMin1ActionPerformed

    private void btnClose1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnClose1ActionPerformed

    private void btnMin2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMin2ActionPerformed
        this.setState(JFrame.ICONIFIED);
    }//GEN-LAST:event_btnMin2ActionPerformed

    private void btnClose2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose2ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnClose2ActionPerformed

    private void btnMin3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMin3ActionPerformed
        this.setState(JFrame.ICONIFIED);
    }//GEN-LAST:event_btnMin3ActionPerformed

    private void btnClose3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose3ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnClose3ActionPerformed

    private void btnMin4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMin4ActionPerformed
        this.setState(JFrame.ICONIFIED);
    }//GEN-LAST:event_btnMin4ActionPerformed

    private void btnClose4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose4ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnClose4ActionPerformed

    private void btnMin5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMin5ActionPerformed
        this.setState(JFrame.ICONIFIED);
    }//GEN-LAST:event_btnMin5ActionPerformed

    private void btnClose5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose5ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnClose5ActionPerformed

    private String getMonthName(int month) {
        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        return monthNames[month - 1]; // Adjust for zero-based index (1-12)
    }

    private void setupKeyBindings() {
        // For the nameTextField
        nameTextField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "enterPressedName");
        nameTextField.getActionMap().put("enterPressedName", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameTextField.getText().trim();

                if (name.length() >= 2) {
                    nameTextField.setEnabled(false); // Disable the field while processing
                    try {
                        EmployeeMethod employeeMethod = new EmployeeMethod(connection);
                        Employee employees = employeeMethod.getEmployeeByName(name);

                        if (employees != null) {
                            getIdPayroll.setText(String.valueOf(employees.getEmployeeId()));
                            updateEmployeeDetails(employees);
                        } else {
                            JOptionPane.showMessageDialog(null, "No employee found with the given name.", "Employee Not Found", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error fetching employee data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        nameTextField.setEnabled(true); // Re-enable the field
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a name to search.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // For getIdPayroll
        getIdPayroll.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "enterPressedId");
        getIdPayroll.getActionMap().put("enterPressedId", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String employeeIdText = getIdPayroll.getText();
                if (!employeeIdText.isEmpty()) {
                    try {
                        int employeeId = Integer.parseInt(employeeIdText);

                        getIdPayroll.setEnabled(false); // Disable the field while processing
                        EmployeeMethod employeeMethod = new EmployeeMethod(connection);
                        Employee employees = employeeMethod.getEmployeeIdById(employeeId);

                        if (employees != null) {
                            updateEmployeeDetails(employees);
                        } else {
                            JOptionPane.showMessageDialog(null, "No employee found with the given ID.", "Employee Not Found", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid Employee ID format. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error fetching employee data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        getIdPayroll.setEnabled(true); // Re-enable the field
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter an Employee ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    
    private Employee getEmployee() {
        Employee employee = null;

        // Check if employee ID is entered in getIdPayroll field
        String employeeIdText = getIdPayroll.getText().trim();
        if (!employeeIdText.isEmpty()) {
            try {
                int EmployeeId = Integer.parseInt(employeeIdText);
                EmployeeMethod getEmployeeMethod = new EmployeeMethod(connection);
                employee = getEmployeeMethod.getEmployeeIdById(EmployeeId);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid Employee ID format.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } 
        // Otherwise, retrieve employee by name if nameTextField is not empty
        else if (!nameTextField.getText().trim().isEmpty()) {
            String name = nameTextField.getText().trim();
            EmployeeMethod employeOpt = new EmployeeMethod(connection);
            try {
                employee = employeOpt.getEmployeeByName(name);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        return employee;
    }
    
    private void setupMonthChooserListener(JMonthChooser monthChooser) {
        monthChooser.addPropertyChangeListener((java.beans.PropertyChangeEvent evt) -> {
            if ("month".equals(evt.getPropertyName())) {
                int selectedMonth = monthChooser.getMonth() + 1; // Adjust month to be 1-based (1-12)
                int selectedYear = (int) yearSpinner.getValue(); // Get the selected year from yearSpinner

                Employee employee = getEmployee(); // This method should retrieve the current employee based on either ID or name

                if (employee == null) {
                    JOptionPane.showMessageDialog(null, "Employee data is null", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    clearDetails(false);
                    updateEmployeeDetailsForSelectedMonth(selectedMonth , selectedYear);
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

        if (isDecember) {
            // Clear leave-related fields only for December
            unpaidLeaveTextField.setText("");
            totalAbsenceTextField.setText("");
            sickLeaveTextField.setText("");
            emergencyLeaveTextField.setText("");
            vacationLeaveTextField.setText("");
            leaveBalanceTextField.setText("");
            unusedLeaveTextField.setText("");
            thirteenthMonthPayTextField.setText("");

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
            updateEmployeeDetails(employee); 


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

    private void updateEmployeeDetails(Employee employee) {
        if (employee == null) {
            JOptionPane.showMessageDialog(this, "Employee data is null", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int selectedMonth = monthChooser.getMonth(); // Convert to 1-based month
            int selectedYear = (int) yearSpinner.getValue();

            // Set employee details
            String FullName = employee.getFirstname() + " " + employee.getLastname();
            nameTextField.setText(FullName);
            emailTextField.setText(employee.getEmail());
            genderTextField.setText(employee.getGender());
            jobTitleTextField.setText(employee.getJobtitle());
            departmentTextField.setText(employee.getDepartmentName());

            // Display rate per hour with formatting
            BigDecimal ratePerHour = employee.getRatePerHour().setScale(4, RoundingMode.HALF_UP);
            rateHourTextField.setText(String.format("%.2f", ratePerHour.doubleValue()));
            HrsMonthTextField.setText(REGULAR_HOURS_PER_MONTH + " hrs");

            if (selectedMonth == 12) {
                updateDecemberDetails(employee, selectedYear);
            } else {
                updateRegularPayroll(employee, selectedMonth + 1, selectedYear, ratePerHour);
            }

            // Set employee image if available
            String imagePath = employee.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                ImageIcon originalIcon = new ImageIcon(imagePath);
                int width = 100, height = 100;
                Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                userImagePayroll.setIcon(new ImageIcon(scaledImage));
            } else {
                userImagePayroll.setIcon(new ImageIcon("src/Users/user.png"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error occurred: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRegularPayroll(Employee employee, int month, int year, BigDecimal ratePerHour) {
        BigDecimal totalHoursWorked = calculateTotalHoursWorked(employee, month, year).setScale(4, RoundingMode.HALF_UP);
        totalHrsWorkedTextField.setText(String.format("%.2f hrs", totalHoursWorked.doubleValue()));

        BigDecimal overtimeHours = calculateOvertimeHours(totalHoursWorked).setScale(4, RoundingMode.HALF_UP);
        overtimeHrsTextField.setText(String.format("%.2f hrs", overtimeHours.doubleValue()));

        BigDecimal totalSalary = calculateTotalSalary(ratePerHour, overtimeHours).setScale(4, RoundingMode.HALF_UP);
        totalSalaryPerMonthTextField.setText(formatCurrency(totalSalary));

        BigDecimal unpaidLeaveCost = calculateUnpaidLeave(ratePerHour, BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP);
        BigDecimal netSalary = calculateNetSalary(totalSalary, unpaidLeaveCost).setScale(4, RoundingMode.HALF_UP);
        netSalaryTextField.setText(formatCurrency(netSalary));

        // Set deductions with proper formatting
        philHealthTextField.setText(formatCurrency(calculatePhilHealthDeduction(totalSalary)));
        SSSTextField.setText(formatCurrency(calculateSSSDeduction(totalSalary)));
        pagibigTextField.setText(formatCurrency(calculatePagIbigDeduction(totalSalary)));
        incomeTaxTextField.setText(formatCurrency(calculateIncomeTax(totalSalary)));
    }

    private void updateDecemberDetails(Employee employee, int year) throws SQLException {
        System.out.println("Update December details: " + employee.getEmployeeId() + ", Year: " + year);

        EmployeeMethod employeeOption = new EmployeeMethod(connection);
        AttendanceMethod attendanceMethod = new AttendanceMethod(connection);
        
        // Fetch December details (12 in 1-based, 11 in 0-based)
        BigDecimal totalHoursWorked = BigDecimal.valueOf(attendanceMethod.getTotalHoursWorkedInMonth(employee.getEmployeeId(), 12, year));
        totalHrsWorkedTextField.setText(String.format("%.2f hrs", totalHoursWorked.doubleValue())); // Convert to double for display

        BigDecimal overtimeHours = calculateOvertimeHours(totalHoursWorked);
        overtimeHrsTextField.setText(String.format("%.2f hrs", overtimeHours.doubleValue())); // Convert to double for display

        BigDecimal ratePerHour = employee.getRatePerHour(); // Get rate per hour as BigDecimal directly
        BigDecimal totalSalary = calculateTotalSalary(ratePerHour, overtimeHours);
        totalSalaryPerMonthTextField.setText(formatCurrency(totalSalary));

        // Calculate unpaid leave
        int unpaidLeaveDays = attendanceMethod.getUnpaidLeaveDays(employee.getEmployeeId(), 12, year);
        BigDecimal unpaidLeaveCost = calculateUnpaidLeave(ratePerHour, BigDecimal.valueOf(unpaidLeaveDays));
        unpaidLeaveTextField.setText(formatCurrency(unpaidLeaveCost));

        // Deductions (PhilHealth, SSS, PagIbig, Income Tax)
        philHealthTextField.setText(formatCurrency(calculatePhilHealthDeduction(totalSalary)));
        SSSTextField.setText(formatCurrency(calculateSSSDeduction(totalSalary)));
        pagibigTextField.setText(formatCurrency(calculatePagIbigDeduction(totalSalary)));
        incomeTaxTextField.setText(formatCurrency(calculateIncomeTax(totalSalary)));

        Map<String, Integer> remainingLeaveDays = employeeOption.getRemainingLeaveDays(employee.getEmployeeId());
        sickLeaveTextField.setText(String.valueOf(remainingLeaveDays.getOrDefault("Sick Leave", TOTAL_SICK_LEAVE)));
        emergencyLeaveTextField.setText(String.valueOf(remainingLeaveDays.getOrDefault("Emergency Leave", TOTAL_EMERGENCY_LEAVE)));
        vacationLeaveTextField.setText(String.valueOf(remainingLeaveDays.getOrDefault("Vacation Leave", TOTAL_VACATION_LEAVE)));

        // Calculate total leave balance and display in leaveBalanceTextField
        int totalLeaveBalance = remainingLeaveDays.getOrDefault("Sick Leave", TOTAL_SICK_LEAVE)
                        + remainingLeaveDays.getOrDefault("Emergency Leave", TOTAL_EMERGENCY_LEAVE)
                        + remainingLeaveDays.getOrDefault("Vacation Leave", TOTAL_VACATION_LEAVE);
        leaveBalanceTextField.setText(totalLeaveBalance + " days");

        BigDecimal unusedLeaveCost = calculateUnusedLeave(employee);
        unusedLeaveTextField.setText(formatCurrency(unusedLeaveCost));

        // Calculate basic salary for the year and 13th month pay
        BigDecimal basicSalaryForYear = ratePerHour.multiply(BigDecimal.valueOf(REGULAR_HOURS_PER_MONTH)).multiply(BigDecimal.valueOf(12)); // Ensure REGULAR_HOURS_PER_MONTH is of type BigDecimal
        BigDecimal thirteenthMonthPay = basicSalaryForYear.divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);
        thirteenthMonthPayTextField.setText(formatCurrency(thirteenthMonthPay));

        // Total absence calculation
        int totalAbsences = employeeOption.getTotalAbsences(employee);
        totalAbsenceTextField.setText(String.valueOf(totalAbsences));

        // Calculate net salary
        BigDecimal netSalary = calculateNetSalary(totalSalary, unpaidLeaveCost).add(unusedLeaveCost).add(thirteenthMonthPay);

        // Update net salary field
        netSalaryTextField.setText(formatCurrency(netSalary));
    }


    private BigDecimal calculateUnusedLeave(Employee employee) throws SQLException {
        EmployeeMethod employeeMethod = new EmployeeMethod(connection);
        Map<String, Integer> remainingLeaveDays = employeeMethod.getRemainingLeaveDays(employee.getEmployeeId());

        // Fetch leave balances or use defaults if not found
        int sickLeaveBalance = remainingLeaveDays.getOrDefault("Sick Leave", TOTAL_SICK_LEAVE);
        int emergencyLeaveBalance = remainingLeaveDays.getOrDefault("Emergency Leave", TOTAL_EMERGENCY_LEAVE);
        int vacationLeaveBalance = remainingLeaveDays.getOrDefault("Vacation Leave", TOTAL_VACATION_LEAVE);

        // Total leave balance
        int leaveBalance = sickLeaveBalance + emergencyLeaveBalance + vacationLeaveBalance;

        // Calculate the unused leave cost assuming 8 hours per day
        BigDecimal dailyRate = employee.getRatePerHour().multiply(BigDecimal.valueOf(8)); // Rate per day (8 hours)
        return BigDecimal.valueOf(leaveBalance).multiply(dailyRate); // Multiply by the total leave balance
    }


    private BigDecimal calculateTotalHoursWorked(Employee employee, int month, int year) {
        AttendanceMethod attendanceMethod = new AttendanceMethod(connection);
        BigDecimal totalHours = BigDecimal.ZERO;

        try {
            totalHours = BigDecimal.valueOf(attendanceMethod.getTotalHoursWorkedInMonth(employee.getEmployeeId(), month, year));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving total hours worked: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return totalHours;
    }

    private BigDecimal calculateOvertimeHours(BigDecimal totalHoursWorked) {
        BigDecimal overtimeHours = totalHoursWorked.subtract(BigDecimal.valueOf(REGULAR_HOURS_PER_MONTH)).setScale(2, RoundingMode.HALF_UP);
        return overtimeHours.max(BigDecimal.ZERO); // Ensure no negative overtime
    }


    private BigDecimal calculateTotalSalary(BigDecimal ratePerHour, BigDecimal overtimeHours) {
        BigDecimal regularSalary = ratePerHour.multiply(BigDecimal.valueOf(REGULAR_HOURS_PER_MONTH)).setScale(4, RoundingMode.HALF_UP);

        BigDecimal overtimeRate = ratePerHour.multiply(BigDecimal.valueOf(1.5)).setScale(4, RoundingMode.HALF_UP); 
        BigDecimal overtimeSalary = overtimeRate.multiply(overtimeHours).setScale(4, RoundingMode.HALF_UP);

        BigDecimal totalSalary = regularSalary.add(overtimeSalary).setScale(4, RoundingMode.HALF_UP);

        return totalSalary;
    }


    private BigDecimal calculateNetSalary(BigDecimal totalSalary, BigDecimal unpaidLeaveCost) {
        BigDecimal philHealthDeduction = calculatePhilHealthDeduction(totalSalary);
        BigDecimal sssDeduction = calculateSSSDeduction(totalSalary);
        BigDecimal pagIbigDeduction = calculatePagIbigDeduction(totalSalary);
        BigDecimal incomeTax = calculateIncomeTax(totalSalary);

        BigDecimal totalDeductions = philHealthDeduction.add(sssDeduction).add(pagIbigDeduction).add(incomeTax).add(unpaidLeaveCost);

        philHealthTextField.setText(formatCurrency(philHealthDeduction));
        SSSTextField.setText(formatCurrency(sssDeduction));
        pagibigTextField.setText(formatCurrency(pagIbigDeduction));
        incomeTaxTextField.setText(formatCurrency(incomeTax));
        totalDeducTextField.setText(formatCurrency(totalDeductions));

        BigDecimal netSalary = totalSalary.subtract(totalDeductions);

        return netSalary;
    }


    private BigDecimal calculatePhilHealthDeduction(BigDecimal totalSalary) {
        return totalSalary.multiply(BigDecimal.valueOf(0.01)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateSSSDeduction(BigDecimal totalSalary) {
        return totalSalary.multiply(BigDecimal.valueOf(0.02)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePagIbigDeduction(BigDecimal totalSalary) {
        return totalSalary.compareTo(BigDecimal.valueOf(200.0)) <= 0
            ? totalSalary.multiply(BigDecimal.valueOf(0.01)).setScale(2, RoundingMode.HALF_UP)
            : totalSalary.multiply(BigDecimal.valueOf(0.02)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateIncomeTax(BigDecimal salary) {
        if (salary.compareTo(BigDecimal.valueOf(250000.00)) <= 0) return BigDecimal.ZERO;
        else if (salary.compareTo(BigDecimal.valueOf(400000.00)) <= 0)
            return (salary.subtract(BigDecimal.valueOf(250000.00))).multiply(BigDecimal.valueOf(0.15)).setScale(4, RoundingMode.HALF_UP);
        else if (salary.compareTo(BigDecimal.valueOf(800000.00)) <= 0)
            return BigDecimal.valueOf(22500).add((salary.subtract(BigDecimal.valueOf(400000.00))).multiply(BigDecimal.valueOf(0.20))).setScale(2, RoundingMode.HALF_UP);
        else if (salary.compareTo(BigDecimal.valueOf(2000000.00)) <= 0)
            return BigDecimal.valueOf(102500).add((salary.subtract(BigDecimal.valueOf(800000.00))).multiply(BigDecimal.valueOf(0.25))).setScale(2, RoundingMode.HALF_UP);
        else if (salary.compareTo(BigDecimal.valueOf(8000000.00)) <= 0)
            return BigDecimal.valueOf(402500).add((salary.subtract(BigDecimal.valueOf(2000000.00))).multiply(BigDecimal.valueOf(0.30))).setScale(2, RoundingMode.HALF_UP);
        else
            return BigDecimal.valueOf(1802500).add((salary.subtract(BigDecimal.valueOf(8000000.00))).multiply(BigDecimal.valueOf(0.35))).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateUnpaidLeave(BigDecimal ratePerHour, BigDecimal unpaidLeaveDays) {
        final BigDecimal HOURS_PER_DAY = BigDecimal.valueOf(8.0);
        BigDecimal totalUnpaidLeaveCost = ratePerHour.multiply(HOURS_PER_DAY).multiply(unpaidLeaveDays);
        return totalUnpaidLeaveCost.setScale(4, RoundingMode.HALF_UP);
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
    private javax.swing.JButton btnChangeRatePerHour;
    private javax.swing.JButton btnClear2;
    private javax.swing.JButton btnClose1;
    private javax.swing.JButton btnClose2;
    private javax.swing.JButton btnClose3;
    private javax.swing.JButton btnClose4;
    private javax.swing.JButton btnClose5;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnEditProfile;
    private javax.swing.JButton btnEmpMan;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnLeaveSum;
    private javax.swing.JButton btnMin1;
    private javax.swing.JButton btnMin2;
    private javax.swing.JButton btnMin3;
    private javax.swing.JButton btnMin4;
    private javax.swing.JButton btnMin5;
    private javax.swing.JButton btnPayroll;
    private javax.swing.JButton btnReceiptPayroll;
    private javax.swing.JComboBox<String> comboBoxPresentAbsent;
    private javax.swing.JComboBox<String> comboBoxSelectStatus;
    private javax.swing.JPanel datePanelPicker;
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
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
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
    private javax.swing.JLabel totalAbsenceLabel;
    private javax.swing.JTextField totalAbsenceTextField;
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
