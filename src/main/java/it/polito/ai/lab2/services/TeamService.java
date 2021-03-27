package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.*;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface TeamService {

    void activateTeamById (int id);

    void activateTeam (String courseName, String teamName);

    void evictTeamById (int id);

    void evictTeam (String courseName, String teamName);

    TeamDTO getTeam (String courseName, String teamName);

    TeamDTO getTeamById (int id);

    List<StudentDTO> getMembersById (int id);

    List<StudentDTO> getMembers (String courseName, String teamName);

    TeamDTO proposeTeam (String courseName, String teamName, List<String> memberIds);

    int getTeamId (String courseName, String teamName);

    int getUsedNVCpuForTeam (String courseName, String teamName);

    int getUsedDiskForTeam (String courseName, String teamName);

    int getUsedRamForTeam (String courseName, String teamName);

    List<VmDTO> getVmsForTeam (String courseName, String teamName);

    List<VmDTO> getVmsForTeamById (int id);
}
