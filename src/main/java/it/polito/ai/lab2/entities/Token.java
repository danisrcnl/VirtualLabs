package it.polito.ai.lab2.entities;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Builder
@Entity
@Data
public class Token {

    @Id
    private String id;

    private Long teamId;

    private Timestamp expiryDate;
}
