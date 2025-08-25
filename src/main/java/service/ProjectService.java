package service;

import domain.Project;
import java.util.List;
import java.util.UUID;

public interface ProjectService {

    Project createProject(Project project) throws Exception;
    void updateProject(Project project) throws Exception;
    void deleteProject(UUID projectId) throws Exception;
    Project getProjectById(UUID projectId) throws Exception;
    List<Project> getAllProjects() throws Exception;

    List<Project> getProjectsByOwner(UUID ownerId) throws Exception;
    List<Project> getProjectByName(String projectName) throws Exception;
}
