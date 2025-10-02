package my.edu.apu.models;

import java.time.LocalDate;
import my.edu.apu.enums.Intake;
import my.edu.apu.enums.Program;
import my.edu.apu.enums.Role;
import my.edu.apu.interfaces.IStudent;

public class Student extends AbstractUser implements IStudent {

    private LocalDate dob;
    private String supervisorId;
    private Intake intake;
    private Program program;

    // ✅ Minimal constructor (for creating new students at runtime)
    public Student(String name, String uniEmail, String password) {
        super(name, uniEmail, password, Role.STUDENT);
    }

    // ✅ Full constructor (used for restoring from file / repo)
    public Student(String name, String uniEmail, String password, String id,
            LocalDate dob, String supervisorId, Intake intake, Program program) {
        super(name, uniEmail, password, Role.STUDENT, id);
        this.dob = dob;
        this.supervisorId = supervisorId;
        this.intake = intake;
        this.program = program;
    }

    @Override
    public LocalDate getDob() {
        return dob;
    }

    @Override
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    @Override
    public String getSupervisorId() {
        return supervisorId;
    }

    @Override
    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

    @Override
    public Intake getIntake() {
        return intake;
    }

    @Override
    public void setIntake(Intake intake) {
        this.intake = intake;
    }

    @Override
    public Program getProgram() {
        return program;
    }

    @Override
    public void setProgram(Program program) {
        this.program = program;
    }
}
