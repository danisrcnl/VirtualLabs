package it.polito.ai.lab2.dtos;

import it.polito.ai.lab2.dataStructures.PaperStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PaperStatusTimeDTO extends RepresentationModel<PaperStatusTimeDTO> {

    private String id;
    private Timestamp timestamp;
    private PaperStatus paperStatus;
    private String content;

}
