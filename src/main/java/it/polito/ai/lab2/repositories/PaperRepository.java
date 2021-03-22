package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {

    @Query("SELECT p FROM Paper p WHERE p.student.id=:studentId AND p.assignment=:assignmentId")
    public Optional<Paper> getForStudentAndAssignment(String studentId, Long assignmentId);
}
