package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.dataStructures.VmStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
public class Vm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    private int nVCpu;
    @NotNull
    private int disk;
    @NotNull
    private int ram;

    private VmStatus currentStatus;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="vm_owner", joinColumns = @JoinColumn(name="vm"),
            inverseJoinColumns = @JoinColumn(name="owner"))
    private List<Student> owners;
    {
        owners = new ArrayList<>();
    }

    @ManyToOne
    @JoinColumn(name="team")
    private Team team;

    @ManyToOne
    @JoinColumn(name="vm_creator")
    private Student creator;

    public void removeRelations () {
        for (Student s : owners)
          s.getVms().remove(this);
        team.getVms().remove(this);
        creator.getCreatedVms().remove(this);
    }

    public int addOwner(Student student) {
        owners.add(student);
        student.getVms().add(this);
        return owners.indexOf(student);
    }

}
