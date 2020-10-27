package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaperRepository extends JpaRepository<Paper, Long> {

    @Query("SELECT p FROM Paper p WHERE p.team=:teamId AND p.assignment=:assignmentId")
    public Optional<Paper> getForTeamAndAssignment(Long teamId, Long assignmentId);
}
