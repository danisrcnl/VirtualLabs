package it.polito.ai.lab2.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    @OneToMany(mappedBy = "course")
    private List<Team> teams;
    {
        teams = new ArrayList<>();
    }

    @OneToMany(mappedBy = "course")
    private List<Assignment> assignments;
    {
        assignments = new ArrayList<>();
    }
/*
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vmModel")
    private VmModel vmModel;
*/

    @OneToOne(cascade = CascadeType.ALL)
    private VmModel vmModel;

/*
    public void setVmModel(VmModel vmModel) {
        if(vmModel == null)
            this.vmModel = null;
        else {
            vmModel.setCourse(this);
            this.vmModel = vmModel;
        }
    }
*/
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

    public int addAssignment (Assignment assignment) {
        assignments.add(assignment);
        assignment.setCourse(this);
        return assignments.indexOf(assignment);
    }
}
