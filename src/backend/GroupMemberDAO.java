package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupMemberDAO {


    public boolean isMember(int groupId, int userId) {
        String sql = "SELECT COUNT(*) AS cnt FROM group_members WHERE group_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("cnt") > 0;
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public int joinGroup(int groupId, int userId) {
        // Check capacity and membership
        String checkSql = "SELECT sg.max_members, sg.status, "
                        + "(SELECT COUNT(*) FROM group_members WHERE group_id = sg.group_id) AS current_count, "
                        + "(SELECT COUNT(*) FROM group_members WHERE group_id = sg.group_id AND user_id = ?) AS is_member "
                        + "FROM study_groups sg WHERE sg.group_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, groupId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                if (rs.getInt("is_member") > 0) {
                    rs.close();
                    return 2;
                }
                if (rs.getString("status").equals("FULL") || rs.getInt("current_count") >= rs.getInt("max_members")) {
                    rs.close();
                    return 1;
                }
            }
            rs.close();
            String insertSql = "INSERT INTO group_members (group_id, user_id) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertSql);
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            pstmt.close();
            return 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 3;
        }
    }

    public boolean leaveGroup(int groupId, int userId) {
        String sql = "DELETE FROM group_members WHERE group_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String[]> getMembersForGroup(int groupId) {
        List<String[]> members = new ArrayList<>();
        String sql = "SELECT u.full_name, u.email, gm.joined_at " +
                     "FROM group_members gm " +
                     "JOIN users u ON gm.user_id = u.user_id " +
                     "WHERE gm.group_id = ? " +
                     "ORDER BY gm.joined_at ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                members.add(new String[]{
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("joined_at")
                });
            }
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return members;
    }
}

