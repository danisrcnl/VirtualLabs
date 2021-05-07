package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.Course;
import it.polito.ai.lab2.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course,String> {

    @Query("SELECT s " +
            "FROM Student s " +
            "INNER JOIN s.teams t " +
            "INNER JOIN t.course c " +
            "WHERE c.name=:courseName " +
            "AND t.status=1")
    public List<Student> getStudentsInTeams(String courseName);

    @Query("SELECT s " +
            "FROM Student s " +
            "WHERE s NOT IN " +
                "(SELECT s " +
                "FROM Student s " +
                "INNER JOIN s.teams t " +
                "INNER JOIN t.course c " +
                "WHERE c.name=:courseName " +
                "AND t.status=1)")
    public List<Student> getStudentsNotInTeams(String courseName);

    @Query("SELECT s FROM Student s INNER JOIN s.papers p INNER JOIN p.assignment a WHERE a.id =:assignmentId")
    public List<Student> getStudentsWithNoPaperFor(Long assignmentId);

}
