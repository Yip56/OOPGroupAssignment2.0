package my.edu.apu.repositories;

import my.edu.apu.interfaces.IRepository;
import my.edu.apu.models.Appointment;
import my.edu.apu.enums.AppointmentStatus;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

public class AppointmentRepository implements IRepository<Appointment> {

    private final List<Appointment> appointments = new ArrayList<>();
    private final Path filePath;

    public AppointmentRepository(String fileName) {
        this.filePath = Paths.get(fileName);
        load();
    }

    @Override
    public List<Appointment> findAll() {
        return new ArrayList<>(appointments);
    }

    @Override
    public Optional<Appointment> findById(String id) {
        return appointments.stream()
                .filter(app -> app.getId().equals(id))
                .findFirst();
    }

    public List<Appointment> findByStudentId(String studentId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment a : appointments) {
            if (a.getStudentId().equals(studentId)) {
                result.add(a);
            }
        }
        return result;
    }

    public List<Appointment> findBySupervisorId(String supervisorId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment a : appointments) {
            if (a.getSupervisorId().equals(supervisorId)) {
                result.add(a);
            }
        }
        return result;
    }

    @Override
    public void add(Appointment appointment) {
        appointments.add(appointment);
        save();
    }

    @Override
    public void remove(String id) {
        appointments.removeIf(app -> app.getId().equals(id));
        save();
    }

    public void update(Appointment updatedAppointment) {
        for (int i = 0; i < appointments.size(); i++) {
            Appointment current = appointments.get(i);
            if (current.getId().equals(updatedAppointment.getId())) {
                appointments.set(i, updatedAppointment);
                save();
                return;
            }
        }
        throw new NoSuchElementException("Appointment with ID" + updatedAppointment.getId() + " not found");
    }

    @Override
    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Appointment app : appointments) {
                writer.write(app.getId() + "|"
                        + app.getStudentId() + "|"
                        + app.getSupervisorId() + "|"
                        + app.getTimeslot() + "|"
                        + app.getStatus().name());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save appointments", e);
        }
    }

    private void load() {
        if (!Files.exists(filePath)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            appointments.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 5) {
                    continue;
                }

                String id = parts[0];
                String studentId = parts[1];
                String supervisorId = parts[2];
                LocalDateTime timeslot = LocalDateTime.parse(parts[3]);
                AppointmentStatus status = AppointmentStatus.valueOf(parts[4]);

                Appointment app = new Appointment(id, studentId, supervisorId, timeslot);
                app.setStatus(status); // override default PENDING if restored
                appointments.add(app);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load appointments", e);
        }
    }
}
