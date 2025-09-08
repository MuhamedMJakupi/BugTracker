package service;

import domain.Team;
import domain.User;

import java.util.List;
import java.util.UUID;

public interface TeamService {

    List<Team> getAllTeams() throws Exception;
    Team getTeamById(UUID teamId) throws Exception;
    Team createTeam(Team team) throws Exception;
    void updateTeam(Team team) throws Exception;
    void deleteTeam(UUID teamId) throws Exception;

     List<Team> getTeamsByOwner(UUID ownerId) throws Exception;
    List<Team> getTeamByName(String teamName) throws Exception;

    void addTeamMember(UUID teamId, UUID userId) throws Exception;
    void removeTeamMember(UUID teamId, UUID userId) throws Exception;
    List<User> getTeamMembers(UUID teamId) throws Exception;
    List<Team> getUserTeams(UUID userId) throws Exception;

}
