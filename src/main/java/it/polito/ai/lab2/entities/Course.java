package it.polito.ai.lab2.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
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

    private int min;

    private int max;

    private Boolean enabled;

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

    @OneToMany(mappedBy = "course")
    private List<Team> teams;
    {
        teams = new ArrayList<>();
    }


    public int addStudent (Student student) {
        students.add(student);
        student.getCourses().add(this);
        return students.indexOf(student);
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
}
