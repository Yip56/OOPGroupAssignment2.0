package my.edu.apu.repositories;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import my.edu.apu.interfaces.IRepository;
import my.edu.apu.models.FailedLoginAttempt;

public class FailedLoginAttemptRepository implements IRepository<FailedLoginAttempt> {

    private final List<FailedLoginAttempt> failedLoginAttempts = new ArrayList<>();
    private final Path filePath;

    public FailedLoginAttemptRepository(String fileName) {
        this.filePath = Paths.get(fileName);
        load();
    }

    @Override
    public Optional<FailedLoginAttempt> findById(String id) {
        for (FailedLoginAttempt attempt : failedLoginAttempts) {
            if (attempt.getId().equals(id)) {
                return Optional.of(attempt);
            }
        }

        return Optional.empty();
    }

    @Override
    public List<FailedLoginAttempt> findAll() {
        return new ArrayList<>(failedLoginAttempts);
    }

    @Override
    public void add(FailedLoginAttempt failedLoginAttempt) {
        failedLoginAttempts.add(failedLoginAttempt);
        save();
    }

    @Override
    public void remove(String id) {
        for (int i = 0; i < failedLoginAttempts.size(); i++) {
            if (failedLoginAttempts.get(i).getId().equals(id)) {
                failedLoginAttempts.remove(i);
                break;
            }
        }

        save();
    }

    @Override
    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (FailedLoginAttempt attempt : failedLoginAttempts) {

                // Write to file
                writer.write(
                        attempt.getId() + "|"
                        + attempt.getUniEmail() + "|"
                        + attempt.getReason() + "|"
                        + attempt.getTimestamp().toString()
                );
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save failed login attempts", e);
        }
    }

    private void load() {
        if (!Files.exists(filePath)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            failedLoginAttempts.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 1) {
                    continue;
                }

                // Get values
                String id = parts[0];
                String uniEmail = parts[1];
                String reason = parts[2];
                LocalDate timestamp = LocalDate.parse(parts[3]);

                // Add to list
                FailedLoginAttempt failedLoginAttempt = new FailedLoginAttempt(uniEmail, reason, timestamp, id);
                failedLoginAttempts.add(failedLoginAttempt);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load failed login attempts.", e);
        }
    }
}
