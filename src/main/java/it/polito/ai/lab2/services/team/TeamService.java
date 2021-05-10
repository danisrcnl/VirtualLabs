package it.polito.ai.lab2.services.team;

import it.polito.ai.lab2.dtos.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface TeamService {

    void activateTeamById (int id);

    void activateTeam (String courseName, String teamName);

    void evictTeamById (int id);

    void evictTeam (String courseName, String teamName);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    TeamDTO getTeam (String courseName, String teamName);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    TeamDTO getTeamById (int id);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    List<StudentDTO> getMembersById (int id);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    List<StudentDTO> getMembers (String courseName, String teamName);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_COURSE_' + #courseName + '_STUDENT')")
    TeamDTO proposeTeam (String courseName, String teamName, List<String> memberIds, String creator);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    int getTeamId (String courseName, String teamName);

    @PreAuthorize("hasAnyRole('ROLE_TEAM_' + #courseName + '_' + #teamName + '_MEMBER', 'ROLE_COURSE_' + #courseName + '_TEACHER')")
    int getUsedNVCpuForTeam (String courseName, String teamName);

    @PreAuthorize("hasAnyRole('ROLE_TEAM_' + #courseName + '_' + #teamName + '_MEMBER', 'ROLE_COURSE_' + #courseName + '_TEACHER')")
    int getUsedDiskForTeam (String courseName, String teamName);

    @PreAuthorize("hasAnyRole('ROLE_TEAM_' + #courseName + '_' + #teamName + '_MEMBER', 'ROLE_COURSE_' + #courseName + '_TEACHER')")
    int getUsedRamForTeam (String courseName, String teamName);

    @PreAuthorize("hasAnyRole('ROLE_TEAM_' + #courseName + '_' + #teamName + '_MEMBER', 'ROLE_COURSE_' + #courseName + '_TEACHER')")
    List<VmDTO> getVmsForTeam (String courseName, String teamName);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_TEAM_' + #id + '_MEMBER')")
    List<VmDTO> getVmsForTeamById (int id);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    String getCreator (int id);
}
