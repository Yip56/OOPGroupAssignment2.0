/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import my.edu.apu.enums.Intake;
import my.edu.apu.enums.Program;
import my.edu.apu.enums.Role;
import my.edu.apu.models.Appointment;
import my.edu.apu.models.FailedLoginAttempt;
import my.edu.apu.models.Feedback;
import my.edu.apu.models.Student;
import my.edu.apu.models.Supervisor;
import my.edu.apu.models.User;
import my.edu.apu.repositories.*;
import my.edu.apu.utils.AppNavigator;
import my.edu.apu.views.panels.SystemAdminView;

/**
 *
 * @author pakdad
 */
public class SystemAdminController {

    private final String systemAdminId;
    private final SystemAdminView systemAdminView;
    private final AppNavigator navigator;
    private final UserRepository userRepo;
    private final StudentRepository studentRepo;
    private final SupervisorRepository supervisorRepo;
    private final AppointmentRepository appointmentRepo;
    private final FeedbackRepository feedbackRepo;
    private final FailedLoginAttemptRepository loginAttemptRepo;

    // Set up date formatters
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy"); // e.g. 05/10/25
    private final DateTimeFormatter dobDateFormatter = DateTimeFormatter.ofPattern("uuuu/MM/dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");    // e.g. 14:30

    // Table models
    private DefaultTableModel userAccountModel;
    private DefaultTableModel studentModel;
    private DefaultTableModel supervisorModel;
    private DefaultTableModel facultyAdminModel;

    public SystemAdminController(SystemAdminView systemAdminView, AppNavigator navigator, UserRepository userRepo, StudentRepository studentRepo, SupervisorRepository supervisorRepo, AppointmentRepository appointmentRepo, FeedbackRepository feedbackRepo, FailedLoginAttemptRepository loginAttemptRepo, String systemAdminId) {
        this.systemAdminView = systemAdminView;
        this.systemAdminId = systemAdminId;
        this.navigator = navigator;
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.supervisorRepo = supervisorRepo;
        this.appointmentRepo = appointmentRepo;
        this.feedbackRepo = feedbackRepo;
        this.loginAttemptRepo = loginAttemptRepo;

        initializeSystemAdminView();
    }

    private void initializeSystemAdminView() {
        initializeDateAndTime();
        initializeDashboard();
        initializeUsers();
        initializeStudentAccounts();
        initializeSupervisorAccounts();
        initializeFacultyAdminAccounts();
        manageUserAccounts();
        manageStudents();
        manageSupervisors();
        manageFacultyAdmins();
    }

    private void initializeDashboard() {
        // Calculate totals
        int totalStudents = studentRepo.findAll().size();
        int totalSupervisors = supervisorRepo.findAll().size();
        int totalFacultyAdmins = 0;

        // Count number of faculty admin users
        List<User> users = userRepo.findAll();
        for (User u : users) {
            if (u.getRole().equals(Role.FACULTY_ADMIN)) {
                totalFacultyAdmins++;
            }
        }

        // Attach totals to respective view
        systemAdminView.getTxtTotalStudents().setText(Integer.toString(totalStudents));
        systemAdminView.getTxtTotalSupervisors().setText(Integer.toString(totalSupervisors));
        systemAdminView.getTxtTotalFacultyAdmin().setText(Integer.toString(totalFacultyAdmins));
    }

