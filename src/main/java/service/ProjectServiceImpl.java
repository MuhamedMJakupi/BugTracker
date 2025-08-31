package service;

import common.AbstractService;
import common.DBValidationUtils;
import domain.Project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectServiceImpl extends AbstractService implements  ProjectService {

    private final DBValidationUtils validationUtils = new DBValidationUtils();


    private Project mapProject(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setProjectId(UUID.fromString(rs.getString("project_id")));
        project.setName(rs.getString("name"));
        project.setDescription(rs.getString("description"));
        project.setOwnerId(UUID.fromString(rs.getString("owner_id")));
        project.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        project.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return project;
    }

    public List<Project> getAllProjects() throws Exception {
        List<Project> projects = new ArrayList<>();

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.ALL_PROJECTS)) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                projects.add(mapProject(rs));
            }
        }
        return projects;
    }

    public Project getProjectById(UUID id) throws Exception {

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.PROJECT_BY_ID)) {
            ps.setString(1, id.toString());
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return mapProject(rs);
            }
            return null;
        }
    }

    public Project createProject(Project project) throws Exception {

        List<String> errors = project.validateForCreation();
        if(!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }

        validationUtils.validateProjectNameUnique(project.getName());

        project.setProjectId(UUID.randomUUID());
        project.setCreatedAt(java.time.LocalDateTime.now());
        project.setUpdatedAt(java.time.LocalDateTime.now());

        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.CREATE_PROJECT)) {
            ps.setString(1,project.getProjectId().toString());
            ps.setString(2, project.getName());
            ps.setString(3, project.getDescription());
            ps.setString(4,project.getOwnerId().toString());
            ps.setTimestamp(5, Timestamp.valueOf(project.getCreatedAt()));
            ps.setTimestamp(6, Timestamp.valueOf(project.getUpdatedAt()));

            ps.executeUpdate();
        }
        return project;
    }


    public void updateProject(Project project) throws Exception {

        List<String> errors = project.validateForUpdate();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }

        Project existingProject = getProjectById(project.getProjectId());

        if(existingProject == null) {
            throw new IllegalArgumentException(project.getProjectId() + " does not exist");
        }

        validationUtils.validateProjectNameUniqueForUpdate(project.getName(), project.getProjectId());

        project.setCreatedAt(existingProject.getCreatedAt());
        project.setUpdatedAt(java.time.LocalDateTime.now());

        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.UPDATE_PROJECT)) {
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setString(3, project.getOwnerId().toString());
            ps.setTimestamp(4, Timestamp.valueOf(project.getUpdatedAt()));
            ps.setString(5, project.getProjectId().toString());
            ps.executeUpdate();
        }
    }

    public void deleteProject(UUID id) throws Exception {

        Project existingProject = getProjectById(id);
        if(existingProject==null) {
            throw new IllegalArgumentException(id + " not found");
        }

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.DELETE_PROJECT)) {
            ps.setString(1, id.toString());
            ps.executeUpdate();
        }
    }

    public List<Project> getProjectByName(String name) throws Exception {
        List<Project> projects = new ArrayList<>();
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.PROJECT_BY_NAME)) {
            ps.setString(1, "%"+name+"%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                projects.add(mapProject(rs));
            }
            return projects;
        }
    }

    public List<Project> getProjectsByOwner(UUID ownerId) throws Exception {
        List<Project> projects = new ArrayList<>();
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.PROJECT_BY_OWNER_ID)) {
            ps.setString(1, ownerId.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                projects.add(mapProject(rs));
            }
        }
        return projects;
    }

    public static class SQL {

        public static final String ALL_PROJECTS = "SELECT * FROM projects";
        public static final String PROJECT_BY_ID = "SELECT * FROM projects WHERE project_id = ?";
        public static final String CREATE_PROJECT = """
                INSERT INTO projects (project_id, name, description, owner_id, created_at, updated_at) 
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        public  static final String UPDATE_PROJECT = """
            UPDATE projects 
            SET name=?,description=?,owner_id=?, updated_at=? 
            WHERE project_id=?
            """;
        public static final String DELETE_PROJECT = "DELETE FROM projects WHERE project_id = ?";
        public static final String PROJECT_BY_NAME = "SELECT * FROM projects WHERE name LIKE ?";
        public static final String PROJECT_BY_OWNER_ID = "SELECT * FROM projects WHERE owner_id = ?";
    }
}
