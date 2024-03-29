package it.polito.ai.lab2.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
public class Token {

    @Id
    private String id;

    private String studentId;

    private Long userId;

    private int teamId;

    private Timestamp expiryDate;

    private Boolean isTeam;
}
