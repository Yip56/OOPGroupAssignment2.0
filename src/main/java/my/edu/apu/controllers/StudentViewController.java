/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.controllers;

import my.edu.apu.views.panels.StudentView;
import my.edu.apu.repositories.*;
import my.edu.apu.models.Student;
import my.edu.apu.models.Supervisor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import my.edu.apu.models.Appointment;
import my.edu.apu.models.Feedback;

/**
 *
 * @author pakdad
 */
public class StudentViewController {

    private final String studentId;
    private final String supervisorId;
    private final StudentView studentView;
    private final StudentRepository studentRepo;
    private final SupervisorRepository supervisorRepo;
    private final AppointmentRepository appointmentRepo;
    private final FeedbackRepository feedbackRepo;
    private DefaultTableModel appointmentModel;
    private DefaultTableModel timeslotModel;
    private DefaultTableModel feedbackModel;

    // Set up date formatters
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy"); // e.g. 05/10/25
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");    // e.g. 14:30

    // Use to keep track of selected rows
    private int selectedAppointmentRow = -1;
    private int selectedTimeslotRow = -1;

    public StudentViewController(StudentView studentView, StudentRepository studentRepo, SupervisorRepository supervisorRepo, AppointmentRepository appointmentRepo, FeedbackRepository feedbackRepo, String studentId) {
        this.studentId = studentId;
        this.studentView = studentView;
        this.studentRepo = studentRepo;
        this.supervisorRepo = supervisorRepo;
        this.appointmentRepo = appointmentRepo;
        this.feedbackRepo = feedbackRepo;
        this.supervisorId = studentRepo.findById(studentId).get().getSupervisorId();

        intializeStudentView();
    }

    private void intializeStudentView() {
        initializeDashboard();
        loadAppointments();
        loadFeedbacks();
        initializeTimeslots();
        manageAppointments();
        manageFeedbackDisplay();
    }

    private void loadAppointments() {
        // Create a model and column names for appointment widget table
        String[] appointmentWidgetColumns = {"Supervisor", "Date"};
        DefaultTableModel appointmentWidgetModel = new DefaultTableModel(appointmentWidgetColumns, 0);

        // Create a model and column names for actual appointment table
        String[] appointmentColumns = {"ID", "Supervisor Name", "Date", "Time", "Status"};
        this.appointmentModel = new DefaultTableModel(appointmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // makes ALL cells uneditable
            }
        };

        // Assuming you already have appointmentRepo and supervisorRepo available
        List<Appointment> appointments = appointmentRepo.findByStudentId(studentId);

        // Clear tables before adding rows
        appointmentWidgetModel.setRowCount(0);
        appointmentModel.setRowCount(0);

        // Fill tables with respective rows
        for (Appointment appt : appointments) {
            String supervisorName = supervisorRepo.findById(appt.getSupervisorId())
                    .map(sup -> sup.getName())
                    .orElse("Unknown Supervisor");

            String date = appt.getTimeslot().format(dateFormatter);
            String time = appt.getTimeslot().format(timeFormatter);
            String status = appt.getStatus().toString();

            appointmentWidgetModel.addRow(new Object[]{supervisorName, date});
            appointmentModel.addRow(new Object[]{appt.getId(), supervisorName, date, time, status});
        }

        // Connect models to tables respectively
        JTable appointmentWidgetTable = studentView.getTblAppointmentsWidget();
        appointmentWidgetTable.setModel(appointmentWidgetModel);

        JTable appointmentTable = studentView.getTblAppointments();
        appointmentTable.setModel(appointmentModel);

