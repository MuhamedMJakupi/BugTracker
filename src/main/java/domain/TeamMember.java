package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (teamId == null) {
            errors.add("Team ID is required");
        }

        if (userId == null) {
            errors.add("User ID is required");
        }

        return errors;
    }

    public List<String> validateForCreation() {
        return validate();
    }

    public List<String> validateForUpdate() {
        return validate();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        TeamMember that = (TeamMember) obj;
        return Objects.equals(teamId, that.teamId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId, userId);
    }
}
