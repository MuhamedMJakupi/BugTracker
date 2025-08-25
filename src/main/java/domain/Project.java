package domain;

import common.AbstractEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Project extends AbstractEntity {

    //private UUID projectId;
    private String name;
    private String description;
    private UUID ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Project() {}

    public Project(String name, String description, UUID ownerId) {
        //this.projectId = UUID.randomUUID();
        setProjectId(UUID.randomUUID());
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
    }

    public UUID getProjectId() { return getId(); }
    public void setProjectId(UUID projectId) { setId(projectId); }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        updateTimestamp();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
        updateTimestamp();
    }

    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
        updateTimestamp();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectId=" + getProjectId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", ownerId=" + ownerId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (name == null || name.trim().isEmpty()) {
            errors.add("Name is required");
        } else if (name.length() < 2 || name.length() > 100) {
            errors.add("Name must be between 2 and 100 characters");
        }

        if (description != null && description.length() > 500) {
            errors.add("Project description cannot exceed 500 characters");
        }

        if (ownerId == null) {
            errors.add("Owner Id is required");
        }

        return errors;    }

    @Override
    public List<String> validateForCreation() {
        List<String> errors = validate();

        if (getProjectId() != null) {
            errors.add("Project ID should not be provided for new projects");
        }

        if (createdAt != null) {
            errors.add("Created timestamp should not be provided for new projects");
        }

        if (updatedAt != null) {
            errors.add("Updated timestamp should not be provided for new projects");
        }

        return errors;    }

    @Override
    public List<String> validateForUpdate() {
        List<String> errors = validate();

        if (getProjectId() == null) {
            errors.add("Project ID is required for updates");
        }

        return errors;    }
}
