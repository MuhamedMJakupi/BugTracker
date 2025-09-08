package domain;

import common.AbstractEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team extends AbstractEntity {

    private String name;
    private UUID ownerId;
    private String createdAt;

    public Team() {}

    public Team(String name,UUID ownerId) {

        setTeamId(UUID.randomUUID());
        this.createdAt = LocalDateTime.now().toString();

        this.name = name;
        this.ownerId = ownerId;
    }

    public UUID getTeamId() {
        return getId();
    }

    public void setTeamId(UUID teamId) {
        setId(teamId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return "Team{" +
                "teamId=" + getTeamId() +
                ", name='" + name + '\'' +
                ", ownerId=" + ownerId +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (name == null || name.trim().isEmpty()) {
            errors.add("Team name is required");
        } else if (name.length() < 2 || name.length() > 100) {
            errors.add("Team name must be between 2 and 100 characters");
        }
        if (ownerId == null || ownerId.toString().trim().isEmpty()) {
            errors.add("Team owner id is required");
        }
        return errors;
    }

    @Override
    public List<String> validateForCreation() {
        List<String> errors = validate();

        if (getTeamId() != null) {
            errors.add("Team ID should not be provided for new teams");
        }

        if (createdAt != null) {
            errors.add("Created at should not be provided for new teams");
        }
        return errors;
    }

}
