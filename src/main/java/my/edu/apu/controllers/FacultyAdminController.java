/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import my.edu.apu.enums.Intake;
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
    private final FeedbackRepository feedbackRepo;

    // Set up date formatters
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy"); // e.g. 05/10/25
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");    // e.g. 14:30

    // Set up table models
    private DefaultTableModel studentModel;
    private DefaultTableModel supervisorListModel;
    private DefaultTableModel supervisorStudentsListModel;
    private DefaultTableModel supervisorTimeslotsModel;

    public FacultyAdminController(FacultyAdminView facultyAdminView, AppNavigator navigator, UserRepository userRepo, StudentRepository studentRepo, SupervisorRepository supervisorRepo, AppointmentRepository appointmentRepo, FeedbackRepository feedbackRepo, String facultyAdminId) {
        this.facultyAdminView = facultyAdminView;
        this.facultyAdminId = facultyAdminId;
        this.navigator = navigator;
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.supervisorRepo = supervisorRepo;
        this.appointmentRepo = appointmentRepo;
        this.feedbackRepo = feedbackRepo;

        initializeFacultyAdminView();
    }

    private void initializeFacultyAdminView() {
        initializeDashboard();
        initializeStudents();
        manageStudentSearch();
        initializeSupervisors();
        manageSupervisorList();
    }

    private void initializeDashboard() {
        // Calculate respective totals
        int totalStudents = studentRepo.findAll().size();
        int totalSupervisors = supervisorRepo.findAll().size();
        List<Student> students = studentRepo.findAll();
        int totalUnassignedStudents = 0;
        for (Student student : students) {
            if (student.getSupervisorId().isEmpty()) {
                totalUnassignedStudents++;
            }
        }

        // Set totals respectively
        facultyAdminView.getTxtTotalStudents().setText(Integer.toString(totalStudents));
        facultyAdminView.getTxtTotalSupervisors().setText(Integer.toString(totalSupervisors));
        facultyAdminView.getTxtUnassignedStudents().setText(Integer.toString(totalUnassignedStudents));
    }

    private void initializeStudents() {
        // Create table model
        String[] columns = {"Student Name", "Sup. Name", "Intake", "Program", "DOB"};
        studentModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ensure no cells are editable
            }
        };

        // Fill up model with data
        loadStudents(false);

        // Attach model to table
        facultyAdminView.getTblStudents().setModel(studentModel);
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
        // Initialize supervisor list table model
        String[] supervisorColumns = {"ID", "Supervisor Name", "# of Assigned Students"};
        supervisorListModel = new DefaultTableModel(supervisorColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ensure cells are not editable
            }
        };

        // Fill up table model
        List<Supervisor> supervisors = supervisorRepo.findAll();
        for (Supervisor sup : supervisors) {
            int totalAssignedStudents = studentRepo.findBySupervisorId(sup.getId()).size();
            supervisorListModel.addRow(new Object[]{sup.getId(), sup.getName(), Integer.toString(totalAssignedStudents)});
        }

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
        facultyAdminView.getTblSupervisorStudents().setModel(supervisorStudentsListModel);
        facultyAdminView.getTblSupervisorTimeslots().setModel(supervisorTimeslotsModel);

        // hide the first column (ID) of the supervisor list table
        TableColumn idColumn = facultyAdminView.getTblSupervisors().getColumnModel().getColumn(0);
        facultyAdminView.getTblSupervisors().removeColumn(idColumn);
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
}
