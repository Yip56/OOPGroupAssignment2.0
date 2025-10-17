package my.edu.apu.repositories;

import my.edu.apu.interfaces.IRepository;
import my.edu.apu.models.Supervisor;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

public class SupervisorRepository implements IRepository<Supervisor> {

    private final List<Supervisor> supervisors = new ArrayList<>();
    private final Path filePath;
    private final UserRepository userRepo;

    public SupervisorRepository(String fileName, UserRepository userRepo) {
        this.filePath = Paths.get(fileName);
        this.userRepo = userRepo;
        load();
    }

    @Override
    public List<Supervisor> findAll() {
        return new ArrayList<>(supervisors);
    }

    @Override
    public Optional<Supervisor> findById(String id) {
        for (Supervisor sup : supervisors) {
            if (sup.getId().equals(id)) {
                return Optional.of(sup);
            }
        }
        return Optional.empty();
    }

    @Override
    public void add(Supervisor supervisor) {
        supervisors.add(supervisor);
        save();
    }

    @Override
    public void remove(String id) {
        for (int i = 0; i < supervisors.size(); i++) {
            if (supervisors.get(i).getId().equals(id)) {
                supervisors.remove(i);
                break;
            }
        }
        userRepo.remove(id); // also remove from users.txt
        save();
    }

    @Override
    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Supervisor sup : supervisors) {
                String timeslotsStr = String.join(",",
                        sup.getAvailableTimeslots().stream()
                                .map(LocalDateTime::toString)
                                .toArray(String[]::new));

                writer.write(sup.getId() + "|" + timeslotsStr);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save supervisors", e);
        }
    }

    private void load() {
        if (!Files.exists(filePath)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            supervisors.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 1) {
                    continue;
                }

                String id = parts[0];
                List<LocalDateTime> timeslots = new ArrayList<>();

                if (parts.length > 1 && !parts[1].isEmpty()) {
                    String[] slots = parts[1].split(",");
                    for (String slot : slots) {
                        timeslots.add(LocalDateTime.parse(slot));
                    }
                }

                // get user info from userRepo
                userRepo.findById(id).ifPresent(user -> {
                    Supervisor sup = new Supervisor(
                            user.getName(),
                            user.getUniEmail(),
                            user.getPassword(),
                            user.getId(),
                            timeslots,
                            user.getAccountStatus()
                    );
                    supervisors.add(sup);
                });
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load supervisors", e);
        }
    }

}
