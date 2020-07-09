package it.polito.ai.lab2.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AssignmentDTO {

    private String id;
    private String creator;
    private String content;
    private Timestamp creationDate;
    private Timestamp expiryDate;
}
