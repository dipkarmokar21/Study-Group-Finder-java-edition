package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudyGroupDAO {

    public int getTotalGroupCount() {
        String sql = "SELECT COUNT(*) AS total FROM study_groups";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    public int getMyCreatedCount(int userId) {
        String sql = "SELECT COUNT(*) AS total FROM study_groups WHERE owner_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("total");
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getMyJoinedCount(int userId) {
        String sql = "SELECT COUNT(*) AS total FROM group_members gm "
                   + "JOIN study_groups sg ON gm.group_id = sg.group_id "
                   + "WHERE gm.user_id = ? AND sg.owner_id != ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("total");
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    public List<String[]> getMyCreatedGroups(int userId) {
        List<String[]> groups = new ArrayList<>();
        String sql = "SELECT sg.group_id, sg.title, sg.subject, sg.max_members, sg.status, sg.owner_id, "
                   + "u.full_name AS owner_name, "
                   + "s.meeting_day, s.meeting_time, "
                   + "(SELECT COUNT(*) FROM group_members gm WHERE gm.group_id = sg.group_id) AS current_members "
                   + "FROM study_groups sg "
                   + "JOIN users u ON sg.owner_id = u.user_id "
                   + "LEFT JOIN schedules s ON sg.group_id = s.group_id "
                   + "WHERE sg.owner_id = ? "
                   + "ORDER BY sg.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String day = rs.getString("meeting_day");
                String time = rs.getString("meeting_time");
                groups.add(new String[]{
                    String.valueOf(rs.getInt("group_id")),
                    rs.getString("title"),
                    rs.getString("subject"),
                    rs.getString("owner_name"),
                    String.valueOf(rs.getInt("current_members")),
                    String.valueOf(rs.getInt("max_members")),
                    rs.getString("status"),
                    (day == null || day.trim().isEmpty()) ? "NOT SET" : day,
                    (time == null || time.trim().isEmpty()) ? "NOT SET" : time,
                    String.valueOf(rs.getInt("owner_id"))
                });
            }
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return groups;
    }
    public List<String[]> getMyJoinedGroups(int userId) {
        List<String[]> groups = new ArrayList<>();
        String sql = "SELECT sg.group_id, sg.title, sg.subject, sg.max_members, sg.status, sg.owner_id, "
                   + "u.full_name AS owner_name, "
                   + "s.meeting_day, s.meeting_time, "
                   + "(SELECT COUNT(*) FROM group_members gm2 WHERE gm2.group_id = sg.group_id) AS current_members "
                   + "FROM group_members gm "
                   + "JOIN study_groups sg ON gm.group_id = sg.group_id "
                   + "JOIN users u ON sg.owner_id = u.user_id "
                   + "LEFT JOIN schedules s ON sg.group_id = s.group_id "
                   + "WHERE gm.user_id = ? AND sg.owner_id != ? "
                   + "ORDER BY gm.joined_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String day = rs.getString("meeting_day");
                String time = rs.getString("meeting_time");
                groups.add(new String[]{
                    String.valueOf(rs.getInt("group_id")),
                    rs.getString("title"),
                    rs.getString("subject"),
                    rs.getString("owner_name"),
                    String.valueOf(rs.getInt("current_members")),
                    String.valueOf(rs.getInt("max_members")),
                    rs.getString("status"),
                    (day == null || day.trim().isEmpty()) ? "NOT SET" : day,
                    (time == null || time.trim().isEmpty()) ? "NOT SET" : time,
                    String.valueOf(rs.getInt("owner_id"))
                });
            }
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return groups;
    }
    public List<String[]> getGroupsBySubject(String subject) {
        List<String[]> groups = new ArrayList<>();
        String sql = "{CALL GetGroupsBySubject(?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, subject);
            ResultSet rs = cstmt.executeQuery();

            while (rs.next()) {
                groups.add(new String[]{
                    String.valueOf(rs.getInt("group_id")),
                    rs.getString("title"),
                    rs.getString("subject"),
                    rs.getString("owner_name"),
                    String.valueOf(rs.getInt("current_members")),
                    String.valueOf(rs.getInt("max_members")),
                    rs.getString("status"),
                    rs.getString("meeting_day") != null ? rs.getString("meeting_day") : "NOT SET",
                    rs.getString("meeting_time") != null ? rs.getString("meeting_time") : "NOT SET",
                    String.valueOf(rs.getInt("owner_id"))
                });
            }
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return groups;
    }
    public boolean createGroupWithSchedule(int ownerId, String title, String subject,
                                            int maxMembers, String meetingDay, String meetingTime) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String groupSql = "INSERT INTO study_groups (owner_id, title, subject, max_members, status) VALUES (?, ?, ?, ?, 'OPEN')";
            PreparedStatement groupStmt = conn.prepareStatement(groupSql, Statement.RETURN_GENERATED_KEYS);
            groupStmt.setInt(1, ownerId);
            groupStmt.setString(2, title);
            groupStmt.setString(3, subject);
            groupStmt.setInt(4, maxMembers);
            groupStmt.executeUpdate();
            ResultSet keys = groupStmt.getGeneratedKeys();
            keys.next();
            int newGroupId = keys.getInt(1);
            keys.close();
            groupStmt.close();
            String scheduleSql = "INSERT INTO schedules (group_id, meeting_day, meeting_time) VALUES (?, ?, ?)";
            PreparedStatement scheduleStmt = conn.prepareStatement(scheduleSql);
            scheduleStmt.setInt(1, newGroupId);
            scheduleStmt.setString(2, meetingDay);
            scheduleStmt.setString(3, meetingTime);
            scheduleStmt.executeUpdate();
            scheduleStmt.close();
            String memberSql = "INSERT INTO group_members (group_id, user_id) VALUES (?, ?)";
            PreparedStatement memberStmt = conn.prepareStatement(memberSql);
            memberStmt.setInt(1, newGroupId);
            memberStmt.setInt(2, ownerId);
            memberStmt.executeUpdate();
            memberStmt.close();

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }


    public List<String> getAllSubjects() {
        List<String> subjects = new ArrayList<>();
        String sql = "SELECT DISTINCT subject FROM study_groups ORDER BY subject";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) subjects.add(rs.getString("subject"));
        } catch (SQLException e) { e.printStackTrace(); }
        return subjects;
    }

    public boolean deleteGroup(int groupId, int ownerId) {
        String sql = "DELETE FROM study_groups WHERE group_id = ? AND owner_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, ownerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

