/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.controllers;

import java.time.format.DateTimeFormatter;
import java.util.List;
import my.edu.apu.models.Student;
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
}
