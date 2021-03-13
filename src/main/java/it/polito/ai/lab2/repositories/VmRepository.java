package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.entities.Vm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VmRepository extends JpaRepository<Vm, Long> {

    @Query("SELECT v FROM Vm v WHERE v.team.course=:courseName")
    List<VmDTO> getVmsForCourse(String courseName);

}
