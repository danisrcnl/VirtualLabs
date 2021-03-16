package it.polito.ai.lab2.dataStructures;

import it.polito.ai.lab2.dtos.CourseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseWithTeacher {

    private CourseDTO courseDTO;
    private String teacherId;
}