        // hide the first column (ID)
        TableColumn idColumn = studentView.getTblAppointments().getColumnModel().getColumn(0);
        studentView.getTblAppointments().removeColumn(idColumn);
    }

    private void manageAppointments() {
        // Initially set both buttons as disabled
        studentView.getBtnMakeAppointment().setEnabled(false);
        studentView.getBtnCancelAppointment().setEnabled(false);

        // When an appointment is selected, turn on "cancel appointment" button
        studentView.getTblAppointments().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                studentView.getBtnCancelAppointment().setEnabled(true);
            }
        });

        // When an timeslot is selected, turn on "make appointment" button
        studentView.getTblTimeslots().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                studentView.getBtnMakeAppointment().setEnabled(true);
            }
        });

        // Add event listeners for creating/canceling appointments
        studentView.getBtnMakeAppointment().addActionListener(e -> makeAppointment());
        studentView.getBtnCancelAppointment().addActionListener(e -> cancelAppointment());
    }

    private void makeAppointment() {
        selectedTimeslotRow = studentView.getTblTimeslots().getSelectedRow();

        // Ensure a row is properly selected
        if (selectedTimeslotRow == -1) {
            displayMessage("Please select a valid timeslot to book an appointment.", "Invalid Action");
            return;
        }

        // Get and parse the model value for the timeslot
        String modelValue = String.valueOf(timeslotModel.getValueAt(selectedTimeslotRow, 0));
        LocalDateTime timeslot = LocalDateTime.parse(modelValue);

        // Create and add a new appointment to the repo
        Appointment appt = new Appointment(studentId, supervisorId, timeslot);
        appointmentRepo.add(appt);

        // Create a new row to add to the appointment model
        String supervisorName = supervisorRepo.findById(supervisorId).get().getName();
        String date = timeslot.format(dateFormatter);
        String time = timeslot.format(timeFormatter);
        String status = appt.getStatus().toString();

        appointmentModel.addRow(new Object[]{appt.getId(), supervisorName, date, time, status});

        // Reload timeslots
        reloadTimeslots();

        // Reset selected row
        selectedTimeslotRow = -1;
        studentView.getBtnMakeAppointment().setEnabled(false);
    }

    private void cancelAppointment() {
        selectedAppointmentRow = studentView.getTblAppointments().getSelectedRow();

        // Ensure a row is properly selected
        if (selectedAppointmentRow == -1) {
            displayMessage("Please select a valid appointment to cancel.", "Invalid Action");
            return;
        }

        // Remove appointment from repository
        String apptId = String.valueOf(appointmentModel.getValueAt(selectedAppointmentRow, 0));
        appointmentRepo.remove(apptId);

        // Remove appointment from model
        appointmentModel.removeRow(selectedAppointmentRow);

        // Reload timeslots
        reloadTimeslots();

        // Reset selected row
        selectedAppointmentRow = -1;
        studentView.getBtnCancelAppointment().setEnabled(false);
    }

    private void displayMessage(String message, String title) {
        JOptionPane.showMessageDialog(studentView, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void initializeTimeslots() {
        // Initialize the timeslot model
        String[] timeslotColumns = {"Full Date", "Date", "Time"};
        this.timeslotModel = new DefaultTableModel(timeslotColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // makes ALL cells uneditable
            }
        };

        // Load in the data
        reloadTimeslots();

        // Attach model to timeslot
        studentView.getTblTimeslots().setModel(timeslotModel);

        // only hide the first column (Full Date)
        TableColumn idColumn = studentView.getTblTimeslots().getColumnModel().getColumn(0);
        studentView.getTblTimeslots().removeColumn(idColumn);
    }

    private void reloadTimeslots() {
        // Empty the model before adding rows
        timeslotModel.setRowCount(0);

        // Collect all of the student's existing appointments' timeslots
        List<Appointment> appointments = appointmentRepo.findByStudentId(studentId);
        List<LocalDateTime> bookedTimeslots = new ArrayList<>();
        for (Appointment appointment : appointments) {
            bookedTimeslots.add(appointment.getTimeslot());
        }

        // Fill up model
        Supervisor sup = supervisorRepo.findById(supervisorId).get();
        for (LocalDateTime timeslot : sup.getAvailableTimeslots()) {

            // If a timeslot is already booked, skip it
            if (bookedTimeslots.contains(timeslot)) {
                continue;
            }

            String date = timeslot.format(dateFormatter);
            String time = timeslot.format(timeFormatter);

            timeslotModel.addRow(new Object[]{timeslot.toString(), date, time});
        }
    }

    private void loadFeedbacks() {
        // Create table model
        String[] columnNames = {"Feedback ID", "Supervisor Name", "Feedback", "Created At"};
        this.feedbackModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // makes ALL cells uneditable
            }
        };

        // Populate feedback table
        List<Feedback> feedbacks = feedbackRepo.findByStudentId(studentId);
        for (Feedback fb : feedbacks) {
            String id = fb.getId();
            String supervisorName = supervisorRepo.findById(fb.getSupervisorId()).get().getName();
            String feedbackText = fb.getFeedback();
            String createdAt = fb.getCreatedAt().format(dateFormatter);

            feedbackModel.addRow(new Object[]{id, supervisorName, feedbackText, createdAt});
        }

        // Attach model to timeslot
        studentView.getTblFeedbacks().setModel(feedbackModel);

        // only hide the first column (Feedback ID)
        TableColumn idColumn = studentView.getTblFeedbacks().getColumnModel().getColumn(0);
        studentView.getTblFeedbacks().removeColumn(idColumn);
    }

    public void manageFeedbackDisplay() {
        // When a feedback is selected, display it
        studentView.getTblFeedbacks().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                displayFeedback();
            }
        });
    }

    private void displayFeedback() {
        // Get selected feedback
        int row = studentView.getTblFeedbacks().getSelectedRow();
        String id = String.valueOf(feedbackModel.getValueAt(row, 0));
        Feedback feedback = feedbackRepo.findById(id).get();

        studentView.getTxtAreaFeedbackDisplay().setText(feedback.getFeedback());
    }

    private void initializeDashboard() {
        // Find the student from the student repository
        Student student = studentRepo.findById(studentId).get();
        String name = student.getName();

        // Set the student's username in the studentView
        studentView.getTxtUsername().setText(name);

        // Find the student's supervisor
        Supervisor supervisor = supervisorRepo.findById(supervisorId).get();
        studentView.getFieldSupervisor().setText(supervisor.getName());

        // Set the date once (doesn't need live updating)
        studentView.getTxtDate().setText(LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM, yyyy")));

        // Set the time initially
        studentView.getTxtTime().setText(LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a")));

        // Update the time every second
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    studentView.getTxtTime().setText(LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a")));
                });
            }
        }, 0, 1000);
    }
}
