package it.polito.ai.lab2.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TeamDTO  extends RepresentationModel<TeamDTO> {

    private int id;
    private String name;
    private int status;
    private String creator;
}
