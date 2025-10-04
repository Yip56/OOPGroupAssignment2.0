package my.edu.apu.repositories;

import my.edu.apu.interfaces.IRepository;
import my.edu.apu.models.Feedback;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class FeedbackRepository implements IRepository<Feedback> {

    private final List<Feedback> feedbackList = new ArrayList<>();
    private final Path filePath;

    public FeedbackRepository(String fileName) {
        this.filePath = Paths.get(fileName);
        load(); // Load feedback from file on startup
    }

    @Override
    public List<Feedback> findAll() {
        return new ArrayList<>(feedbackList); // return copy to avoid external modification
    }

    @Override
    public Optional<Feedback> findById(String id) {
        for (Feedback fb : feedbackList) {
            if (fb.getId().equals(id)) {
                return Optional.of(fb);
            }
        }
        return Optional.empty();
    }

    @Override
    public void add(Feedback feedback) {
        feedbackList.add(feedback);
        save();
    }

    @Override
    public void remove(String id) {
        for (int i = 0; i < feedbackList.size(); i++) {
            if (feedbackList.get(i).getId().equals(id)) {
                feedbackList.remove(i);
                break;
            }
        }
        save();
    }

    @Override
    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Feedback fb : feedbackList) {
                writer.write(fb.getId() + "|"
                        + fb.getStudentId() + "|"
                        + fb.getSupervisorId() + "|"
                        + fb.getFeedback().replace("|", " ") + "|"
                        + // sanitize so feedback text doesn’t break format
                        fb.getCreatedAt().toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save feedback", e);
        }
    }

    private void load() {
        if (!Files.exists(filePath)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            feedbackList.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length != 5) {
                    continue; // must match expected format
                }
                String id = parts[0];
                String studentId = parts[1];
                String supervisorId = parts[2];
                String feedbackText = parts[3];
                LocalDate createdAt = LocalDate.parse(parts[4]);

                Feedback fb = new Feedback(id, studentId, supervisorId, feedbackText, createdAt);
                feedbackList.add(fb);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load feedback", e);
        }
    }
}
