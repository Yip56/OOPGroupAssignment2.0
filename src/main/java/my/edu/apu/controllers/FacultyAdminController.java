/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.controllers;

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

    private DefaultTableModel studentModel;

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
        String[] columns = {"ID", "Student Name", "Sup. Name", "Intake", "Program", "DOB"};
        studentModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ensure no cells are editable
            }
        };

        // Fill up model with data
        loadStudents(false);

        // Attach model to timeslot
        facultyAdminView.getTblStudents().setModel(studentModel);

        // only hide the first column (Feedback ID)
        TableColumn idColumn = facultyAdminView.getTblStudents().getColumnModel().getColumn(0);
        facultyAdminView.getTblStudents().removeColumn(idColumn);
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
            studentModel.addRow(new Object[]{stud.getId(), studentName, supName, intake, program, dob});
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
}
