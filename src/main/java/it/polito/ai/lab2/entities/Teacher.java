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
@Entity
@Data
public class Teacher {

    @Id
    private String id;

    private String firstName;

    private String name;

    private String email;

    private String photoPath;

    @ManyToMany(cascade = {/*CascadeType.PERSIST, CascadeType.MERGE*/ CascadeType.ALL})
    @JoinTable(name="teacher_course", joinColumns = @JoinColumn(name="teacher_id"),
            inverseJoinColumns = @JoinColumn(name="course_name"))
    private List<Course> courses;
    {
        courses = new ArrayList<>();
    }

    @OneToOne(mappedBy = "teacher")
    @JoinColumn(name = "user_id")
    private User user;

    public int addCourse (Course course) {
        courses.add(course);
        course.getTeachers().add(this);
        return courses.indexOf(course);
    }
}
