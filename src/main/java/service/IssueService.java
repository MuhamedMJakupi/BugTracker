package service;

import domain.Issue;
import java.util.List;
import java.util.UUID;

public interface IssueService {

    Issue createIssue(Issue issue) throws Exception;
    void updateIssue(Issue issue) throws Exception;
    void deleteIssue(UUID issueId) throws Exception;
    Issue getIssueById(UUID issueId) throws Exception;
    List<Issue> getAllIssues() throws Exception;

    List<Issue> getIssueForProject(UUID projectId) throws Exception;
    List<Issue> getIssuesByReporter(UUID reporterId) throws Exception;
    List<Issue> getIssuesByAssignee(UUID assigneeId) throws Exception;
    List<Issue> getIssuesByStatus(int statusId) throws Exception;
    List<Issue> getIssuesByPriority(int priorityId) throws Exception;
    List<Issue> searchIssuesByTitle(String searchTerm) throws Exception;
}
