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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JList;
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
import java.time.Year;
import java.time.format.DateTimeParseException;

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
    private DefaultTableModel studentNamesModel;
    private DefaultTableModel feedbackLogModel;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private int selectedAppointmentRow = -1;
    private int selectedFeedbackRow = -1;
    private int selectedFeedbackLogRow = -1;
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
        loadFeedback();
        manageFeedback();
        loadTimeslots();
        initializeComboDay();
        initializeComboMonth();
        initializeComboYear();
        initializeComboMinute();
        initializeComboHour();
        manageTimeslots();
    }
    
    private void updateTimeslots(){
        String year = supervisorView.getComboYear().getSelectedItem().toString();
        int month = supervisorView.getComboMonth().getSelectedIndex();
        int day = supervisorView.getComboDay().getSelectedIndex();
        String hour = supervisorView.getComboHour().getSelectedItem().toString();
        String minute = supervisorView.getComboMinute().getSelectedItem().toString();  
        
        int yearAsInt;

        try {
            // Attempt to convert the String to an integer
            yearAsInt = Integer.parseInt(year);
            
            } catch (NumberFormatException e) {
                 // This block is executed if the conversion fails
                 System.err.println("Error: The selected item '" + year + "' is not a valid number.");
    
                // Handle the error, such as setting a default value or showing a message to the user
                    yearAsInt = -1; // Example: set a default error value
                }
        
        if(month == 1 && day >28 && yearAsInt % 4 == 0){
            JOptionPane.showMessageDialog(supervisorView, "Invalid Day");
            return;
        }
        if(month == 1 && day >27 && yearAsInt % 4 != 0){
            JOptionPane.showMessageDialog(supervisorView, "Invalid Day");
            return;
        }
        String date;
        if (month < 9 && day < 9){
            date = year + "-0" + (month+1) + "-0" + (day+1)+ "T";
        }else if(month < 9){
            date = year + "-0" + (month+1) + "-" + (day+1) + "T";
        }else if(day < 9){
            date = year + "-" + (month+1) + "-0" + (day+1) + "T";
        }else{
            date = year + "-" + (month+1) + "-" + (day+1) + "T";
        }
        
        String time = hour + ':' + minute;
        
        String dateTime = date + time;
        LocalDateTime localDateTime = null;
        
        try {
            localDateTime = LocalDateTime.parse(dateTime);
        }catch (DateTimeParseException e) {
            System.err.println("Error parsing date/time string: " + e.getMessage());
        }   
        
        Supervisor sup = supervisorRepo.findById(supervisorId).get();
        sup.addTimeslot(localDateTime);
        supervisorRepo.save();

        loadTimeslots();
    }
    
    private void deleteTimeslots(){
        selectedTimeslotRow = supervisorView.getTblTimeslot().getSelectedRow();
        
        String date = String.valueOf((String) timeslotModel.getValueAt(selectedTimeslotRow, 0));
        String time = String.valueOf((String) timeslotModel.getValueAt(selectedTimeslotRow, 1));
        Year currentYear = Year.now();
        
        int year_current = currentYear.getValue();
        String yearStr = String.valueOf(year_current);
        
        String[] dateParts = date.split("/");
        String day = dateParts[0];
        String month = dateParts[1];
        String year = dateParts[2];
        
        if(yearStr.contains(year)){
            year = yearStr;
        }else{
            return;
        }
        
        String dateTime = year + "-" + month + "-" + day + "T" + time;
        
        LocalDateTime localDateTime = null;
        
        try {
            localDateTime = LocalDateTime.parse(dateTime);
        }catch (DateTimeParseException e) {
            System.err.println("Error parsing date/time string: " + e.getMessage());
        }   
        
        Supervisor sup = supervisorRepo.findById(supervisorId).get();
        sup.removeTimeslot(localDateTime);
        supervisorRepo.save();

        loadTimeslots();
        
    }
    private void manageTimeslots(){
       
        supervisorView.getBtnCreateTimeslot().addActionListener(e -> updateTimeslots());
        supervisorView.getBtnDeleteTimeslot().addActionListener(e -> deleteTimeslots());
    }
    private void loadTimeslots(){
        List<LocalDateTime> timeslots = supervisorRepo.findById(supervisorId).map(sup -> sup.getAvailableTimeslots()).get();
        timeslotModel.setRowCount(0);
        for(LocalDateTime timeslot : timeslots){
            LocalDateTime dateTime = LocalDateTime.parse(timeslot.toString());
            String date = dateTime.toLocalDate().format(dateFormatter);
            String time = dateTime.toLocalTime().format(timeFormatter);
            
            timeslotModel.addRow(new Object[]{date, time}); 
        }
    }
    private void initializeComboDay(){
        DefaultComboBoxModel<Object> modelDay = new DefaultComboBoxModel<>();
  

        for(int day = 1; day < 32; day++){
            modelDay.addElement(day);
        }
        supervisorView.getComboDay().setModel(modelDay);
    }
    
    private void initializeComboMonth(){
        
        DefaultComboBoxModel<Object> modelMonth = new DefaultComboBoxModel<>();
        
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        
        
        
        for(String month : months){
            modelMonth.addElement(month);
        }       
        supervisorView.getComboMonth().setModel(modelMonth);
    }
    
    private void initializeComboYear(){
        
        DefaultComboBoxModel<Object> modelYear = new DefaultComboBoxModel<>();
        supervisorView.getComboYear().removeAllItems();
        Year currentYear = Year.now();
        
        int year = currentYear.getValue();
        modelYear.addElement(year);
        modelYear.addElement(year + 1);

        
        supervisorView.getComboYear().setModel(modelYear);
    }
    
    private void initializeComboHour(){
        
        DefaultComboBoxModel<Object> modelHour = new DefaultComboBoxModel<>();
        
        for(int hour = 0; hour < 24; hour++){
            String minutes;
            if(hour < 10){
                minutes = '0'+ Integer.toString(hour);
                System.out.println(minutes);
                modelHour.addElement(minutes);
            }else{
                minutes = Integer.toString(hour);
                modelHour.addElement(minutes);
            }
        }
        
        supervisorView.getComboHour().setModel(modelHour);
    }
    
    private void initializeComboMinute(){
        
        DefaultComboBoxModel<Object> modelMinute = new DefaultComboBoxModel<>();
        
        for(int minute = 0; minute < 60; minute++){
            String minutes;
            if(minute < 10){
                minutes = '0'+ Integer.toString(minute);
                modelMinute.addElement(minutes);
            }else{
                minutes = Integer.toString(minute);
                modelMinute.addElement(minutes);
            }
        }
        supervisorView.getComboMinute().setModel(modelMinute);
    }

    private void loadFeedback(){
        List<Feedback> feedbacks = feedbackRepo.findBySupervisorId(supervisorId);
        
        feedbackLogModel.setRowCount(0);
        
        for(Feedback feedback : feedbacks){

            String studentName = studentRepo.findById(feedback.getStudentId())
                    .map(stu -> stu.getName())
                    .orElse("Unknown Student");
            
            String StudentFeedback = feedback.getFeedback();
            String createdDate = feedback.getCreatedAt().format(dateFormatter);
            
            feedbackLogModel.addRow(new Object[]{feedback.getId(), studentName, StudentFeedback, createdDate});

        }
    }
    private void manageFeedback(){
        supervisorView.getBtnAddFeedback().setEnabled(false);
        supervisorView.getBtnDeleteFeedback().setEnabled(false);
        
        supervisorView.getTblStudentNames().addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseReleased(java.awt.event.MouseEvent evt){
                supervisorView.getBtnAddFeedback().setEnabled(true);
            }
        });
        
        supervisorView.getTblFeedbackLog().addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseReleased(java.awt.event.MouseEvent evt){
                supervisorView.getBtnDeleteFeedback().setEnabled(true);
            }
        });
        
        supervisorView.getBtnAddFeedback().addActionListener(e -> updateFeedbackLog());
        supervisorView.getBtnDeleteFeedback().addActionListener(e -> deleteFeedbackLog());
    }
    private void updateFeedbackLog(){
        selectedFeedbackRow = supervisorView.getTblStudentNames().getSelectedRow();
        
        if(selectedFeedbackRow == -1){
            displayMessage("Please select a valid row.", "Invalid Action");
            return;
        }
        
        String studentId = String.valueOf(studentNamesModel.getValueAt(selectedFeedbackRow, 0));
        String feedback = supervisorView.getTxtFeedback().getText();
        
        Feedback fdbk = new Feedback(studentId, supervisorId, feedback);
        feedbackRepo.add(fdbk);
        loadFeedback();
        supervisorView.getBtnAddFeedback().setEnabled(false);
        selectedFeedbackRow = -1;

    }
    private void deleteFeedbackLog(){
        selectedFeedbackLogRow = supervisorView.getTblFeedbackLog().getSelectedRow();
        
        String feedbackId = String.valueOf(feedbackLogModel.getValueAt(selectedFeedbackLogRow, 0));
        
        if(selectedFeedbackLogRow == -1){
            displayMessage("Please select a valid row.", "Invalid Action");
            return;
        }

        feedbackRepo.remove(feedbackId);
        loadFeedback();
        supervisorView.getBtnDeleteFeedback().setEnabled(false);
        selectedFeedbackRow = -1;
    }
    private void loadStudents(){
        List<Student> students = studentRepo.findAll();
                
        studentModel.setRowCount(0);
        
        for (Student student : students){   
            List<Appointment> appointments = appointmentRepo.findBySupervisorId(supervisorId);
            
            String studentId = student.getId();
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
                studentNamesModel.addRow(new Object[]{studentId, studentName});

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
        
        String[] studentNamesColumns = {"ID", "Student Names"};
        this.studentNamesModel = new DefaultTableModel(studentNamesColumns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        String[] feedbackLogColumns = {"ID", "Student Name", "Feedback", "Created Date"};
        this.feedbackLogModel = new DefaultTableModel(feedbackLogColumns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        String[] timeslotColumns = {"Date", "Time"};
        this.timeslotModel = new DefaultTableModel(timeslotColumns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        
        
        JTable studentTable = supervisorView.getTblStudents();
        studentTable.setModel(studentModel);
        
        JTable studentNamesTable = supervisorView.getTblStudentNames();
        studentNamesTable.setModel(studentNamesModel);
        
        JTable feedbackLogTable = supervisorView.getTblFeedbackLog();
        feedbackLogTable.setModel(feedbackLogModel);
        

        JTable appointmentWidgetTable = supervisorView.getTblAppointmentsWidget();
        appointmentWidgetTable.setModel(appointmentWidgetModel);

        JTable appointmentTable = supervisorView.getTblAppointmentRequests();
        appointmentTable.setModel(appointmentModel);
        
        JTable timeslotTable = supervisorView.getTblTimeslot();
        timeslotTable.setModel(timeslotModel);

        // hide the first column (ID)
        TableColumn idApptColumn = supervisorView.getTblAppointmentRequests().getColumnModel().getColumn(0);
        supervisorView.getTblAppointmentRequests().removeColumn(idApptColumn);
        
        TableColumn idFeedStuColumn = supervisorView.getTblStudentNames().getColumnModel().getColumn(0);
        supervisorView.getTblStudentNames().removeColumn(idFeedStuColumn);
        
        TableColumn idFeedLogColumn = supervisorView.getTblFeedbackLog().getColumnModel().getColumn(0);
        supervisorView.getTblFeedbackLog().removeColumn(idFeedLogColumn);

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
