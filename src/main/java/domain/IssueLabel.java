package domain;

import common.AbstractEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IssueLabel extends AbstractEntity {

    private String name;
    private String createdAt;
    public IssueLabel() {}

    public IssueLabel(String name) {

        setLabelId(UUID.randomUUID());
        this.createdAt = LocalDateTime.now().toString();

        this.name = name;
    }

    public UUID getLabelId() {
        return getId();
    }

    public void setLabelId(UUID labelId) {
        setId(labelId);
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

    @Override
    public String toString() {
        return "IssueLabel{" +
                "labelId=" + getLabelId() +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (name == null || name.trim().isEmpty()) {
            errors.add("Label name is required");
        } else if (name.length() > 50) {
            errors.add("Label name should not exceed 50 characters");
        }
        return errors;
    }

    @Override
    public List<String> validateForCreation() {
        List<String> errors = validate();

        if (getLabelId() != null) {
            errors.add("Label ID should not be provided for new labels");
        }

        if (createdAt != null) {
            errors.add("Created timestamp should not be provided for new labels");
        }
        return errors;
    }

}
