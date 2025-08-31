package service;

import common.AbstractService;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReportingServiceImpl extends AbstractService implements ReportingService {

    @Override
    public Map<String, Integer> getIssueCountsByStatus() throws Exception {
        Map<String, Integer> stats = new HashMap<>();
        String sql = """
            SELECT ist.name as status_name, COUNT(*) as count
            FROM issues i
            JOIN issue_statuses ist ON i.status_id = ist.status_id
            GROUP BY ist.status_id, ist.name
            ORDER BY ist.status_id
            """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                stats.put(rs.getString("status_name"), rs.getInt("count"));
            }
        }
        return stats;
    }

    @Override
    public Map<String, Integer> getIssueCountsByPriority() throws Exception {
        Map<String, Integer> stats = new HashMap<>();
        String sql = """
            SELECT ipr.name as priority_name, COUNT(*) as count
            FROM issues i
            JOIN issue_priorities ipr ON i.priority_id = ipr.priority_id
            GROUP BY ipr.priority_id, ipr.name
            ORDER BY ipr.priority_id
            """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                stats.put(rs.getString("priority_name"), rs.getInt("count"));
            }
        }
        return stats;
    }

    @Override
    public Map<String, Integer> getIssueCountsByAssignee() throws Exception {
        Map<String, Integer> stats = new HashMap<>();
        String sql = """
            SELECT COALESCE(u.name, 'Unassigned') as assignee_name, COUNT(*) as count
            FROM issues i
            LEFT JOIN users u ON i.assignee_id = u.user_id
            GROUP BY u.user_id, u.name
            ORDER BY count DESC
            """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                stats.put(rs.getString("assignee_name"), rs.getInt("count"));
            }
        }
        return stats;
    }

    @Override
    public Map<String, Integer> getIssueCountsByReporter() throws Exception {
        Map<String, Integer> stats = new HashMap<>();
        String sql = """
            SELECT u.name as reporter_name, COUNT(*) as count
            FROM issues i
            JOIN users u ON i.reporter_id = u.user_id
            GROUP BY u.user_id, u.name
            ORDER BY count DESC
            """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                stats.put(rs.getString("reporter_name"), rs.getInt("count"));
            }
        }
        return stats;
    }

    @Override
    public Map<String, Integer> getProjectIssueStats(UUID projectId) throws Exception {
        Map<String, Integer> stats = new HashMap<>();
        String sql = """
            SELECT ist.name as status, COUNT(*) as count
            FROM issues i
            JOIN issue_statuses ist ON i.status_id = ist.status_id
            WHERE i.project_id = ?
            GROUP BY ist.status_id, ist.name
            """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, projectId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.put(rs.getString("status"), rs.getInt("count"));
                }
            }
        }
        return stats;
    }

    @Override
    public Double getAverageTimeToResolution() throws Exception {
        String sql = """
            SELECT AVG(TIMESTAMPDIFF(DAY, i.created_at, i.updated_at)) as avg_days
            FROM issues i
            JOIN issue_statuses ist ON i.status_id = ist.status_id
            WHERE ist.name = 'DONE'
            """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("avg_days");
            }
        }
        return 0.0;
    }

    @Override
    public Map<String, Integer> getIssuesCreatedByMonth() throws Exception {
        Map<String, Integer> stats = new HashMap<>();
        String sql = """
            SELECT 
                DATE_FORMAT(created_at, '%Y-%m') as month,
                COUNT(*) as count
            FROM issues
            WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)
            GROUP BY DATE_FORMAT(created_at, '%Y-%m')
            ORDER BY month
            """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                stats.put(rs.getString("month"), rs.getInt("count"));
            }
        }
        return stats;
    }

    @Override
    public Map<String, Integer> getIssuesResolvedByMonth() throws Exception {
        Map<String, Integer> stats = new HashMap<>();
        String sql = """
            SELECT 
                DATE_FORMAT(updated_at, '%Y-%m') as month,
                COUNT(*) as count
            FROM issues i
            JOIN issue_statuses ist ON i.status_id = ist.status_id
            WHERE ist.name = 'DONE'
            AND updated_at >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)
            GROUP BY DATE_FORMAT(updated_at, '%Y-%m')
            ORDER BY month
            """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                stats.put(rs.getString("month"), rs.getInt("count"));
            }
        }
        return stats;
    }

    @Override
    public Map<String, Integer> getIssuesResolvedByUser() throws Exception {
        Map<String, Integer> stats = new HashMap<>();
        String sql = """
            SELECT u.name as user_name, COUNT(*) as count
            FROM issues i
            JOIN users u ON i.assignee_id = u.user_id
            JOIN issue_statuses ist ON i.status_id = ist.status_id
            WHERE ist.name = 'DONE'
            GROUP BY u.user_id, u.name
            ORDER BY count DESC
            """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                stats.put(rs.getString("user_name"), rs.getInt("count"));
            }
        }
        return stats;
    }

    @Override
    public Map<String, Integer> getOpenIssuesByUser() throws Exception {
        Map<String, Integer> stats = new HashMap<>();
        String sql = """
            SELECT COALESCE(u.name, 'Unassigned') as user_name, COUNT(*) as count
            FROM issues i
            LEFT JOIN users u ON i.assignee_id = u.user_id
            JOIN issue_statuses ist ON i.status_id = ist.status_id
            WHERE ist.name IN ('TODO', 'IN_PROGRESS')
            GROUP BY u.user_id, u.name
            ORDER BY count DESC
            """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                stats.put(rs.getString("user_name"), rs.getInt("count"));
            }
        }
        return stats;
    }

    @Override
    public Map<String, Object> getDashboardStats() throws Exception {
        Map<String, Object> dashboard = new HashMap<>();

        dashboard.put("totalIssues", getTotalIssueCount());
        dashboard.put("openIssues", getOpenIssueCount());
        dashboard.put("resolvedIssues", getResolvedIssueCount());
        dashboard.put("totalProjects", getTotalProjectCount());
        dashboard.put("totalUsers", getTotalUserCount());

        dashboard.put("issuesByStatus", getIssueCountsByStatus());
        dashboard.put("issuesByPriority", getIssueCountsByPriority());

        dashboard.put("averageResolutionTime", getAverageTimeToResolution());

        return dashboard;
    }

    private int getTotalIssueCount() throws Exception {
        String sql = "SELECT COUNT(*) FROM issues";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int getOpenIssueCount() throws Exception {
        String sql = """
            SELECT COUNT(*) FROM issues i
            JOIN issue_statuses ist ON i.status_id = ist.status_id
            WHERE ist.name IN ('TODO', 'IN_PROGRESS')
            """;
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int getResolvedIssueCount() throws Exception {
        String sql = """
            SELECT COUNT(*) FROM issues i
            JOIN issue_statuses ist ON i.status_id = ist.status_id
            WHERE ist.name = 'DONE'
            """;
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int getTotalProjectCount() throws Exception {
        String sql = "SELECT COUNT(*) FROM projects";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int getTotalUserCount() throws Exception {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
