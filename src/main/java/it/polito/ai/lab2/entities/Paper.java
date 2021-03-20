package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.dataStructures.PaperStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

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
    private Long id;

    private String content;

    private PaperStatus currentStatus;

    private int mark;

    private boolean editable;

    @ManyToOne
    @JoinColumn(name="student")
    private Student student;

    @ManyToOne
    @JoinColumn(name="assignment")
    private Assignment assignment;

    @OneToMany(mappedBy = "paper")
    private List<PaperStatusTime> statusHistory;

    public int addStatusHistory(PaperStatusTime paperStatusTime) {
        statusHistory.add(paperStatusTime);
        paperStatusTime.setPaper(this);
        return statusHistory.indexOf(paperStatusTime);
    }

}
