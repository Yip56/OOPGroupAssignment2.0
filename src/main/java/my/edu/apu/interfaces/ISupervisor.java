/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package my.edu.apu.interfaces;

import java.util.List;
import java.time.LocalDate;

/**
 *
 * @author pakdad
 */
public interface ISupervisor extends IUser {

    // Getters
    List<String> getStudentIds();

    List<LocalDate> getAvailableTimeslots();

    // Other
    void addStudentId(String studentId);

    void removeStudentId(String studentId);

    void addTimeslot(LocalDate timeslot);

    void removeTimeslot(LocalDate timeslot);
}
