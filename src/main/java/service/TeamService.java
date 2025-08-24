package service;

import common.AbstractService;
import domain.Team;
import domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamService extends AbstractService {

    private boolean teamExists(UUID teamId) throws Exception {
        //String sql = "SELECT COUNT(*) FROM teams WHERE team_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.TEAM_EXISTS)) {
            ps.setString(1, teamId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private boolean teamNameExists(String name) throws Exception {
        //String sql = "SELECT COUNT(*) FROM teams WHERE name = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.TEAM_NAME_EXISTS)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    private boolean teamNameExistsExcludingTeam(String name, UUID excludeTeamId) throws Exception {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.TEAM_NAME_EXISTS_EXCLUDING)) {
            ps.setString(1, name);
            ps.setString(2, excludeTeamId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }


    private Team mapTeam(ResultSet rs) throws Exception {
        Team team = new Team();
        team.setTeamId(UUID.fromString(rs.getString("team_id")));
        team.setName(rs.getString("name"));
        team.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return team;
    }

    public Team getTeamById(UUID teamId) throws Exception {
        //String sql = "SELECT * FROM teams WHERE team_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.TEAM_BY_ID)) {
            ps.setString(1, teamId.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapTeam(rs);
            }
            return null;
        }
    }

    public List<Team> getAllTeams() throws Exception {
        List<Team> teams = new ArrayList<>();
        //String sql = "SELECT * FROM teams";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.ALL_TEAMS)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                teams.add(mapTeam(rs));
            }
        }
        return teams;
    }
    private final TeamValidationService teamValidationService = new TeamValidationService();

    public Team createTeam(Team team) throws Exception {
        List<String> errors = teamValidationService.validateTeamForCreate(team);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
        if(teamNameExists(team.getName())) {
            throw new IllegalArgumentException("Team name already exists: " + team.getName());
        }

        team.setTeamId(UUID.randomUUID());
        team.setCreatedAt(java.time.LocalDateTime.now());

        //String sql = "INSERT INTO teams (team_id, name, created_at) VALUES (?, ?, ?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.CREATE_TEAM)) {
            ps.setString(1, team.getTeamId().toString());
            ps.setString(2, team.getName());
            ps.setTimestamp(3, Timestamp.valueOf(team.getCreatedAt()));
            ps.executeUpdate();
        }
        return team;
    }

    public void updateTeam(Team team) throws Exception {
        List<String> errors = teamValidationService.validateTeamForUpdate(team);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
        if (teamNameExistsExcludingTeam(team.getName(), team.getTeamId())) {
            throw new IllegalArgumentException("Team name already exists: " + team.getName());
        }

        Team existingTeam = getTeamById(team.getTeamId());
        if(existingTeam == null) {
            throw new IllegalArgumentException("Team not found: " + team.getTeamId());
        }


        //String sql = "UPDATE teams SET name = ? WHERE team_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.UPDATE_TEAM)) {
            ps.setString(1, team.getName());
            ps.setString(2, team.getTeamId().toString());
            ps.executeUpdate();
        }
    }

    public void deleteTeam(UUID teamId) throws Exception {
        Team existingTeam = getTeamById(teamId);
        if (existingTeam == null) {
            throw new IllegalArgumentException("Team not found: " + teamId);
        }

        //String sql = "DELETE FROM teams WHERE team_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.DELETE_TEAM)) {
            ps.setString(1, teamId.toString());
            ps.executeUpdate();
        }
    }

    public List<Team> getTeamByName(String teamName) throws Exception {
        //String sql = "SELECT * FROM teams WHERE name Like ?";
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
        if (getTeamById(teamId) == null) {
            throw new IllegalArgumentException("Team not found");
        }

        if (!userExists(userId)) {
            throw new IllegalArgumentException("User not found");
        }

        if (isUserTeamMember(teamId, userId)) {
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
        if (!isUserTeamMember(teamId, userId)) {
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
        if (getTeamById(teamId) == null) {
            throw new IllegalArgumentException("Team not found");
        }

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
        if (!userExists(userId)) {
            throw new IllegalArgumentException("User not found");
        }

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


    private boolean isUserTeamMember(UUID teamId, UUID userId) throws SQLException {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.CHECK_TEAM_MEMBERSHIP)) {
            ps.setString(1, teamId.toString());
            ps.setString(2, userId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private boolean userExists(UUID userId) throws SQLException {
        //String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.USER_EXISTS)) {
            ps.setString(1, userId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(UUID.fromString(rs.getString("user_id")));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRoleId(rs.getInt("role_id"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }


    public static class SQL{
        public static final String USER_EXISTS = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        public static final String TEAM_EXISTS = "SELECT COUNT(*) FROM teams WHERE team_id = ?";
        public static final String TEAM_NAME_EXISTS = "SELECT COUNT(*) FROM teams WHERE name = ?";
        public static final String TEAM_BY_ID = "SELECT * FROM teams WHERE team_id = ?";
        public static final String ALL_TEAMS = "SELECT * FROM teams";
        public static final String CREATE_TEAM="INSERT INTO teams (team_id, name, created_at) VALUES (?, ?, ?)";
        public static final String UPDATE_TEAM = "UPDATE teams SET name = ? WHERE team_id = ?";
        public static final String DELETE_TEAM = "DELETE FROM teams WHERE team_id = ?";
        public static final String TEAM_BY_NAME = "SELECT * FROM teams WHERE name LIKE ?";


        public static final String ADD_TEAM_MEMBER =
                "INSERT INTO team_members (team_id, user_id) VALUES (?, ?)";

        public static final String REMOVE_TEAM_MEMBER =
                "DELETE FROM team_members WHERE team_id = ? AND user_id = ?";

        public static final String CHECK_TEAM_MEMBERSHIP =
                "SELECT COUNT(*) FROM team_members WHERE team_id = ? AND user_id = ?";

        public static final String GET_TEAM_MEMBERS =
                "SELECT u.* FROM users u JOIN team_members tm ON u.user_id = tm.user_id WHERE tm.team_id = ?";

        public static final String GET_USER_TEAMS =
                "SELECT t.* FROM teams t JOIN team_members tm ON t.team_id = tm.team_id WHERE tm.user_id = ?";

        public static final String REMOVE_ALL_TEAM_MEMBERS =
                "DELETE FROM team_members WHERE team_id = ?";
        public static final String TEAM_NAME_EXISTS_EXCLUDING =
                "SELECT COUNT(*) FROM teams WHERE name = ? AND team_id != ?";


    }


}
