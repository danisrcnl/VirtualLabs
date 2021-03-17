package it.polito.ai.lab2.dataStructures;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamRequest {
    private String teamName;
    private List<String> memberIds;
    private int hours;
}
