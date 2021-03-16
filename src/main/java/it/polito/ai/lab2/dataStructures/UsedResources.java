package it.polito.ai.lab2.dataStructures;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsedResources {
    private int VCpu;
    private int Ram;
    private int Disk;
}
