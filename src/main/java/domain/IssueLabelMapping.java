package domain;

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
}
