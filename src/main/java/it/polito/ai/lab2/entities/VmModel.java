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
    private Long id;

    private String operatingSystem;

    private int maxNVCpu;   // max nvcpu a team can allocate

    private int maxDisk;    // max disk a team can allocate

    private int maxRam;     // max ram a team can allocate

    private int maxVmsForCourse;

    private int maxActiveVms;

    /*
    @OneToOne(mappedBy = "vmModel")
    @JoinColumn(name = "course")
    private Course course;
*/
    @OneToOne(mappedBy = "vmModel")
    @JoinColumn(name = "course_name")
    private Course course;


}
