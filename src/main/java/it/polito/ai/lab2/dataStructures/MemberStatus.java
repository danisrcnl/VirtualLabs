package it.polito.ai.lab2.dataStructures;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MemberStatus {
    private String studentId;
    private Boolean isCreator;
    private Boolean hasAccepted;
}
