package my.edu.apu.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import my.edu.apu.enums.Role;
import my.edu.apu.interfaces.ISupervisor;

public class Supervisor extends AbstractUser implements ISupervisor {

    private List<LocalDateTime> timeslots;

// Normal constructor (for creating new supervisors)
    public Supervisor(String name, String uniEmail, String password) {
        super(name, uniEmail, password, Role.SUPERVISOR);
        this.timeslots = new ArrayList<>();
    }

// Constructor with id + timeslots (for restoring from file)
    public Supervisor(String name, String uniEmail, String password, String id, List<LocalDateTime> timeslots, boolean status) {
        super(name, uniEmail, password, Role.SUPERVISOR, id, status);
        this.timeslots = new ArrayList<>(timeslots); // make a copy to protect internal state
    }

    @Override
    public List<LocalDateTime> getAvailableTimeslots() {
        return new ArrayList<>(this.timeslots); // return a copy to avoid accidental external changes
    }

    @Override
    public void addTimeslot(LocalDateTime timeslot) {
        this.timeslots.add(timeslot);
    }

    @Override
    public void removeTimeslot(LocalDateTime timeslot) {
        this.timeslots.remove(timeslot);
    }

// (Optional) For repository convenience
    public void setTimeslots(List<LocalDateTime> timeslots) {
        this.timeslots = new ArrayList<>(timeslots);
    }

}
