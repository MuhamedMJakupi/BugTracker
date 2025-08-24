package service;

import domain.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectValidationService {

    public List<String> validateProject(Project project) {
        List<String> errors = new ArrayList<>();

        if(project.getName() == null || project.getName().trim().isEmpty()) {
            errors.add("Name is required");
        }
        else if(project.getName().length() < 2 || project.getName().length() > 100) {
            errors.add("Name must be between 2 and 100 characters");
        }

        if(project.getDescription() != null && project.getDescription().length() > 500) {
            errors.add("Project description cannot exceed 500 characters");
        }

        if(project.getOwnerId() == null) {
            errors.add("Owner Id is required");
        }

        return errors;
    }

    public List<String> validateProjectForUpdate(Project project){
        List<String> errors =validateProject(project);

        if(project.getProjectId() == null ) {
            errors.add("Project ID is required for update!");
        }
        return errors;
    }

    public List<String> validateProjectForCreation(Project project){
        List<String> errors = validateProject(project);

        if(project.getProjectId()  != null ) {
            errors.add("Project  ID should not be provided for new projects");
        }
        if (project.getCreatedAt() != null) {
            errors.add("Created timestamp should not be provided for new projects");
        }

        if (project.getUpdatedAt() != null) {
            errors.add("Updated timestamp should not be provided for new projects");
        }
        return errors;
    }

}
