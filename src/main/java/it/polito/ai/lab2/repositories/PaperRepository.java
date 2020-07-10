package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.Paper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperRepository extends JpaRepository<Paper, String> {
}
