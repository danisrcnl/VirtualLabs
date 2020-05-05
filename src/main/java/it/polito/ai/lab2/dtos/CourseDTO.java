package it.polito.ai.lab2.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CourseDTO extends RepresentationModel<CourseDTO> {

    private String name;
    private int min;
    private int max;
    private Boolean enabled;
}
