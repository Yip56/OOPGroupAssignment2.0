/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package my.edu.apu.interfaces;

import java.util.List;
import java.time.LocalDateTime;

/**
 *
 * @author pakdad
 */
public interface ISupervisor extends IUser {

    List<LocalDateTime> getAvailableTimeslots();

    void addTimeslot(LocalDateTime timeslot);

    void removeTimeslot(LocalDateTime timeslot);
}
