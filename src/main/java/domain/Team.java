package domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Team {

    private UUID teamId;
    private String name;
    private LocalDateTime createdAt;

    public Team() {}

    public Team(String name) {
        this.teamId = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();

        this.name = name;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
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
                "teamId=" + teamId +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
