package it.polito.ai.lab2.dtos;

import it.polito.ai.lab2.dataStructures.VmStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VmDTO extends RepresentationModel<VmDTO> {
    private Long id;
    private int nVCpu;
    private int disk;
    private int ram;
    private VmStatus vmStatus;
}
