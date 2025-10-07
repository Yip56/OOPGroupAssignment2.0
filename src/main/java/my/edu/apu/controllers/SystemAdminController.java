/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.controllers;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import my.edu.apu.enums.Role;
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

    // Set up date formatters
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy"); // e.g. 05/10/25
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");    // e.g. 14:30

    public SystemAdminController(SystemAdminView systemAdminView, AppNavigator navigator, UserRepository userRepo, StudentRepository studentRepo, SupervisorRepository supervisorRepo, AppointmentRepository appointmentRepo, FeedbackRepository feedbackRepo, String systemAdminId) {
        this.systemAdminView = systemAdminView;
        this.systemAdminId = systemAdminId;
        this.navigator = navigator;
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.supervisorRepo = supervisorRepo;
        this.appointmentRepo = appointmentRepo;
        this.feedbackRepo = feedbackRepo;
        
        initializeSystemAdminView();
    }
    
    private void initializeSystemAdminView() {
        initializeDashboard();
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
}
