package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.entities.Vm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VmRepository extends JpaRepository<Vm, Long> {

    @Query("SELECT v FROM Vm v WHERE v.team.course.name=:courseName")
    List<VmDTO> getVmsForCourse(String courseName);

}
