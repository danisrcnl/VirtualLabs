package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.dataStructures.PaperStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
public class Paper {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private String content;

    private PaperStatus currentStatus;

    private int mark;

    @ManyToOne
    @JoinColumn("team_id")
    private Team team;

    @ManyToOne
    @JoinColumn("assignment_id")
    private Assignment assignment;

    @OneToMany(mappedBy = "paper")
    private List<PaperStatusTime> statusHistory;

}
