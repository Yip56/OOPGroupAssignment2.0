/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.controllers;

import my.edu.apu.enums.AppointmentStatus;
import my.edu.apu.views.panels.StudentView;
import my.edu.apu.repositories.*;
import my.edu.apu.models.Student;
import my.edu.apu.models.Supervisor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import javax.swing.SwingUtilities;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import my.edu.apu.models.Appointment;
import my.edu.apu.models.Feedback;
import my.edu.apu.utils.AppNavigator;
import my.edu.apu.views.panels.SupervisorView;
import my.edu.apu.repositories.AppointmentRepository;
import my.edu.apu.repositories.StudentRepository;

/**
 *
 * @author CS Yip
 */
public class SupervisorViewController {

    private String studentId;
    private final String supervisorId;
    private final SupervisorView supervisorView;
    private final AppNavigator navigator;
    private final StudentRepository studentRepo;
    private final SupervisorRepository supervisorRepo;
    private final AppointmentRepository appointmentRepo;
    private final FeedbackRepository feedbackRepo;
    private DefaultTableModel appointmentWidgetModel;
    private DefaultTableModel appointmentModel;
    private DefaultTableModel timeslotModel;
    private DefaultTableModel feedbackModel;
    private DefaultTableModel studentModel;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private int selectedAppointmentRow = -1;
    private int selectedTimeslotRow = -1;

    public SupervisorViewController(SupervisorView supervisorView, AppNavigator navigator, StudentRepository studentRepo, SupervisorRepository supervisorRepo, AppointmentRepository appointmentRepo, FeedbackRepository feedbackRepo, String supervisorId) {
        this.supervisorView = supervisorView;
        this.navigator = navigator;
        this.studentRepo = studentRepo;
        this.supervisorRepo = supervisorRepo;
        this.appointmentRepo = appointmentRepo;
        this.feedbackRepo = feedbackRepo;
        this.supervisorId = supervisorId;

        intializeSupervisorView();
    }

    private void intializeSupervisorView() {
        initializeAppointments();
        loadAppointments();
        manageAppointmentRequests();
        loadStudents();

    }
    private void loadStudents(){
        List<Student> students = studentRepo.findAll();
                
        studentModel.setRowCount(0);
        
        for (Student student : students){   
            List<Appointment> appointments = appointmentRepo.findBySupervisorId(supervisorId);
            
            String studentName = student.getName();
            String intake = student.getIntake().toString();
            String program = student.getProgram().toString();
            int numberOfAppointments = 0;

            for (Appointment appt : appointments) {
                String apptStudentName = studentRepo.findById(appt.getStudentId())
                    .map(stu -> stu.getName())
                    .orElse("Unknown Student");
                if(apptStudentName.equals(studentName)){
                    numberOfAppointments++;   
                }
            }
            if(numberOfAppointments != 0){
                studentModel.addRow(new Object[]{studentName, intake, program, numberOfAppointments}); 
            }

        }
        
    }

    private void loadAppointments() {
        List<Appointment> appointments = appointmentRepo.findBySupervisorId(supervisorId);

        appointmentWidgetModel.setRowCount(0);
        appointmentModel.setRowCount(0);

        for (Appointment appt : appointments) {
            String studentName = studentRepo.findById(appt.getStudentId())
                    .map(stu -> stu.getName())
                    .orElse("Unknown Student");

            String date = appt.getTimeslot().format(dateFormatter);
            String time = appt.getTimeslot().format(timeFormatter);
            String status = appt.getStatus().toString();

            appointmentWidgetModel.addRow(new Object[]{studentName, date});
            appointmentModel.addRow(new Object[]{appt.getId(), studentName, date, time, status});

        }
    }

    private void initializeAppointments() {
        String[] appointmentWidgetColumns = {"Student", "Date"};
        this.appointmentWidgetModel = new DefaultTableModel(appointmentWidgetColumns, 0);

        String[] appointmentColumns = {"ID", "Student Name", "Date", "Time", "Status"};
        this.appointmentModel = new DefaultTableModel(appointmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        String[] studentColumns = {"Student Name", "Intake", "Program", "Number of Appointments"};
        this.studentModel = new DefaultTableModel(studentColumns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        JTable studentTable = supervisorView.getTblStudents();
        studentTable.setModel(studentModel);
        

        JTable appointmentWidgetTable = supervisorView.getTblAppointmentsWidget();
        appointmentWidgetTable.setModel(appointmentWidgetModel);

        JTable appointmentTable = supervisorView.getTblAppointmentRequests();
        appointmentTable.setModel(appointmentModel);

        // hide the first column (ID)
        TableColumn idColumn = supervisorView.getTblAppointmentRequests().getColumnModel().getColumn(0);
        supervisorView.getTblAppointmentRequests().removeColumn(idColumn);

    }

    private void manageAppointmentRequests() {
        // Initially set both buttons as disabled
        supervisorView.getBtnApproveAppointments().setEnabled(false);
        supervisorView.getBtnSetAppointmentAsPending().setEnabled(false);
        supervisorView.getBtnRejectAppointments().setEnabled(false);

        // When an appointment is selected, turn on "cancel appointment" button
        supervisorView.getTblAppointmentRequests().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                supervisorView.getBtnApproveAppointments().setEnabled(true);
                supervisorView.getBtnSetAppointmentAsPending().setEnabled(true);
                supervisorView.getBtnRejectAppointments().setEnabled(true);
            }
        });

        // Add event listeners for creating/canceling appointments
        supervisorView.getBtnApproveAppointments().addActionListener(e -> updateAppointmentStatus(AppointmentStatus.APPROVED));
        supervisorView.getBtnSetAppointmentAsPending().addActionListener(e -> updateAppointmentStatus(AppointmentStatus.PENDING));
        supervisorView.getBtnRejectAppointments().addActionListener(e -> updateAppointmentStatus(AppointmentStatus.REJECTED));
        supervisorView.getBtnDeleteAllRejectedAppointments().addActionListener(e -> deleteAllRejectedAppointments());
    }

    public void updateAppointmentStatus(AppointmentStatus status) {
        selectedAppointmentRow = supervisorView.getTblAppointmentRequests().getSelectedRow();

        // Ensure a row is properly selected
        if (selectedAppointmentRow == -1) {
            displayMessage("Please select a valid timeslot to book an appointment.", "Invalid Action");
            return;
        }

        String appointmentId = String.valueOf(appointmentModel.getValueAt(selectedAppointmentRow, 0));
        Appointment appointment = appointmentRepo.findById(appointmentId).get();
        LocalDateTime timeslot = appointment.getTimeslot();

        Appointment appt = new Appointment(appointment.getId(), appointment.getStudentId(), supervisorId, timeslot);
        appt.setStatus(status);

        appointmentRepo.update(appt);
        loadAppointments();
        supervisorView.getBtnApproveAppointments().setEnabled(false);
        supervisorView.getBtnSetAppointmentAsPending().setEnabled(false);
        supervisorView.getBtnRejectAppointments().setEnabled(false);
        selectedAppointmentRow = -1;
    }

    public void deleteAllRejectedAppointments() {
        List<Appointment> appointments = appointmentRepo.findBySupervisorId(supervisorId);

        appointmentWidgetModel.setRowCount(0);
        appointmentModel.setRowCount(0);

        for (Appointment appt : appointments) {

            if (appt.getStatus().equals(AppointmentStatus.REJECTED)) {
                appointmentRepo.remove(appt.getId());
            }
        }
        loadAppointments();
    }

    private void displayMessage(String message, String title) {
        JOptionPane.showMessageDialog(supervisorView, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
