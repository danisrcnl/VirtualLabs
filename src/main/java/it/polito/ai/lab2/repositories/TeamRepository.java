package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {

    @Query("SELECT t.id FROM Team t WHERE t.course.name=:courseName AND t.name=:teamName")
    int getIdByCourseAndName(String courseName, String teamName);
    @Query("SELECT t FROM Team t WHERE t.course.name=:courseName AND t.name=:teamName")
    Team getTeamByCourseAndName(String courseName, String teamName);
}
