package domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class IssueLabel {

    private UUID labelId;
    private String name;
    private LocalDateTime createdAt;

    public IssueLabel() {}

    public IssueLabel(String name) {
        this.labelId = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();

        this.name = name;
    }

    public UUID getLabelId() {
        return labelId;
    }

    public void setLabelId(UUID labelId) {
        this.labelId = labelId;
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
        return "IssueLabel{" +
                "labelId=" + labelId +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