    private void initializeDateAndTime() {
        // Find the student from the student repository
        User systemAdmin = userRepo.findById(systemAdminId).get();
        String name = systemAdmin.getName();

        // Set the student's username in the studentView
        systemAdminView.getTxtUsername().setText(name);

        // Set the date once (doesn't need live updating)
        systemAdminView.getTxtDate().setText(LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM, yyyy")));

        // Set the time initially
        systemAdminView.getTxtTime().setText(LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a")));

        // Update the time every second
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    systemAdminView.getTxtTime().setText(LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a")));
                });
            }
        }, 0, 1000);
    }

    private void initializeUsers() {
        // Create user account model
        String[] userAccountModelColumns = {"ID", "Name", "Email", "Role", "Account Status"};
        userAccountModel = new DefaultTableModel(userAccountModelColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        loadUsers();

        // Attach model to table
        systemAdminView.getTblUserAccounts().setModel(userAccountModel);

        // Hide the id column
        TableColumn idColumn = systemAdminView.getTblUserAccounts().getColumnModel().getColumn(0);
        systemAdminView.getTblUserAccounts().removeColumn(idColumn);
    }

    private void loadUsers() {
        // Clear the table model
        userAccountModel.setRowCount(0);

        // Get filter inputs
        String nameFilter = systemAdminView.getTxtUsernameSearch().getText().trim().toLowerCase();
        String emailFilter = systemAdminView.getTxtEmailSearch().getText().trim().toLowerCase();
        String accStatusFilter = String.valueOf(systemAdminView.getComboAccStatusSearch().getSelectedItem());

        // Get all users
        List<User> users = userRepo.findAll();
        List<User> filteredUsers = new ArrayList<>();

        for (User u : users) {
            // Skip system admins
            if (u.getRole().equals(Role.SYSTEM_ADMIN)) {
                continue;
            }

            // --- Apply filters ---
            boolean matchesName = nameFilter.isEmpty() || u.getName().toLowerCase().contains(nameFilter);
            boolean matchesEmail = emailFilter.isEmpty() || u.getUniEmail().toLowerCase().contains(emailFilter);
            boolean matchesStatus;

            switch (accStatusFilter) {
                case "Enabled" ->
                    matchesStatus = u.getAccountStatus();
                case "Disabled" ->
                    matchesStatus = !u.getAccountStatus();
                default ->
                    matchesStatus = true; // "All" or any unexpected value
            };

            // Only include users matching all criteria
            if (matchesName && matchesEmail && matchesStatus) {
                filteredUsers.add(u);
            }
        }

        // Populate the table model
        for (User u : filteredUsers) {
            String accStatus = u.getAccountStatus() ? "Enabled" : "Disabled";
            userAccountModel.addRow(new Object[]{
                u.getId(),
                u.getName(),
                u.getUniEmail(),
                u.getRole().toString(),
                accStatus
            });
        }
    }

    private void initializeStudentAccounts() {
        // Initialize the student model
        String[] columns = {"ID", "Name", "Email", "Intake", "Program"};
        studentModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Fill up model
        loadStudents();

        // Attach model to table
        systemAdminView.getTblStudentAccounts().setModel(studentModel);

        // Hide the id column
        TableColumn idColumn = systemAdminView.getTblStudentAccounts().getColumnModel().getColumn(0);
        systemAdminView.getTblStudentAccounts().removeColumn(idColumn);

    }

    private void loadStudents() {
        // Empty out the model
        studentModel.setRowCount(0);

        // Fill up model
        List<Student> students = studentRepo.findAll();
        for (Student stud : students) {
            String id = stud.getId();
            String name = stud.getName();
            String uniEmail = stud.getUniEmail();
            String intake = stud.getIntake().toString();
            String program = stud.getProgram().toString();

            studentModel.addRow(new Object[]{id, name, uniEmail, intake, program});
        }
    }

    private void manageStudents() {
        systemAdminView.getBtnDeleteStudent().setEnabled(false);
        systemAdminView.getBtnUpdateStudent().setEnabled(false);

        // Listen for when the user selects a row
        systemAdminView.getTblStudentAccounts().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                // Set buttons to enabled
                systemAdminView.getBtnDeleteStudent().setEnabled(true);
                systemAdminView.getBtnUpdateStudent().setEnabled(true);

                // Prefill values
                prefillStudentDetails();
            }
        });

        // Listeners for button
        systemAdminView.getBtnCreateStudent().addActionListener(e -> createStudent());
        systemAdminView.getBtnUpdateStudent().addActionListener(e -> updateStudent());
        systemAdminView.getBtnDeleteStudent().addActionListener(e -> deleteStudent());
    }

    private void createStudent() {
        // Get intake and program lists
        Intake[] intakes = Intake.values();
        Program[] programs = Program.values();

        // Get updated student details
        String name = systemAdminView.getTxtStudentName().getText();
        String email = systemAdminView.getTxtStudentEmail().getText();
        String password = new String(systemAdminView.getTxtStudentPassword().getPassword());
        String dobText = systemAdminView.getTxtStudentDob().getText().trim();
        Intake intake = intakes[systemAdminView.getComboIntake().getSelectedIndex()];
        Program program = programs[systemAdminView.getComboProgram().getSelectedIndex()];

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(systemAdminView, "Please fill up all of the student's details.");
            return;
        }

        if (!dobText.matches("\\d{4}/\\d{2}/\\d{2}")) {
            JOptionPane.showMessageDialog(systemAdminView, "Invalid format! Please use yyyy/MM/dd");
            return;
        }

        try {
            // Parse strictly (no fake dates like 2023/02/29)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu/MM/dd")
                    .withResolverStyle(ResolverStyle.STRICT);

            LocalDate dob = LocalDate.parse(dobText, formatter);

            // Create new objects
            Student student = new Student(name, email, password);
            User user = new User(name, email, password, Role.STUDENT);

            // Set student details
            student.setSupervisorId("");
            student.setDob(dob);
            student.setIntake(intake);
            student.setProgram(program);

            // Add both objects to repositories
            studentRepo.add(student);
            userRepo.add(user);

            // Update model
            loadStudents();
            loadUsers();

            // empty out details
            systemAdminView.getTxtStudentName().setText("");
            systemAdminView.getTxtStudentEmail().setText("");
            systemAdminView.getTxtStudentPassword().setText("");
            systemAdminView.getTxtStudentDob().setText("");
            systemAdminView.getComboIntake().setSelectedIndex(0);
            systemAdminView.getComboProgram().setSelectedIndex(0);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(systemAdminView, "Invalid date! Please enter a real calendar date.");
        }
    }

    private void updateStudent() {
        // Get selected row's student ID
        int row = systemAdminView.getTblStudentAccounts().getSelectedRow();
        String studentId = String.valueOf(studentModel.getValueAt(row, 0));

        // Get student from id
        User user = userRepo.findById(studentId).get();
        Student student = studentRepo.findById(studentId).get();

        // Get intake and program lists
        Intake[] intakes = Intake.values();
        Program[] programs = Program.values();

        // Get updated student details
        String name = systemAdminView.getTxtStudentName().getText();
        String email = systemAdminView.getTxtStudentEmail().getText();
        String password = new String(systemAdminView.getTxtStudentPassword().getPassword());
        String dobText = systemAdminView.getTxtStudentDob().getText().trim();
        Intake intake = intakes[systemAdminView.getComboIntake().getSelectedIndex()];
        Program program = programs[systemAdminView.getComboProgram().getSelectedIndex()];

        if (!dobText.matches("\\d{4}/\\d{2}/\\d{2}")) {
            JOptionPane.showMessageDialog(systemAdminView, "Invalid format! Please use yyyy/MM/dd");
            return;
        }

        try {
            // Parse strictly (no fake dates like 2023/02/29)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu/MM/dd")
                    .withResolverStyle(ResolverStyle.STRICT);

            LocalDate dob = LocalDate.parse(dobText, formatter);
            // Update base student details
            user.setName(name);
            user.setUniEmail(email);
            user.setPassword(password);
            student.setName(name);
            student.setUniEmail(email);
            student.setPassword(password);
            student.setDob(dob);

            // Update specific student details
            student.setIntake(intake);
            student.setProgram(program);

            // Save changes
            studentRepo.save();
            userRepo.save();

            // Update model
            loadStudents();
            loadUsers();

            // Set update button to disabled
            systemAdminView.getBtnUpdateStudent().setEnabled(false);

            // empty out details
            systemAdminView.getTxtStudentName().setText("");
            systemAdminView.getTxtStudentEmail().setText("");
            systemAdminView.getTxtStudentPassword().setText("");
            systemAdminView.getTxtStudentDob().setText("");
            systemAdminView.getComboIntake().setSelectedIndex(0);
            systemAdminView.getComboProgram().setSelectedIndex(0);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(systemAdminView, "Invalid date! Please enter a real calendar date.");
        }
    }

    private void deleteStudent() {
        // Get selected row's student ID
        int row = systemAdminView.getTblStudentAccounts().getSelectedRow();
        String studentId = String.valueOf(studentModel.getValueAt(row, 0));

        // Ensure user actually wants to delete the student
        int confirmation = JOptionPane.showConfirmDialog(systemAdminView, "Are you sure you want to delete this student?");
        if (confirmation != JOptionPane.OK_OPTION) {
            return;
        }

        // Remove student
        userRepo.remove(studentId);
        studentRepo.remove(studentId);

        // Remove student's appointments
        List<Appointment> appointments = appointmentRepo.findByStudentId(studentId);
        for (Appointment appt : appointments) {
            appointmentRepo.remove(appt.getId());
        }

        // Remove student's appointments
        List<Feedback> feedbacks = feedbackRepo.findByStudentId(studentId);
        for (Feedback fb : feedbacks) {
            feedbackRepo.remove(fb.getId());
        }

        // Update models
        loadStudents();
        loadUsers();

        // Set button to disabled
        systemAdminView.getBtnDeleteStudent().setEnabled(false);
    }

    private void prefillStudentDetails() {
        // Initialize index values
        int intakeIndex = 0;
        int programIndex = 0;

        // Get selected row's student ID
        int row = systemAdminView.getTblStudentAccounts().getSelectedRow();
        String studentId = String.valueOf(studentModel.getValueAt(row, 0));

        // Get student from id
        Student student = studentRepo.findById(studentId).get();

        // Get intake and program indexes for combo box
        Intake[] intakes = Intake.values();
        Program[] programs = Program.values();
        for (int i = 0; i < intakes.length; i++) {
            if (student.getIntake().equals(intakes[i])) {
                intakeIndex = i;
            }
        }
        for (int i = 0; i < programs.length; i++) {
            if (student.getProgram().equals(programs[i])) {
                programIndex = i;
            }
        }

        // Prefill details
        systemAdminView.getTxtStudentName().setText(student.getName());
        systemAdminView.getTxtStudentEmail().setText(student.getUniEmail());
        systemAdminView.getTxtStudentPassword().setText(student.getPassword());
        systemAdminView.getTxtStudentDob().setText(student.getDob().format(dobDateFormatter));
        systemAdminView.getComboIntake().setSelectedIndex(intakeIndex);
        systemAdminView.getComboProgram().setSelectedIndex(programIndex);
    }

    private void initializeSupervisorAccounts() {
        // Initialize supervisor model
        String[] columns = {"ID", "Name", "Email"};
        supervisorModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Load supervisor data in model
        loadSupervisors();

        // Attach model to table
        systemAdminView.getTblSupervisorAccounts().setModel(supervisorModel);

        // Hide the id column
        TableColumn idColumn = systemAdminView.getTblSupervisorAccounts().getColumnModel().getColumn(0);
        systemAdminView.getTblSupervisorAccounts().removeColumn(idColumn);
    }

    private void loadSupervisors() {
        // empty the model
        supervisorModel.setRowCount(0);

        // load all the supervisors
        List<Supervisor> supervisors = supervisorRepo.findAll();

        for (Supervisor sup : supervisors) {
            supervisorModel.addRow(new Object[]{sup.getId(), sup.getName(), sup.getUniEmail()});
        }
    }

    private void manageSupervisors() {
        systemAdminView.getBtnDeleteSupervisor().setEnabled(false);
        systemAdminView.getBtnUpdateSupervisor().setEnabled(false);

        // Listen for when the user selects a row
        systemAdminView.getTblSupervisorAccounts().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                systemAdminView.getBtnDeleteSupervisor().setEnabled(true);
                systemAdminView.getBtnUpdateSupervisor().setEnabled(true);

                // Prefill values
                prefillSupervisorDetails();
            }
        });

        // Listeners for button
        systemAdminView.getBtnCreateSupervisor().addActionListener(e -> createSupervisor());
        systemAdminView.getBtnUpdateSupervisor().addActionListener(e -> updateSupervisor());
        systemAdminView.getBtnDeleteSupervisor().addActionListener(e -> deleteSupervisor());
    }

    private void createSupervisor() {
        // Get supervisor details
        String name = systemAdminView.getTxtSupervisorName().getText().trim();
        String email = systemAdminView.getTxtSupervisorEmail().getText().trim();
        String password = new String(systemAdminView.getTxtSupervisorPassword().getPassword()).trim();

        // Validate the values inside each field
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(systemAdminView, "Please make sure all fields are filled in.", "Error Creating Supervisor", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (userRepo.findByUniEmail(email).isPresent()) {
            JOptionPane.showMessageDialog(systemAdminView, "A user with this email already exists.", "Error Creating Supervisor", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create supervisor and user objects
        Supervisor sup = new Supervisor(name, email, password);
        User user = new User(name, email, password, Role.SUPERVISOR);

        // Add them to the repositories
        supervisorRepo.add(sup);
        userRepo.add(user);

        // Update supervisor and user models
        loadSupervisors();
        loadUsers();

        // Reset supervisor form
        resetSupervisorForm();
    }

    private void updateSupervisor() {
        // Get the supervisor ID
        int row = systemAdminView.getTblSupervisorAccounts().getSelectedRow();
        String supId = String.valueOf(supervisorModel.getValueAt(row, 0));

        // Get the supervisor and user objects
        Supervisor sup = supervisorRepo.findById(supId).get();
        User user = userRepo.findById(supId).get();

        // Get updated supervisor details
        String updatedName = systemAdminView.getTxtSupervisorName().getText().trim();
        String updatedEmail = systemAdminView.getTxtSupervisorEmail().getText().trim();
        String updatedPassword = new String(systemAdminView.getTxtSupervisorPassword().getPassword()).trim();

        // Validate the values inside each field
        if (updatedName.isBlank() || updatedEmail.isBlank() || updatedPassword.isBlank()) {
            JOptionPane.showMessageDialog(systemAdminView, "Please make sure all fields are filled in.", "Error Updating Supervisor", JOptionPane.ERROR_MESSAGE);
            return;
        } // Ensure that the email is unique, if changed
        else if (!sup.getUniEmail().equals(updatedEmail) && userRepo.findByUniEmail(updatedEmail).isPresent()) {
            JOptionPane.showMessageDialog(systemAdminView, "A user with this email already exists.", "Error Updating Supervisor", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update fields respectively in both objects
        sup.setName(updatedName);
        sup.setUniEmail(updatedEmail);
        sup.setPassword(updatedPassword);
        user.setName(updatedName);
        user.setUniEmail(updatedEmail);
        user.setPassword(updatedPassword);

        // Save changes
        supervisorRepo.save();
        userRepo.save();

        // Update supervisor and user models
        loadSupervisors();
        loadUsers();

        // Reset the supervisor form
        resetSupervisorForm();
    }

    private void deleteSupervisor() {
        // Get the supervisor ID
        int row = systemAdminView.getTblSupervisorAccounts().getSelectedRow();
        String supId = String.valueOf(supervisorModel.getValueAt(row, 0));

        // Remove the supervisor
        supervisorRepo.remove(supId);

        // Update supervisor and user list
        loadSupervisors();
        loadUsers();

        // Reset the supervisor form
        resetSupervisorForm();
    }

    private void resetSupervisorForm() {
        // Turn off deletion and update buttons
        systemAdminView.getBtnDeleteSupervisor().setEnabled(false);
        systemAdminView.getBtnUpdateSupervisor().setEnabled(false);

        // Empty out fields
        systemAdminView.getTxtSupervisorName().setText("");
        systemAdminView.getTxtSupervisorEmail().setText("");
        systemAdminView.getTxtSupervisorPassword().setText("");
    }

    private void prefillSupervisorDetails() {
        // Get the supervisor ID
        int row = systemAdminView.getTblSupervisorAccounts().getSelectedRow();
        String supId = String.valueOf(supervisorModel.getValueAt(row, 0));

        // Get supervisor and fill up fields respectively
        Supervisor sup = supervisorRepo.findById(supId).get();
        systemAdminView.getTxtSupervisorName().setText(sup.getName());
        systemAdminView.getTxtSupervisorEmail().setText(sup.getUniEmail());
        systemAdminView.getTxtSupervisorPassword().setText(sup.getPassword());
    }

    private void initializeFacultyAdminAccounts() {
        // Initialize faculty admin model
        String[] columns = {"ID", "Name", "Email"};
        facultyAdminModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Load faculty admin data in model
        loadFacultyAdmins();

        // Attach model to table
        systemAdminView.getTblFacultyAdminAccounts().setModel(facultyAdminModel);

        // Hide the id column
        TableColumn idColumn = systemAdminView.getTblFacultyAdminAccounts().getColumnModel().getColumn(0);
        systemAdminView.getTblFacultyAdminAccounts().removeColumn(idColumn);
    }

    private void loadFacultyAdmins() {
        // Empty out model
        facultyAdminModel.setRowCount(0);

        // Get all users
        List<User> users = userRepo.findAll();

        for (User user : users) {
            // Only continue if the user is a faculty admin
            if (user.getRole().equals(Role.FACULTY_ADMIN)) {
                // Add row
                facultyAdminModel.addRow(new Object[]{user.getId(), user.getName(), user.getUniEmail()});
            }
        }
    }

    private void manageFacultyAdmins() {
        systemAdminView.getBtnDeleteFacultyAdmin().setEnabled(false);
        systemAdminView.getBtnUpdateFacultyAdmin().setEnabled(false);

        // Listen for when the user selects a row
        systemAdminView.getTblFacultyAdminAccounts().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                systemAdminView.getBtnDeleteFacultyAdmin().setEnabled(true);
                systemAdminView.getBtnUpdateFacultyAdmin().setEnabled(true);

                // Prefill values
                prefillFacultyAdminDetails();
            }
        });

        // Listeners for button
        systemAdminView.getBtnCreateFacultyAdmin().addActionListener(e -> createFacultyAdmin());
        systemAdminView.getBtnUpdateFacultyAdmin().addActionListener(e -> updateFacultyAdmin());
        systemAdminView.getBtnDeleteFacultyAdmin().addActionListener(e -> deleteFacultyAdmin());
    }

    private void createFacultyAdmin() {
        // Get faculty admin details
        String name = systemAdminView.getTxtFacultyAdminName().getText().trim();
        String email = systemAdminView.getTxtFacultyAdminEmail().getText().trim();
        String password = new String(systemAdminView.getTxtFacultyAdminPassword().getPassword()).trim();

        // Validate the values inside each field
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(systemAdminView, "Please make sure all fields are filled in.", "Error Creating Faculty Admin", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (userRepo.findByUniEmail(email).isPresent()) {
            JOptionPane.showMessageDialog(systemAdminView, "A user with this email already exists.", "Error Creating Faculty Admin", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create faculty admin object
        User facAdmin = new User(name, email, password, Role.FACULTY_ADMIN);

        // Add faculty admin to repo
        userRepo.add(facAdmin);

        // Update faculty admin and user models
        loadFacultyAdmins();
        loadUsers();

        // Reset faculty admin form
        resetFacultyAdminForm();
    }

    private void updateFacultyAdmin() {
        // Get the faculty admin ID
        int row = systemAdminView.getTblFacultyAdminAccounts().getSelectedRow();
        String facAdminId = String.valueOf(facultyAdminModel.getValueAt(row, 0));

        // Get the faculty admin
        User facAdmin = userRepo.findById(facAdminId).get();

        // Get updated faculty admin details
        String updatedName = systemAdminView.getTxtFacultyAdminName().getText().trim();
        String updatedEmail = systemAdminView.getTxtFacultyAdminEmail().getText().trim();
        String updatedPassword = new String(systemAdminView.getTxtFacultyAdminPassword().getPassword()).trim();

        // Validate the values inside each field
        if (updatedName.isBlank() || updatedEmail.isBlank() || updatedPassword.isBlank()) {
            JOptionPane.showMessageDialog(systemAdminView, "Please make sure all fields are filled in.", "Error Updating Faculty Admin", JOptionPane.ERROR_MESSAGE);
            return;
        } // Ensure that the email is unique, if changed
        else if (!facAdmin.getUniEmail().equals(updatedEmail) && userRepo.findByUniEmail(updatedEmail).isPresent()) {
            JOptionPane.showMessageDialog(systemAdminView, "A user with this email already exists.", "Error Updating Faculty Admin", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update fields respectively in both objects
        facAdmin.setName(updatedName);
        facAdmin.setUniEmail(updatedEmail);
        facAdmin.setPassword(updatedPassword);

        // Save changes
        userRepo.save();

        // Update faculty admin and user models
        loadFacultyAdmins();
        loadUsers();

        // Reset the faculty admin form
        resetFacultyAdminForm();
    }

    private void deleteFacultyAdmin() {
        // Get the faculty admin ID
        int row = systemAdminView.getTblFacultyAdminAccounts().getSelectedRow();
        String facAdminId = String.valueOf(facultyAdminModel.getValueAt(row, 0));

        // Delete the faculty admin
        userRepo.remove(facAdminId);

        // Update models respectively
        loadFacultyAdmins();
        loadUsers();

        // Reset form details
        resetFacultyAdminForm();
    }

    private void prefillFacultyAdminDetails() {
        // Get the faculty admin ID
        int row = systemAdminView.getTblFacultyAdminAccounts().getSelectedRow();
        String facAdminId = String.valueOf(facultyAdminModel.getValueAt(row, 0));

        // Get faculty admin and fill up fields respectively
        User facAdmin = userRepo.findById(facAdminId).get();
        systemAdminView.getTxtFacultyAdminName().setText(facAdmin.getName());
        systemAdminView.getTxtFacultyAdminEmail().setText(facAdmin.getUniEmail());
        systemAdminView.getTxtFacultyAdminPassword().setText(facAdmin.getPassword());
    }

    private void resetFacultyAdminForm() {
        // Turn off deletion and update buttons
        systemAdminView.getBtnDeleteFacultyAdmin().setEnabled(false);
        systemAdminView.getBtnUpdateFacultyAdmin().setEnabled(false);

        // Empty out fields
        systemAdminView.getTxtFacultyAdminName().setText("");
        systemAdminView.getTxtFacultyAdminEmail().setText("");
        systemAdminView.getTxtFacultyAdminPassword().setText("");
    }

    private void manageUserAccounts() {
        // Initially set both buttons as disabled
        systemAdminView.getBtnUpdateUserDetails().setEnabled(false);
        systemAdminView.getBtnDeleteUser().setEnabled(false);

        systemAdminView.getTblUserAccounts().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                prefillUserAccountDetails();
                systemAdminView.getBtnUpdateUserDetails().setEnabled(true);
                systemAdminView.getBtnDeleteUser().setEnabled(true);
            }
        });

        systemAdminView.getTxtUsernameSearch().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                loadUsers();
            }
        });

        systemAdminView.getTxtEmailSearch().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                loadUsers();
            }
        });

        systemAdminView.getComboAccStatusSearch().addActionListener(e -> loadUsers());
        systemAdminView.getBtnUpdateUserDetails().addActionListener(e -> updateUserDetails());
        systemAdminView.getBtnDeleteUser().addActionListener(e -> deleteUserAccount());
        systemAdminView.getBtnResetSearch().addActionListener(e -> resetSearch());
    }

    private void prefillUserAccountDetails() {
        // Get the selected row from the model
        int row = systemAdminView.getTblUserAccounts().getSelectedRow();
        String userId = String.valueOf(userAccountModel.getValueAt(row, 0));

        // Update user data in the respective fields
        User u = userRepo.findById(userId).get();
        systemAdminView.getTxtName().setText(u.getName());
        systemAdminView.getTxtUserEmail().setText(u.getUniEmail());

        // Update account status field
        if (u.getAccountStatus()) {
            systemAdminView.getComboUserAccStatus().setSelectedIndex(1);
        } else {
            systemAdminView.getComboUserAccStatus().setSelectedIndex(2);
        }
    }

    private void resetUserAccountDetails() {
        // Reset text fields and buttons
        systemAdminView.getTxtName().setText("");
        systemAdminView.getTxtUserEmail().setText("");
        systemAdminView.getBtnUpdateUserDetails().setEnabled(false);
        systemAdminView.getBtnDeleteUser().setEnabled(false);
        systemAdminView.getComboUserAccStatus().setSelectedIndex(0);
    }

    private void deleteUserAccount() {
        // Ensure the system admin wishes to delete this user
        int confirmation = JOptionPane.showConfirmDialog(systemAdminView, "Are you sure you want to delete this user?", "Confirm User Deletion", JOptionPane.WARNING_MESSAGE);
        if (confirmation != JOptionPane.OK_OPTION) {
            return;
        }

        // Get the selected row from the model
        int row = systemAdminView.getTblUserAccounts().getSelectedRow();
        String userId = String.valueOf(userAccountModel.getValueAt(row, 0));

        User u = userRepo.findById(userId).get();

        // Delete user
        userRepo.remove(userId);

        switch (u.getRole()) {
            case Role.STUDENT ->
                studentRepo.remove(userId);
            case Role.SUPERVISOR ->
                supervisorRepo.remove(userId);
        }

        // Update ALL user models
        loadUsers();
        loadStudents();
        loadSupervisors();
        loadFacultyAdmins();

        // Reset text fields and buttons
        resetUserAccountDetails();
    }

    private void updateUserDetails() {
        // Get the selected row from the model
        int row = systemAdminView.getTblUserAccounts().getSelectedRow();
        String userId = String.valueOf(userAccountModel.getValueAt(row, 0));

        // Get the user
        User user = userRepo.findById(userId).get();

        // Get new name and email
        String updatedName = systemAdminView.getTxtName().getText();
        String updatedEmail = systemAdminView.getTxtUserEmail().getText();

        // Get updated account status
        String accStatusChoice = String.valueOf(systemAdminView.getComboUserAccStatus().getSelectedItem());
        boolean updatedAccStatus;

        // Handle all cases of combo box
        switch (accStatusChoice) {
            case "Enabled" ->
                updatedAccStatus = true;

            case "Disabled" -> {
                if (user.getAccountStatus()) {
                    String msg = """
                Are you sure you want to disable this user's account?
                They will no longer be able to access it until re-enabled.
                """;
                    int confirmation = JOptionPane.showConfirmDialog(
                            systemAdminView,
                            msg,
                            "Confirm Account Disabling",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );
                    if (confirmation != JOptionPane.OK_OPTION) {
                        return;
                    }
                }
                updatedAccStatus = false;
            }

            default -> {
                JOptionPane.showMessageDialog(
                        systemAdminView,
                        "Please select a valid account status.",
                        "Error Updating User Details",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }

        // If the updated email is not equal to the same existing email and a user with the same email is found
        if (!updatedEmail.equalsIgnoreCase(user.getUniEmail()) && userRepo.findByUniEmail(updatedEmail).isPresent()) {
            JOptionPane.showMessageDialog(systemAdminView, "Please choose a unique email.", "Error Updating User", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update user details and save
        user.setName(updatedName);
        user.setUniEmail(updatedEmail);
        user.setAccountStatus(updatedAccStatus);
        userRepo.save();

        // Update ALL user models
        loadUsers();
        loadStudents();
        loadSupervisors();
        loadFacultyAdmins();

        // Reset text fields and buttons
        resetUserAccountDetails();
    }

    private void resetSearch() {
        systemAdminView.getTxtEmailSearch().setText("");
        systemAdminView.getTxtUsernameSearch().setText("");
        systemAdminView.getComboAccStatusSearch().setSelectedIndex(0);
        loadUsers();
    }
}
