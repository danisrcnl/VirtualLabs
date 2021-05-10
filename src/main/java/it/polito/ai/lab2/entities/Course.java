package it.polito.ai.lab2.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Course {

    @Id
    private String name;
    @NotNull
    private int min;
    @NotNull
    private int max;

    private Boolean enabled;
    @NotNull
    private String acronym;

    @ManyToMany(mappedBy = "courses")
    private List<Student> students;
    {
        students = new ArrayList<>();
    }

    @ManyToMany(mappedBy = "courses")
    private List<Teacher> teachers;
    {
        teachers = new ArrayList<>();
    }

    @OneToMany(mappedBy = "course", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private List<Team> teams;
    {
        teams = new ArrayList<>();
    }

    @OneToMany(mappedBy = "course", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private List<Assignment> assignments;
    {
        assignments = new ArrayList<>();
    }

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(cascade = CascadeType.REMOVE)
    private VmModel vmModel;

    public int addStudent (Student student) {
        students.add(student);
        student.getCourses().add(this);
        return students.indexOf(student);
    }

    public void removeRelations () {
        for (Student s : students) {
            if(s.getUser() != null)
                s.getUser().getRoles().removeIf(r -> r.contains(this.getName()));
            s.getCourses().remove(this);
        }
        for (Teacher t : teachers) {
            if(t.getUser() != null)
                t.getUser().getRoles().removeIf(r -> r.contains(this.getName()));
            t.getCourses().remove(this);
        }
    }

    public int addTeacher (Teacher teacher) {
        teachers.add(teacher);
        teacher.getCourses().add(this);
        return teachers.indexOf(teacher);
    }

    public int addTeam (Team team) {
        teams.add(team);
        team.setCourse(this);
        return teams.indexOf(team);
    }

    public int addAssignment (Assignment assignment) {
        assignments.add(assignment);
        assignment.setCourse(this);
        return assignments.indexOf(assignment);
    }
}
