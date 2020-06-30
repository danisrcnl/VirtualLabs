package it.polito.ai.lab2.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
public class Team {

    @GeneratedValue
    @Id
    private Long id;

    @Column(unique = true)
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
        /*
        student.getTeams().add(this);
        */
        student.setTeam(this);
        return members.indexOf(student);
    }
}
