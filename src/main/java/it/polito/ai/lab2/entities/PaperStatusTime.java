package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.dataStructures.PaperStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
public class PaperStatusTime {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private Timestamp timestamp;

    private PaperStatus paperStatus;

    @ManyToOne
    @JoinColumn("paperStatusTime_id")
    private Paper paper;

}
