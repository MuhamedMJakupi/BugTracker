package service;

import common.AbstractService;
import common.DBValidationUtils;
import domain.Team;
import domain.TeamMember;
import domain.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamServiceImpl extends AbstractService implements TeamService {

    private final  DBValidationUtils validationUtils = new DBValidationUtils();

    private Team mapTeam(ResultSet rs) throws Exception {
        Team team = new Team();
        team.setTeamId(UUID.fromString(rs.getString("team_id")));
        team.setName(rs.getString("name"));
        team.setOwnerId(UUID.fromString(rs.getString("owner_id")));
        team.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime().toString());
        return team;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(UUID.fromString(rs.getString("user_id")));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRoleId(rs.getInt("role_id"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime().toString());
        return user;
    }

    public List<Team> getAllTeams() throws Exception {
        List<Team> teams = new ArrayList<>();

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.ALL_TEAMS)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                teams.add(mapTeam(rs));
            }
        }
        return teams;
    }

    public Team getTeamById(UUID teamId) throws Exception {
        validationUtils.validateTeamExists(teamId, "Team");
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.TEAM_BY_ID)) {
            ps.setString(1, teamId.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapTeam(rs);
            }
            return null;
        }
    }

    public Team createTeam(Team team) throws Exception {
        List<String> errors = team.validateForCreation();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
        validationUtils.validateUserExists(team.getOwnerId(), "Owner");
        validationUtils.validateTeamNameUnique(team.getName());
        team.setTeamId(UUID.randomUUID());
        team.setCreatedAt(java.time.LocalDateTime.now().toString());

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.CREATE_TEAM)) {
            ps.setString(1, team.getTeamId().toString());
            ps.setString(2, team.getName());
            ps.setString(3, team.getOwnerId().toString());
            LocalDateTime ldt = LocalDateTime.parse(team.getCreatedAt());
            ps.setTimestamp(4, Timestamp.valueOf(ldt));
            ps.executeUpdate();
        }
        return team;
    }

    public void updateTeam(Team team) throws Exception {
        List<String> errors = team.validate();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }

        validationUtils.validateTeamExists(team.getTeamId(), "Team");
        validationUtils.validateUserExists(team.getOwnerId(), "Owner");
        validationUtils.validateTeamNameUniqueForUpdate(team.getName(), team.getTeamId());

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.UPDATE_TEAM)) {
            ps.setString(1, team.getName());
            ps.setString(2, team.getOwnerId().toString());
            ps.setString(3, team.getTeamId().toString());
            ps.executeUpdate();
        }
    }

    public void deleteTeam(UUID teamId) throws Exception {
        validationUtils.validateTeamExists(teamId, "Team");

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.DELETE_TEAM)) {
            ps.setString(1, teamId.toString());
            ps.executeUpdate();
        }
    }

    public List<Team> getTeamsByOwner(UUID ownerId) throws Exception {
        validationUtils.validateUserExists(ownerId, "Owner");
        List<Team> teams = new ArrayList<>();
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.TEAMS_BY_OWNER)) {
            ps.setString(1, ownerId.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                teams.add(mapTeam(rs));
            }
        }
        return teams;
    }

    public List<Team> getTeamByName(String teamName) throws Exception {
        List<Team> teams = new ArrayList<>();
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.TEAM_BY_NAME)) {
            ps.setString(1, "%" + teamName + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                 teams.add(mapTeam(rs));
            }
            return teams;
        }
    }

    public void addTeamMember(UUID teamId, UUID userId) throws Exception {
        TeamMember teamMember = new TeamMember();
        teamMember.setTeamId(teamId);
        teamMember.setUserId(userId);

        List<String> errors = teamMember.validate();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }

        validationUtils.validateTeamExists(teamId, "Team");
        validationUtils.validateUserExists(userId, "User");

        if (validationUtils.isUserTeamMember(teamId, userId)) {
            throw new IllegalArgumentException("User is already a member of this team");
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.ADD_TEAM_MEMBER)) {
            ps.setString(1, teamId.toString());
            ps.setString(2, userId.toString());
            ps.executeUpdate();
        }
    }

    public void removeTeamMember(UUID teamId, UUID userId) throws Exception {

        validationUtils.validateTeamExists(teamId, "Team");
        validationUtils.validateUserExists(userId, "User");

        if (!validationUtils.isUserTeamMember(teamId, userId)) {
            throw new IllegalArgumentException("User is not a member of this team");
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.REMOVE_TEAM_MEMBER)) {
            ps.setString(1, teamId.toString());
            ps.setString(2, userId.toString());
            ps.executeUpdate();
        }
    }

    public List<User> getTeamMembers(UUID teamId) throws Exception {
        validationUtils.validateTeamExists(teamId, "Team");

        List<User> members = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.GET_TEAM_MEMBERS)) {
            ps.setString(1, teamId.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                members.add(mapUser(rs));
            }
        }
        return members;
    }

    public List<Team> getUserTeams(UUID userId) throws Exception {
        validationUtils.validateUserExists(userId, "User");

        List<Team> teams = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.GET_USER_TEAMS)) {
            ps.setString(1, userId.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                teams.add(mapTeam(rs));
            }
        }
        return teams;
    }

    public static class SQL{

        public static final String ALL_TEAMS = "SELECT * FROM teams";
        public static final String TEAM_BY_ID = "SELECT * FROM teams WHERE team_id = ?";
        public static final String CREATE_TEAM = "INSERT INTO teams (team_id, name, owner_id, created_at) VALUES (?, ?, ?, ?)";
        public static final String UPDATE_TEAM = "UPDATE teams SET name = ?, owner_id = ? WHERE team_id = ?";
        public static final String DELETE_TEAM = "DELETE FROM teams WHERE team_id = ?";
        public static final String TEAMS_BY_OWNER = "SELECT * FROM teams WHERE owner_id = ? ORDER BY created_at DESC";
        public static final String TEAM_BY_NAME = "SELECT * FROM teams WHERE name LIKE ?";

        public static final String ADD_TEAM_MEMBER =
                "INSERT INTO team_members (team_id, user_id) VALUES (?, ?)";
        public static final String REMOVE_TEAM_MEMBER =
                "DELETE FROM team_members WHERE team_id = ? AND user_id = ?";
        public static final String GET_TEAM_MEMBERS =
                "SELECT u.* FROM users u JOIN team_members tm ON u.user_id = tm.user_id WHERE tm.team_id = ?";
        public static final String GET_USER_TEAMS =
                "SELECT t.* FROM teams t JOIN team_members tm ON t.team_id = tm.team_id WHERE tm.user_id = ?";

    }
}
