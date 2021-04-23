package it.polito.ai.lab2.services.team;

import it.polito.ai.lab2.dtos.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface TeamService {

    // @PreAuthorize("hasRole('ROLE_TEAM_' + #id + '_MEMBER')")
    void activateTeamById (int id);

    // @PreAuthorize("hasRole('ROLE_TEAM_' + #courseName + '_' + #teamName + '_MEMBER')")
    void activateTeam (String courseName, String teamName);

    // @PreAuthorize("hasRole('ROLE_TEAM_' + #id + '_MEMBER')")
    void evictTeamById (int id);

    // @PreAuthorize("hasRole('ROLE_TEAM_' + #courseName + '_' + #teamName + '_MEMBER')")
    void evictTeam (String courseName, String teamName);

    // @PreAuthorize("hasAnyRole('ROLE_TEAM_' + #courseName + '_' + #teamName + '_MEMBER', 'ROLE_COURSE_' + #courseName + '_TEACHER')")
    TeamDTO getTeam (String courseName, String teamName);

    // @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_TEAM_' + #id + '_MEMBER')")
    TeamDTO getTeamById (int id);

    // @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_TEAM_' + #id + '_MEMBER')")
    List<StudentDTO> getMembersById (int id);

    // @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_TEAM_' + #courseName + '_' + #teamName + '_MEMBER')")
    List<StudentDTO> getMembers (String courseName, String teamName);

    // @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_COURSE_' + #courseName + '_STUDENT')")
    TeamDTO proposeTeam (String courseName, String teamName, List<String> memberIds, String creator);

    // @PreAuthorize("hasAnyRole('ROLE_TEAM_' + #courseName + '_' + #teamName + '_MEMBER', 'ROLE_COURSE_' + #courseName + '_TEACHER')")
    int getTeamId (String courseName, String teamName);

    // @PreAuthorize("hasAnyRole('ROLE_TEAM_' + #courseName + '_' + #teamName + '_MEMBER', 'ROLE_COURSE_' + #courseName + '_TEACHER')")
    int getUsedNVCpuForTeam (String courseName, String teamName);

    // @PreAuthorize("hasAnyRole('ROLE_TEAM_' + #courseName + '_' + #teamName + '_MEMBER', 'ROLE_COURSE_' + #courseName + '_TEACHER')")
    int getUsedDiskForTeam (String courseName, String teamName);

    // @PreAuthorize("hasAnyRole('ROLE_TEAM_' + #courseName + '_' + #teamName + '_MEMBER', 'ROLE_COURSE_' + #courseName + '_TEACHER')")
    int getUsedRamForTeam (String courseName, String teamName);

    // @PreAuthorize("hasAnyRole('ROLE_TEAM_' + #courseName + '_' + #teamName + '_MEMBER', 'ROLE_COURSE_' + #courseName + '_TEACHER')")
    List<VmDTO> getVmsForTeam (String courseName, String teamName);

    // @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_TEAM_' + #id + '_MEMBER')")
    List<VmDTO> getVmsForTeamById (int id);

    // @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_TEAM_' + #id + '_MEMBER')")
    String getCreator (int id);
}
