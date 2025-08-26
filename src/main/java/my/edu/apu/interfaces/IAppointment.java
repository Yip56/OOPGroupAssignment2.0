/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package my.edu.apu.interfaces;

import my.edu.apu.enums.AppointmentStatus;
import java.time.LocalDate;

/**
 *
 * @author pakdad
 */
public interface IAppointment {

    // Getters
    String getStudentId();

    String getSupervisorId();

    LocalDate getTimeslot();

    AppointmentStatus getStatus();

    // Setters
    void setTimeslot(LocalDate timeslot);

    void setStatus(AppointmentStatus status);
}
