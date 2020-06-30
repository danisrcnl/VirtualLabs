package it.polito.ai.lab2.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TeacherDTO {

    private String id;
    private String firstName;
    private String name;
    private String email;
    private String photoPath;
}
