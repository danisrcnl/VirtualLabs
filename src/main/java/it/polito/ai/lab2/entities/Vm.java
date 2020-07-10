package it.polito.ai.lab2.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
    private String id;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="vm_owner", joinColumns = @JoinColumn(name="vm_id"),
            inverseJoinColumns = @JoinColumn(name="owner_id"))
    private List<Student> owners;
    {
        owners = new ArrayList<>();
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vmModel_id", referencedColumnName = "id")
    private VmModel vmModel;

}
