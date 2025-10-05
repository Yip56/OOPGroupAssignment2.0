package my.edu.apu.repositories;

import my.edu.apu.enums.Intake;
import my.edu.apu.enums.Program;
import my.edu.apu.interfaces.IRepository;
import my.edu.apu.models.Student;
import my.edu.apu.models.User;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class StudentRepository implements IRepository<Student> {

    private final List<Student> students = new ArrayList<>();
    private final Path filePath;
    private final UserRepository userRepo; // link to users

    public StudentRepository(String fileName, UserRepository userRepo) {
        this.filePath = Paths.get(fileName);
        this.userRepo = userRepo;
        load();
    }

    @Override
    public List<Student> findAll() {
        return new ArrayList<>(students);
    }

    @Override
    public Optional<Student> findById(String id) {
        for (Student student : students) {
            if (student.getId().equals(id)) {
                return Optional.of(student);
            }
        }
        return Optional.empty();
    }

    @Override
    public void add(Student student) {
        students.add(student);
        save();
    }

    @Override
    public void remove(String id) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId().equals(id)) {
                students.remove(i);
                break;
            }
        }
        userRepo.remove(id);
        save();
    }

    @Override
    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Student student : students) {
                writer.write(student.getId() + "|"
                        + student.getDob() + "|"
                        + student.getSupervisorId() + "|"
                        + student.getIntake().name() + "|"
                        + student.getProgram().name());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save students", e);
        }
    }

    private void load() {
        if (!Files.exists(filePath)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            students.clear();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length != 5) {
                    continue; // skip malformed
                }

                String id = parts[0];
                LocalDate dob = LocalDate.parse(parts[1]);
                String supervisorId = parts[2];
                Intake intake = Intake.valueOf(parts[3]);
                Program program = Program.valueOf(parts[4]);

                // 🔑 fetch user base info first
                Optional<User> baseUserOpt = userRepo.findById(id);
                if (baseUserOpt.isEmpty()) {
                    continue; // student without user? skip
                }
                User baseUser = baseUserOpt.get();

                // build student
                Student student = new Student(
                        baseUser.getName(),
                        baseUser.getUniEmail(),
                        baseUser.getPassword(),
                        id,
                        dob,
                        supervisorId,
                        intake,
                        program
                );
                students.add(student);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load students", e);
        }
    }
}
