package it.polito.ai.lab2.dtos;

import it.polito.ai.lab2.dataStructures.PaperStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PaperDTO extends RepresentationModel<PaperDTO> {

    private Long id;
    private String content;
    private String creator;
    private PaperStatus currentStatus;
    private int mark;
    private boolean editable;

}
