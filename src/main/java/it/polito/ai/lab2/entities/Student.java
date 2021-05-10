package it.polito.ai.lab2.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
public class Student {

    @Id
    private String id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String email;

    private String photoPath;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="student_course", joinColumns = @JoinColumn(name="student"),
            inverseJoinColumns = @JoinColumn(name="course_name"))
    private List<Course> courses;
    {
        courses = new ArrayList<>();
    }

    @ManyToMany(mappedBy = "members")
    private List<Team> teams;
    {
        teams = new ArrayList<>();
    }

    @OneToMany(mappedBy = "student")
    private List<Paper> papers;
    {
        papers = new ArrayList<>();
    }

    @OneToMany(mappedBy = "creator")
    private List<Vm> createdVms;
    {
        createdVms = new ArrayList<>();
    }

    @OneToMany(mappedBy = "creator")
    private List<Team> createdTeams;
    {
        createdTeams = new ArrayList<>();
    }

    public int addCourse (Course course) {
        courses.add(course);
        course.getStudents().add(this);
        return courses.indexOf(course);
    }

    @ManyToMany(mappedBy = "owners")
    private List<Vm> vms;
    {
        vms = new ArrayList<>();
    }

    @OneToOne(mappedBy = "student")
    @JoinColumn(name = "user_id")
    private User user;

    public int addVm(Vm vm) {
        vms.add(vm);
        vm.getOwners().add(this);
        return vms.indexOf(vm);
    }

    public int addTeam (Team team) {
        teams.add(team);
        team.getMembers().add(this);
        return teams.indexOf(team);
    }
}
