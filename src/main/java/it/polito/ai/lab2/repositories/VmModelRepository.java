package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.VmModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VmModelRepository extends JpaRepository<VmModel, Long> {

    @Query("SELECT v FROM VmModel v WHERE v.course.name=:courseName")
    VmModel getVmModelByCourse(String courseName);
}
