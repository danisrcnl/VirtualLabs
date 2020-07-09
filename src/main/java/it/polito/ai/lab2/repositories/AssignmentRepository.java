package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, String> {
}
