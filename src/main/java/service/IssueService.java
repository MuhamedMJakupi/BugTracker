package service;

import domain.Issue;
import domain.IssueHistory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IssueService {

    List<Issue> getAllIssues() throws Exception;
    Issue getIssueById(UUID issueId) throws Exception;
    Issue createIssue(Issue issue) throws Exception;
    void updateIssue(Issue issue, UUID changedByUserId) throws Exception;
    void deleteIssue(UUID issueId) throws Exception;

    List<Issue> getIssueForProject(UUID projectId) throws Exception;
    List<Issue> getIssuesByReporter(UUID reporterId) throws Exception;
    List<Issue> getIssuesByAssignee(UUID assigneeId) throws Exception;
    List<Issue> getIssuesByStatus(int statusId) throws Exception;
    List<Issue> getIssuesByPriority(int priorityId) throws Exception;

    List<Issue> searchIssuesByTitle(String searchTerm) throws Exception;

    List<IssueHistory> getIssueHistory(UUID issueId) throws Exception;
    void recordHistoryChange(IssueHistory history) throws Exception;

    List<Map<String, Object>> getAllPriorities() throws Exception;
    List<Map<String, Object>> getAllStatuses() throws Exception;

    //void updateIssue(Issue issue) throws Exception;
    //void updateIssueWithHistory(Issue issue, UUID changedByUserId) throws Exception;


}
