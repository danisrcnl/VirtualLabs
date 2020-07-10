package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.Vm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VmRepository extends JpaRepository<Vm, String> {
}
