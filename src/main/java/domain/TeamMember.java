package domain;

import java.util.UUID;

public class TeamMember {

    private UUID teamId;
    private UUID userId;

    public TeamMember() {}

    public TeamMember(UUID teamId, UUID userId) {
        this.teamId = teamId;
        this.userId = userId;
    }

    public UUID getTeamId() { return teamId; }
    public void setTeamId(UUID teamId) { this.teamId = teamId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "TeamMember{" +
                "teamId=" + teamId +
                ", userId=" + userId +
                '}';
    }
}
