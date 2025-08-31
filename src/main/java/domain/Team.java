package domain;

import common.AbstractEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team extends AbstractEntity {

    private String name;
    private LocalDateTime createdAt;

    public Team() {}

    public Team(String name) {

        setTeamId(UUID.randomUUID());
        this.createdAt = LocalDateTime.now();

        this.name = name;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Team{" +
                "teamId=" + getTeamId() +
                ", name='" + name + '\'' +
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

    @Override
    public List<String> validateForUpdate() {
        List<String> errors = validate();

        if (getTeamId() == null) {
            errors.add("Team id is required");
        }
        return errors;
    }
}
