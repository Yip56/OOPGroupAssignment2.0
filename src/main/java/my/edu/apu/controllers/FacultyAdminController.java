/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.controllers;

import java.awt.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import my.edu.apu.enums.Intake;
import my.edu.apu.enums.Program;
import my.edu.apu.models.Appointment;
import my.edu.apu.models.Student;
import my.edu.apu.models.Supervisor;
import my.edu.apu.repositories.*;
import my.edu.apu.utils.AppNavigator;
import my.edu.apu.views.panels.FacultyAdminView;

/**
 *
 * @author pakdad
 */
public class FacultyAdminController {

    private final String facultyAdminId;
    private final FacultyAdminView facultyAdminView;
    private final AppNavigator navigator;
    private final UserRepository userRepo;
    private final StudentRepository studentRepo;
    private final SupervisorRepository supervisorRepo;
    private final AppointmentRepository appointmentRepo;

    // Set up date formatters
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy"); // e.g. 05/10/25
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");    // e.g. 14:30

    // Set up table models
    private DefaultTableModel studentModel;
    private DefaultTableModel studentAssignmentModel;
    private DefaultTableModel supervisorListModel;
    private DefaultTableModel supervisorAssignmentModel;
    private DefaultTableModel supervisorStudentsListModel;
    private DefaultTableModel supervisorTimeslotsModel;
    private DefaultTableModel supervisorWorkloadReportModel;
    private DefaultTableModel timeslotUtilizationReportModel;

    public FacultyAdminController(FacultyAdminView facultyAdminView, AppNavigator navigator, UserRepository userRepo, StudentRepository studentRepo, SupervisorRepository supervisorRepo, AppointmentRepository appointmentRepo, String facultyAdminId) {
        this.facultyAdminView = facultyAdminView;
        this.facultyAdminId = facultyAdminId;
        this.navigator = navigator;
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.supervisorRepo = supervisorRepo;
        this.appointmentRepo = appointmentRepo;

        initializeFacultyAdminView();
    }

    private void initializeFacultyAdminView() {
        initializeDashboard();
        initializeStudents();
        initializeSupervisors();
        initializeSupervisorWorkloadReport();
        initializeDistributionReport();
        initializeTimeslotUtilizationReport();
        manageStudentSearch();
        manageSupervisorList();
        manageSupervisorAssignments();
        manageSignOut();
    }

