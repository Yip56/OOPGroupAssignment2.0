/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import my.edu.apu.enums.Role;
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

    // Set up date formatters
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy"); // e.g. 05/10/25
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");    // e.g. 14:30

    // Table models
    private DefaultTableModel userAccountModel;

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
        initializeDateAndTime();
        initializeDashboard();
        initializeUsers();
        manageUserAccounts();
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
        String[] userAccountModelColumns = {"ID", "Name", "Email", "Role"};
        userAccountModel = new DefaultTableModel(userAccountModelColumns, 0);

        loadUsers();

        // Attach model to table
        systemAdminView.getTblUserAccounts().setModel(userAccountModel);

        // Hide the id column
        TableColumn idColumn = systemAdminView.getTblUserAccounts().getColumnModel().getColumn(0);
        systemAdminView.getTblUserAccounts().removeColumn(idColumn);
    }

    private void loadUsers() {
        // Empty model
        userAccountModel.setRowCount(0);

        // Fill up table model with data
        List<User> users = userRepo.findAll();
        for (User u : users) {
            if (u.getRole().equals(Role.SYSTEM_ADMIN)) {
                continue;
            }
            userAccountModel.addRow(new Object[]{u.getId(), u.getName(), u.getUniEmail(), u.getRole().toString()});
        }
    }

    private void manageUserAccounts() {
        // Initially set both buttons as disabled
        systemAdminView.getBtnUpdateUserDetails().setEnabled(false);
        systemAdminView.getBtnDeleteUser().setEnabled(false);

        systemAdminView.getTblUserAccounts().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                prefillUserAccountDetails();
                systemAdminView.getBtnUpdateUserDetails().setEnabled(true);
                systemAdminView.getBtnDeleteUser().setEnabled(true);
            }
        });

        systemAdminView.getTxtUsernameSearch().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                searchByName();
            }
        });

        systemAdminView.getTxtEmailSearch().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                searchByEmail();
            }
        });

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
    }

    private void resetUserAccountDetails() {
        // Reset text fields and buttons
        systemAdminView.getTxtName().setText("");
        systemAdminView.getTxtUserEmail().setText("");
        systemAdminView.getBtnUpdateUserDetails().setEnabled(false);
        systemAdminView.getBtnDeleteUser().setEnabled(false);
    }

    private void deleteUserAccount() {
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

        // Update table model with new user list
        loadUsers();

        // Reset text fields and buttons
        resetUserAccountDetails();
    }

    private void updateUserDetails() {
        // Get the selected row from the model
        int row = systemAdminView.getTblUserAccounts().getSelectedRow();
        String userId = String.valueOf(userAccountModel.getValueAt(row, 0));

        String updatedName = systemAdminView.getTxtName().getText();
        String updatedEmail = systemAdminView.getTxtUserEmail().getText();

        User currentUser = userRepo.findById(userId).get();

        // If the updated email is not equal to the same existing email and a user with the same email is found
        if (!updatedEmail.equalsIgnoreCase(currentUser.getUniEmail()) && userRepo.findByUniEmail(updatedEmail).isPresent()) {
            JOptionPane.showMessageDialog(systemAdminView, "Please choose a unique email.");
            return;
        }

        // Update user details
        User updatedUser = new User(updatedName, updatedEmail, currentUser.getPassword(), currentUser.getRole(), userId);
        userRepo.update(updatedUser);

        // Update table model with new user list
        loadUsers();

        // Reset text fields and buttons
        resetUserAccountDetails();
    }

    private void searchByName() {
        // Filter users by name
        List<User> users = userRepo.findAll();
        String name = systemAdminView.getTxtUsernameSearch().getText();
        List<User> filteredUsers = new ArrayList<>();

        for (User u : users) {
            if (u.getRole().equals(Role.SYSTEM_ADMIN)) {
                continue;
            }
            if (u.getName().toLowerCase().contains(name.toLowerCase())) {
                filteredUsers.add(u);
            }
        }

        // Update table model
        userAccountModel.setRowCount(0);
        for (User u : filteredUsers) {
            userAccountModel.addRow(new Object[]{u.getId(), u.getName(), u.getUniEmail(), u.getRole().toString()});
        }
    }

    private void searchByEmail() {
        // Filter users by email
        List<User> users = userRepo.findAll();
        String email = systemAdminView.getTxtEmailSearch().getText();
        List<User> filteredUsers = new ArrayList<>();

        for (User u : users) {
            if (u.getRole().equals(Role.SYSTEM_ADMIN)) {
                continue;
            }
            if (u.getUniEmail().toLowerCase().contains(email.toLowerCase())) {
                filteredUsers.add(u);
            }
        }

        // Update table model
        userAccountModel.setRowCount(0);
        for (User u : filteredUsers) {
            userAccountModel.addRow(new Object[]{u.getId(), u.getName(), u.getUniEmail(), u.getRole().toString()});
        }
    }

    private void resetSearch() {
        systemAdminView.getTxtEmailSearch().setText("");
        systemAdminView.getTxtUsernameSearch().setText("");
        loadUsers();
    }
}
