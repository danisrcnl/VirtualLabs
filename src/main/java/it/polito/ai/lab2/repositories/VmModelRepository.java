package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.VmModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VmModelRepository extends JpaRepository<VmModel, Long> {
}
