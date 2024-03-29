package it.polito.ai.lab2.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    @NotEmpty
    private String name;

    private int status;


    @ManyToOne
    @JoinColumn(name="course_id")
    private Course course;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="team_student", joinColumns = @JoinColumn(name="team_id"),
            inverseJoinColumns = @JoinColumn(name="student_id"))
    private List<Student> members;
    {
        members = new ArrayList<>();
    }

    @OneToMany(mappedBy = "team")
    private List<Vm> vms;
    {
        vms = new ArrayList<>();
    }

    @ManyToOne
    @JoinColumn(name="team_creator")
    private Student creator;

    public void setCourse(Course course) {
        if(this.course == null){
            course.getTeams().add(this);
            this.course = course;
            return;
        }
        if(course == null)
            this.course.getTeams().remove(this);
        else {
            this.course.getTeams().remove(this);
            course.getTeams().add(this);
        }
        this.course = course;
    }

    public int addMember(Student student) {
        members.add(student);
        student.getTeams().add(this);
        return members.indexOf(student);
    }

    public int addvm(Vm vm) {
        vms.add(vm);
        vm.setTeam(this);
        return vms.indexOf(vm);
    }
}
