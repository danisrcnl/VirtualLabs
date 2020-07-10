package it.polito.ai.lab2.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private String creator;

    private String content;

    private Timestamp creationDate;

    private Timestamp expiryDate;

    @ManyToOne
    @JoinColumn(name="course_id")
    private Course course;

    @OneToMany(mappedBy = "assignment")
    private List<Paper> papers;
    {
        papers = new ArrayList<>();
    }


    public void setCourse(Course course) {
        if(this.course == null){
            course.getAssignments().add(this);
            this.course = course;
            return;
        }
        if(course == null)
            this.course.getAssignments().remove(this);
        else {
            this.course.getAssignments().remove(this);
            course.getAssignments().add(this);
        }
        this.course = course;
    }

}
