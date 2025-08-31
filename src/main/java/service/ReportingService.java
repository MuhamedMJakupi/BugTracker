package service;

import java.util.Map;
import java.util.UUID;

public interface ReportingService {

    Map<String, Integer> getIssueCountsByStatus() throws Exception;
    Map<String, Integer> getIssueCountsByPriority() throws Exception;
    Map<String, Integer> getIssueCountsByAssignee() throws Exception;
    Map<String, Integer> getIssueCountsByReporter() throws Exception;

    Map<String, Integer> getProjectIssueStats(UUID projectId) throws Exception;

    Double getAverageTimeToResolution() throws Exception;
    Map<String, Integer> getIssuesCreatedByMonth() throws Exception;
    Map<String, Integer> getIssuesResolvedByMonth() throws Exception;

    Map<String, Integer> getIssuesResolvedByUser() throws Exception;
    Map<String, Integer> getOpenIssuesByUser() throws Exception;

    Map<String, Object> getDashboardStats() throws Exception;
}
