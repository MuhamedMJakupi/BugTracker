package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class IssueLabelMapping {
    private UUID issueId;
    private UUID labelId;

    public IssueLabelMapping() {}

    public IssueLabelMapping(UUID issueId, UUID labelId) {
        this.issueId = issueId;
        this.labelId = labelId;
    }

    public UUID getIssueId() { return issueId; }
    public void setIssueId(UUID issueId) { this.issueId = issueId; }
    public UUID getLabelId() { return labelId; }
    public void setLabelId(UUID labelId) { this.labelId = labelId; }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (issueId == null) {
            errors.add("Issue ID is required");
        }

        if (labelId == null) {
            errors.add("Label ID is required");
        }
        return errors;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        IssueLabelMapping that = (IssueLabelMapping) obj;
        return Objects.equals(issueId, that.issueId) &&
                Objects.equals(labelId, that.labelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issueId, labelId);
    }
}
