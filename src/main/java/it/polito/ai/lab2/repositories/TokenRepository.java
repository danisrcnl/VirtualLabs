package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository <Token,String> {

    @Query("SELECT tk FROM Token tk WHERE tk.expiryDate>:t")
    List<Token> findAllByExpiryBefore(Timestamp t);
    @Query("SELECT tk FROM Token tk WHERE (tk.teamId=:teamId)")
    List<Token> findAllByTeamId(int teamId);
    @Query("SELECT tk FROM Token tk WHERE tk.studentId=:studentId")
    List<Token> findAllByStudentId(String studentId);
    @Query("SELECT tk FROM Token tk WHERE tk.isTeam=:false")
    List<Token> findAllNotTeam ();
    @Query("SELECT tk FROM Token tk WHERE tk.isTeam=:false AND tk.userId=:id")
    Optional<Token> findUserToken (String id);

}
