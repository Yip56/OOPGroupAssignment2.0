package my.edu.apu.interfaces;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */


import my.edu.apu.enums.AppointmentStatus;
import java.time.LocalDateTime;

/**
 *
 * @author pakdad
 */
public interface IAppointment {

    // Getters
    String getId();
    
    String getStudentId();

    String getSupervisorId();

    LocalDateTime getTimeslot();

    AppointmentStatus getStatus();

    // Setters
    void setTimeslot(LocalDateTime timeslot);

    void setStatus(AppointmentStatus status);
}
