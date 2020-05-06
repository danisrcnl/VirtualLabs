package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.security.Timestamp;
import java.util.List;

public interface TokenRepository extends JpaRepository <Token,String> {

    @Query("SELECT tk FROM Token tk WHERE (tk.expiryDate<:t)")
    List<Token> findAllByExpiryBefore(Timestamp t);
    @Query("SELECT tk FROM Token tk WHERE (tk.teamId=:teamId)")
    List<Token> findAllByTeamId(Long teamId);

}