    private void initializeDashboard() {
        // Get faculty admin name via their ID to set in the dashboard
        String name = userRepo.findById(facultyAdminId).get().getName();
        facultyAdminView.getTxtUsername().setText(name);

        // Calculate respective totals
        int totalStudents = studentRepo.findAll().size();
        int totalSupervisors = supervisorRepo.findAll().size();
        int totalUnassignedStudents = studentRepo.findBySupervisorId("").size();

        // Set totals respectively
        facultyAdminView.getTxtTotalStudents().setText(Integer.toString(totalStudents));
        facultyAdminView.getTxtTotalSupervisors().setText(Integer.toString(totalSupervisors));
        facultyAdminView.getTxtUnassignedStudents().setText(Integer.toString(totalUnassignedStudents));

        // Set the date once (doesn't need live updating)
        facultyAdminView.getTxtDate().setText(LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM, yyyy")));

        // Set the time initially
        facultyAdminView.getTxtTime().setText(LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a")));

        // Update the time every second
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    facultyAdminView.getTxtTime().setText(LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a")));
                });
            }
        }, 0, 1000);
    }

    private void initializeStudents() {
        // Create student list table model
        String[] studentListColumns = {"Student Name", "Sup. Name", "Intake", "Program", "DOB"};
        studentModel = new DefaultTableModel(studentListColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ensure no cells are editable
            }
        };

        // Create student assignment table model
        String[] studentAssignmentColumns = {"ID", "Student Name", "Sup. Name", "Intake", "Program", "DOB"};
        studentAssignmentModel = new DefaultTableModel(studentAssignmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ensure no cells are editable
            }
        };

        // Fill up models with data
        loadStudents(false);
        loadStudentAssignments(false);

        // Intialize the student assignment filter
        initializeStudentAssignmentFilter();

        // Attach models to tables respectively
        facultyAdminView.getTblStudents().setModel(studentModel);
        facultyAdminView.getTblAssignmentStudents().setModel(studentAssignmentModel);

        // hide the first column (ID) of the supervisor list table
        TableColumn idColumn = facultyAdminView.getTblAssignmentStudents().getColumnModel().getColumn(0);
        facultyAdminView.getTblAssignmentStudents().removeColumn(idColumn);
    }

    private void loadStudents(boolean applyFilters) {
        // Create flags
        boolean applyIntakeSearch = true;
        boolean applyProgramSearch = true;
        boolean applyStudentNameSearch = true;
        boolean applySupervisorNameSearch = true;

        // Get respective search values
        String intakeSearch = facultyAdminView.getComboIntakeFilter().getSelectedItem().toString();
        String programSearch = facultyAdminView.getComboProgramFilter().getSelectedItem().toString();
        String studentNameSearch = facultyAdminView.getTxtStudentName().getText();
        String supervisorNameSearch = facultyAdminView.getTxtSupervisorName().getText();

        // Update flags
        if (intakeSearch.equals("All")) {
            applyIntakeSearch = false;
        }
        if (programSearch.equals("All")) {
            applyProgramSearch = false;
        }
        if (studentNameSearch.isBlank()) {
            applyProgramSearch = false;
        }
        if (supervisorNameSearch.isBlank()) {
            applyProgramSearch = false;
        }

        // Empty out the model
        studentModel.setRowCount(0);

        List<Student> students = studentRepo.findAll();
        for (Student stud : students) {
            // Get respective values
            String studentName = stud.getName();
            String intake = stud.getIntake().toString();
            String program = stud.getProgram().toString();
            String dob = stud.getDob().format(dateFormatter);

            // Attempt to find respective supervisor
            Optional<Supervisor> res = supervisorRepo.findById(stud.getSupervisorId());

            // Map supervisor to name or "–" if empty
            String supName = res
                    .map(sup -> sup.getName())
                    .orElse("–");

            // Apply filters if needed
            if (applyFilters) {
                if (applyIntakeSearch && !intake.equals(intakeSearch)) {
                    continue;
                } else if (applyProgramSearch && !program.equals(programSearch)) {
                    continue;
                } else if (applyStudentNameSearch && !studentName.toLowerCase().contains(studentNameSearch.toLowerCase())) {
                    continue;
                } else if (applySupervisorNameSearch && !supName.toLowerCase().contains(supervisorNameSearch.toLowerCase())) {
                    continue;
                }
            }

            // Add row to student model
            studentModel.addRow(new Object[]{studentName, supName, intake, program, dob});
        }
    }

    private void initializeStudentAssignmentFilter() {
        // Create and fill up new combobox model with Supervisor objects
        DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>();
        model.addElement("Any Supervisor");
        model.addElement("No Supervisor");
        for (Supervisor s : supervisorRepo.findAll()) {
            model.addElement(s);
        }

        // Assign model to filter box
        facultyAdminView.getComboStudentFilter().setModel(model);

        // Custom renderer to show only the name in the dropdown
        facultyAdminView.getComboStudentFilter().setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                switch (value) {
                    case Supervisor supervisor ->
                        setText(supervisor.getName());
                    case String string ->
                        setText(string);
                    default ->
                        value.toString();
                }

                return this;
            }
        });
    }

    private void loadStudentAssignments(boolean applyFilters) {
        // Set up flag
        boolean applySupervisorFilter = true;

        // Get value from filter and update flag if needed
        Object selectedSupervisor = facultyAdminView.getComboStudentFilter().getSelectedItem();
        if ("Any Supervisor".equals(selectedSupervisor)) {
            applySupervisorFilter = false;
        }

        // Empty out the model
        studentAssignmentModel.setRowCount(0);

        List<Student> students = studentRepo.findAll();
        for (Student stud : students) {
            // Get respective values
            String studentName = stud.getName();
            String intake = stud.getIntake().toString();
            String program = stud.getProgram().toString();
            String dob = stud.getDob().format(dateFormatter);

            // Attempt to find respective supervisor
            Optional<Supervisor> optionalSupervisor = supervisorRepo.findById(stud.getSupervisorId());

            // Map supervisor to id or "" if empty for filter
            String supId = optionalSupervisor
                    .map(sup -> sup.getId())
                    .orElse("");

            // Map supervisor to name or "–" if empty
            String supName = optionalSupervisor
                    .map(sup -> sup.getName())
                    .orElse("–");

            // Apply filters if needed
            if (applyFilters) {
                boolean isNoSupervisor = selectedSupervisor.equals("No Supervisor");

                if (applySupervisorFilter && selectedSupervisor instanceof Supervisor s && s.getId().equals(supId)) {
                    continue;
                }
                if (applySupervisorFilter && isNoSupervisor && optionalSupervisor.isPresent()) {
                    continue;
                }
            }

            // Add row to student model
            studentAssignmentModel.addRow(new Object[]{stud.getId(), studentName, supName, intake, program, dob});
        }
    }

    private void manageSupervisorAssignments() {
        // Initially set both buttons as disabled
        facultyAdminView.getBtnAssignSupervisor().setEnabled(false);
        facultyAdminView.getBtnUnassignSupervisor().setEnabled(false);

        // Enable buttons respectively
        facultyAdminView.getTblAssignmentStudents().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                facultyAdminView.getBtnUnassignSupervisor().setEnabled(true);

                // Only set the assignSupervisor button as enabled if both rows are selected
                if (facultyAdminView.getTblAssignmentSupervisors().getSelectedRow() > -1) {
                    facultyAdminView.getBtnAssignSupervisor().setEnabled(true);
                }
            }
        });

        // Enable buttons respectively
        facultyAdminView.getTblAssignmentSupervisors().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                // Only set the assignSupervisor button as enabled if both rows are selected
                if (facultyAdminView.getTblAssignmentStudents().getSelectedRow() > -1) {
                    facultyAdminView.getBtnAssignSupervisor().setEnabled(true);
                }
            }
        });

        // Add action listeners respectively
        facultyAdminView.getComboStudentFilter().addActionListener(e -> loadStudentAssignments(true));
        facultyAdminView.getBtnAssignSupervisor().addActionListener(e -> assignSupervisor());
        facultyAdminView.getBtnUnassignSupervisor().addActionListener(e -> unassignSupervisor());
    }

    private void turnOffAssignmentButtons() {
        // Set both buttons as disabled
        facultyAdminView.getBtnAssignSupervisor().setEnabled(false);
        facultyAdminView.getBtnUnassignSupervisor().setEnabled(false);
    }

    private void assignSupervisor() {
        // Get selected student ID
        int studRow = facultyAdminView.getTblAssignmentStudents().getSelectedRow();
        String studId = String.valueOf(studentAssignmentModel.getValueAt(studRow, 0));

        // Get selected supervisor ID
        int supRow = facultyAdminView.getTblAssignmentSupervisors().getSelectedRow();
        String supId = String.valueOf(supervisorAssignmentModel.getValueAt(supRow, 0));

        // Find student and supervisor
        Student stud = studentRepo.findById(studId).get();
        Supervisor sup = supervisorRepo.findById(supId).get();

        // Update student ID and save
        stud.setSupervisorId(sup.getId());
        studentRepo.save();

        // Update models
        initializeDashboard();
        loadStudentAssignments(true);
        loadStudents(true);
        loadSupervisors();
        loadSupervisorWorkloadReport();
        loadTimeslotUtilizationReport();

        // Update buttons
        turnOffAssignmentButtons();
    }

    private void unassignSupervisor() {
        // Get selected student ID
        int row = facultyAdminView.getTblAssignmentStudents().getSelectedRow();
        String studId = String.valueOf(studentAssignmentModel.getValueAt(row, 0));

        // If supervisor ID doesn't exist, display error message
        Student stud = studentRepo.findById(studId).get();
        if (stud.getSupervisorId().isEmpty()) {
            JOptionPane.showMessageDialog(facultyAdminView, "No supervisor to unassign.", "Assignment Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Ensure user actually wishes to unassign supervisor
        int confirm = JOptionPane.showConfirmDialog(facultyAdminView, "Are you sure you'd like to unassign this supervisor?", "Confirm Unassignment", JOptionPane.INFORMATION_MESSAGE);
        if (confirm != JOptionPane.OK_OPTION) {
            return;
        }

        // Remove supervisor ID from student record and save
        stud.setSupervisorId("");
        studentRepo.save();

        // Remove appointments for respective student
        for (Appointment appt : appointmentRepo.findByStudentId(studId)) {
            appointmentRepo.remove(appt.getId());
        }

        // Update models
        initializeDashboard();
        loadStudentAssignments(true);
        loadStudents(true);
        loadSupervisors();
        loadSupervisorWorkloadReport();
        loadTimeslotUtilizationReport();

        // Update buttons
        turnOffAssignmentButtons();
    }

    private void manageStudentSearch() {
        facultyAdminView.getComboIntakeFilter().addActionListener(e -> loadStudents(true));
        facultyAdminView.getComboProgramFilter().addActionListener(e -> loadStudents(true));

        facultyAdminView.getTxtStudentName().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                loadStudents(true);
            }
        });

        facultyAdminView.getTxtSupervisorName().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                loadStudents(true);
            }
        });

        facultyAdminView.getBtnResetSearchFilters().addActionListener(e -> {
            facultyAdminView.getTxtStudentName().setText("");
            facultyAdminView.getTxtSupervisorName().setText("");
            facultyAdminView.getComboIntakeFilter().setSelectedIndex(0);
            facultyAdminView.getComboProgramFilter().setSelectedIndex(0);
        });
    }

    private void initializeSupervisors() {
        // Initialize supervisor list and supervisor assignment table models
        String[] supervisorColumns = {"ID", "Supervisor Name", "# of Assigned Students"};
        supervisorListModel = new DefaultTableModel(supervisorColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ensure cells are not editable
            }
        };

        supervisorAssignmentModel = new DefaultTableModel(supervisorColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ensure cells are not editable
            }
        };

        // Fill up table models
        loadSupervisors();

        // Initialize the supervisor's students table model
        String[] supervisorStudentsColumns = {"Student Name", "Intake", "Program", "DOB"};
        supervisorStudentsListModel = new DefaultTableModel(supervisorStudentsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ensure cells are not editable
            }
        };

        // Initialize the supervisor's timeslots table model
        String[] supervisorTimeslotsColumns = {"Date", "Time"};
        supervisorTimeslotsModel = new DefaultTableModel(supervisorTimeslotsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ensure cells are not editable
            }
        };

        // Attach models to table respectively
        facultyAdminView.getTblSupervisors().setModel(supervisorListModel);
        facultyAdminView.getTblAssignmentSupervisors().setModel(supervisorAssignmentModel);
        facultyAdminView.getTblSupervisorStudents().setModel(supervisorStudentsListModel);
        facultyAdminView.getTblSupervisorTimeslots().setModel(supervisorTimeslotsModel);

        // hide the first column (ID) of the supervisor list and supervisor assignment tables
        TableColumn supervisorListIdColumn = facultyAdminView.getTblSupervisors().getColumnModel().getColumn(0);
        TableColumn supervisorAssignmentIdColumn = facultyAdminView.getTblAssignmentSupervisors().getColumnModel().getColumn(0);
        facultyAdminView.getTblSupervisors().removeColumn(supervisorListIdColumn);
        facultyAdminView.getTblAssignmentSupervisors().removeColumn(supervisorAssignmentIdColumn);
    }

    private void loadSupervisors() {
        // Empty out the models
        supervisorListModel.setRowCount(0);
        supervisorAssignmentModel.setRowCount(0);

        // Fill up table models
        List<Supervisor> supervisors = supervisorRepo.findAll();
        for (Supervisor sup : supervisors) {
            int totalAssignedStudents = studentRepo.findBySupervisorId(sup.getId()).size();
            supervisorListModel.addRow(new Object[]{sup.getId(), sup.getName(), Integer.toString(totalAssignedStudents)});
            supervisorAssignmentModel.addRow(new Object[]{sup.getId(), sup.getName(), Integer.toString(totalAssignedStudents)});
        }
    }

    private void manageSupervisorList() {
        facultyAdminView.getTblSupervisors().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                displaySupervisorDetails();
            }
        });
    }

    private void displaySupervisorDetails() {
        // Get selected supervisor ID
        int row = facultyAdminView.getTblSupervisors().getSelectedRow();
        String supervisorId = String.valueOf(supervisorListModel.getValueAt(row, 0));

        // Find respective supervisor and their details
        Supervisor supervisor = supervisorRepo.findById(supervisorId).get();
        List<Student> students = studentRepo.findBySupervisorId(supervisorId);
        List<LocalDateTime> availableTimeslots = supervisor.getAvailableTimeslots();

        // Empty out supervisor's details' table models
        supervisorStudentsListModel.setRowCount(0);
        supervisorTimeslotsModel.setRowCount(0);

        // Fill up supervisor's student table
        for (Student stud : students) {
            String intake = stud.getIntake().toString();
            String program = stud.getProgram().toString();
            String dob = stud.getDob().format(dateFormatter);

            supervisorStudentsListModel.addRow(new Object[]{stud.getName(), intake, program, dob});
        }

        // Fill up supervisors available timeslots
        for (LocalDateTime timeslot : availableTimeslots) {
            String date = timeslot.format(dateFormatter);
            String time = timeslot.format(timeFormatter);

            supervisorTimeslotsModel.addRow(new Object[]{date, time});
        }
    }

    private void initializeSupervisorWorkloadReport() {
        // Define table column headers
        String[] columns = {
            "Supervisor Name",
            "Number of Students Assigned",
            "Total Appointments",
            "Pending Appointments",
            "Approved Appointments",
            "Remaining Timeslots"
        };

        // Initialize the table model with the defined columns and make it read-only
        supervisorWorkloadReportModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent editing cells directly in the table
            }
        };

        // For each supervisor, compute workload statistics and add a row to the report
        loadSupervisorWorkloadReport();

        // Attach the populated model to the table in the faculty admin view
        facultyAdminView.getTblSupervisorWorkloadReport().setModel(supervisorWorkloadReportModel);
    }

    private void loadSupervisorWorkloadReport() {
        // Empty the model
        supervisorWorkloadReportModel.setRowCount(0);

        // Retrieve all supervisors from the repository
        List<Supervisor> supervisors = supervisorRepo.findAll();

        // Fill up report model
        for (Supervisor supervisor : supervisors) {

            // Retrieve all appointments linked to this supervisor
            List<Appointment> appointments = appointmentRepo.findBySupervisorId(supervisor.getId());
            // Use a Set for faster lookup when checking booked timeslots
            Set<LocalDateTime> bookedTimeslots = new HashSet<>();

            int pendingCount = 0;
            int approvedCount = 0;

            // Count appointment statuses and collect booked timeslots
            for (Appointment appt : appointments) {
                switch (appt.getStatus()) {
                    case PENDING ->
                        pendingCount++;
                    case APPROVED ->
                        approvedCount++;
                    default -> {
                    }
                }
                bookedTimeslots.add(appt.getTimeslot());
            }

            // Count total students supervised by this supervisor
            int totalStudents = studentRepo.findBySupervisorId(supervisor.getId()).size();

            // Determine how many available timeslots remain unbooked
            int remainingSlots = countRemainingTimeslots(supervisor.getAvailableTimeslots(), bookedTimeslots);

            // Add a summarized row to the report model
            supervisorWorkloadReportModel.addRow(new Object[]{
                supervisor.getName(),
                totalStudents,
                appointments.size(),
                pendingCount,
                approvedCount,
                remainingSlots
            });
        }
    }

    private void initializeTimeslotUtilizationReport() {
        // Define table column headers
        String[] columns = {
            "Supervisor Name",
            "Total Timeslots",
            "Total Booked Timeslots",
            "Total Free Timeslots",
            "Timeslot Utilization"
        };

        // Initialize the table model with the defined columns and make it read-only
        timeslotUtilizationReportModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent editing cells directly in the table
            }
        };

        // For each supervisor, compute workload statistics and add a row to the report
        loadTimeslotUtilizationReport();

        // Attach the populated model to the table in the faculty admin view
        facultyAdminView.getTblTimeslotUtilizationReport().setModel(timeslotUtilizationReportModel);
    }

    private void loadTimeslotUtilizationReport() {
        // Empty the model
        timeslotUtilizationReportModel.setRowCount(0);

        // Retrieve all supervisors from the repository
        List<Supervisor> supervisors = supervisorRepo.findAll();

        // Fill up report model
        for (Supervisor supervisor : supervisors) {

            // Retrieve all appointments linked to this supervisor
            List<Appointment> appointments = appointmentRepo.findBySupervisorId(supervisor.getId());
            // Use a Set for faster lookup when checking booked timeslots
            Set<LocalDateTime> bookedTimeslots = new HashSet<>();

            // Collect booked timeslots
            for (Appointment appt : appointments) {
                bookedTimeslots.add(appt.getTimeslot());
            }

            // Count remaining available timeslots
            List<LocalDateTime> availableTimeslots = supervisor.getAvailableTimeslots();
            int remainingSlotsCount = countRemainingTimeslots(supervisor.getAvailableTimeslots(), bookedTimeslots);

            // Calculate utilization percentage
            int totalTimeslots = availableTimeslots.size();
            int booked = bookedTimeslots.size();
            String percentageDisplay = getTimeslotUtilizationPercentageDisplay(totalTimeslots, booked);

            // Add a summarized row to the report model
            timeslotUtilizationReportModel.addRow(new Object[]{
                supervisor.getName(),
                totalTimeslots,
                booked,
                remainingSlotsCount,
                percentageDisplay
            });
        }
    }

    private void initializeDistributionReport() {
        // --- Step 1: Define enums ---
        Intake[] intakes = Intake.values();
        Program[] programs = Program.values();

        // --- Step 2: Convert enums to string columns ---
        String[] intakeColumns = new String[intakes.length];
        for (int i = 0; i < intakes.length; i++) {
            intakeColumns[i] = intakes[i].toString();
        }

        String[] programColumns = new String[programs.length];
        for (int i = 0; i < programs.length; i++) {
            programColumns[i] = programs[i].toString();
        }

        // --- Step 3: Initialize all table models (read-only) ---
        DefaultTableModel studentCountByIntakeModel = new DefaultTableModel(intakeColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        DefaultTableModel studentCountByProgramModel = new DefaultTableModel(programColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        DefaultTableModel programCountsByIntakeModel = new DefaultTableModel(
                createProgramIntakeColumns(intakes),
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // --- Step 4: Prepare counting structures ---
        int[] totalIntakeCounts = new int[intakes.length];
        int[] totalProgramCounts = new int[programs.length];
        int[][] programIntakeCounts = new int[programs.length][intakes.length]; // [program][intake]

        // --- Step 5: Load students ---
        List<Student> students = studentRepo.findAll();

        // --- Step 6: Count totals ---
        for (Student stud : students) {
            Intake intake = stud.getIntake();
            Program program = stud.getProgram();

            if (intake == null || program == null) {
                System.out.println("Skipping invalid student record: " + stud.getId());
                continue;
            }

            int intakeIndex = intake.ordinal();
            int programIndex = program.ordinal();

            totalIntakeCounts[intakeIndex]++;
            totalProgramCounts[programIndex]++;
            programIntakeCounts[programIndex][intakeIndex]++;
        }

        // --- Step 7: Populate studentCountByIntakeModel ---
        Object[] intakeRowData = new Object[totalIntakeCounts.length];
        for (int i = 0; i < totalIntakeCounts.length; i++) {
            intakeRowData[i] = totalIntakeCounts[i];
        }
        studentCountByIntakeModel.addRow(intakeRowData);

        // --- Step 8: Populate studentCountByProgramModel ---
        Object[] programRowData = new Object[totalProgramCounts.length];
        for (int i = 0; i < totalProgramCounts.length; i++) {
            programRowData[i] = totalProgramCounts[i];
        }
        studentCountByProgramModel.addRow(programRowData);

        // --- Step 9: Populate programCountsByIntakeModel (main combined table) ---
        for (int i = 0; i < programs.length; i++) {
            Object[] row = new Object[intakes.length + 1];
            row[0] = programs[i].toString(); // first column = program name
            for (int j = 0; j < intakes.length; j++) {
                row[j + 1] = programIntakeCounts[i][j];
            }
            programCountsByIntakeModel.addRow(row);
        }

        // --- Step 10: Attach models to UI tables ---
        facultyAdminView.getTblReportStudentByIntake().setModel(studentCountByIntakeModel);
        facultyAdminView.getTblReportStudentsByProgram().setModel(studentCountByProgramModel);
        facultyAdminView.getTblReportProgramCountsByIntake().setModel(programCountsByIntakeModel);

        // --- Step 11: Set total student count respectively ---
        facultyAdminView.getTxtReportStudentCount().setText(Integer.toString(students.size()));
    }

    private int countRemainingTimeslots(List<LocalDateTime> available, Set<LocalDateTime> booked) {
        int count = 0;

        // Increment counter for each available timeslot not present in booked set
        for (LocalDateTime timeslot : available) {
            if (!booked.contains(timeslot)) {
                count++;
            }
        }

        return count;
    }

    private String getTimeslotUtilizationPercentageDisplay(int totalTimeslots, int booked) {
        double utilizationPercentage;

        if (totalTimeslots > 0) {
            utilizationPercentage = ((double) booked / totalTimeslots) * 100;
        } else {
            // Optional: define behavior when no timeslots exist
            utilizationPercentage = -1;
        }

        return (totalTimeslots > 0)
                ? String.format("%.1f%%", utilizationPercentage)
                : "N/A";
    }

    private String[] createProgramIntakeColumns(Intake[] intakes) {
        String[] columns = new String[intakes.length + 1];
        columns[0] = "Program";
        for (int i = 0; i < intakes.length; i++) {
            columns[i + 1] = intakes[i].toString();
        }
        return columns;
    }

    private void manageSignOut() {
        facultyAdminView.getBtnSignOut().addActionListener(e -> {
            // Switch to login frame
            JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(facultyAdminView);
            navigator.switchToLogin(mainFrame);
        });
    }
}
