package domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Project {

    private UUID projectId;

    private String name;
    private String description;
    private UUID ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Project() {}

    public Project(String name, String description, UUID ownerId) {
        this.projectId = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
    }

    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }

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
                "projectId=" + projectId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", ownerId=" + ownerId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
