package it.polito.ai.lab2.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TeamDTO {

    private int id;
    private String name;
    private int status;
}
