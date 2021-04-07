package it.polito.ai.lab2.dataStructures;

import it.polito.ai.lab2.dtos.VmDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VmSubmission {
    private VmDTO vmDTO;
    private String creator;
}
