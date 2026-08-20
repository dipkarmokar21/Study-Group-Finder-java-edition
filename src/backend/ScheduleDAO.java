package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {
    public List<String[]> getAllSchedules() {
        List<String[]> schedules = new ArrayList<>();
        String sql = "SELECT s.schedule_id, sg.group_id, sg.title, s.meeting_day, s.meeting_time "
                   + "FROM schedules s "
                   + "JOIN study_groups sg ON s.group_id = sg.group_id "
                   + "ORDER BY s.schedule_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                schedules.add(new String[]{
                    String.valueOf(rs.getInt("schedule_id")),
                    String.valueOf(rs.getInt("group_id")),
                    rs.getString("title"),
                    rs.getString("meeting_day"),
                    rs.getString("meeting_time")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }
    public boolean addSchedule(int groupId, String meetingDay, String meetingTime) {
        String sql = "INSERT INTO schedules (group_id, meeting_day, meeting_time) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            pstmt.setString(2, meetingDay);
            pstmt.setString(3, meetingTime);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }
}

