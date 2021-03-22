package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.PaperStatusTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaperStatusTimeRepository extends JpaRepository<PaperStatusTime, String> {
}
