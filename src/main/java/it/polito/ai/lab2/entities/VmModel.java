package it.polito.ai.lab2.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
public class VmModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private int nVCpu;

    private int disk;

    private int ram;

    @OneToOne(mappedBy = "vmModel")
    private Vm vm;

}
