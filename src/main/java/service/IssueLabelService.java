package service;

import domain.IssueLabel;
import java.util.List;
import java.util.UUID;

public interface IssueLabelService {

    IssueLabel createLabel(IssueLabel label) throws Exception;
    void updateLabel(IssueLabel label) throws Exception;
    void deleteLabel(UUID labelId) throws Exception;
    List<IssueLabel> getAllLabels() throws Exception;
    IssueLabel getLabelById(UUID labelId) throws Exception;

    List<IssueLabel> searchLabelsByName(String query) throws Exception;

    void addLabelToIssue(UUID issueId, UUID labelId) throws Exception;
    void removeLabelFromIssue(UUID issueId, UUID labelId) throws Exception;
    List<IssueLabel> getLabelsForIssue(UUID issueId) throws Exception;
}
