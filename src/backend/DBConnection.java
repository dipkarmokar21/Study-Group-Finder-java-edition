package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/study_group_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";


    private static boolean initialized = false;
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found! Add mysql-connector-j JAR to classpath.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        if (!initialized) {
            initializeDatabase(conn);
            initialized = true;
        }
        return conn;
    }
    private static void initializeDatabase(Connection conn) {
        try (Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TRIGGER IF EXISTS trg_after_member_insert");
            stmt.execute(
                "CREATE TRIGGER trg_after_member_insert " +
                "AFTER INSERT ON group_members " +
                "FOR EACH ROW " +
                "BEGIN " +
                "    DECLARE member_count INT; " +
                "    DECLARE allowed_max INT; " +
                "    SELECT COUNT(*) INTO member_count FROM group_members WHERE group_id = NEW.group_id; " +
                "    SELECT max_members INTO allowed_max FROM study_groups WHERE group_id = NEW.group_id; " +
                "    IF member_count >= allowed_max THEN " +
                "        UPDATE study_groups SET status = 'FULL' WHERE group_id = NEW.group_id; " +
                "    END IF; " +
                "END"
            );
            System.out.println("Trigger 'trg_after_member_insert' created.");
            stmt.execute("DROP TRIGGER IF EXISTS trg_after_member_delete");
            stmt.execute(
                "CREATE TRIGGER trg_after_member_delete " +
                "AFTER DELETE ON group_members " +
                "FOR EACH ROW " +
                "BEGIN " +
                "    DECLARE member_count INT; " +
                "    DECLARE allowed_max INT; " +
                "    SELECT COUNT(*) INTO member_count FROM group_members WHERE group_id = OLD.group_id; " +
                "    SELECT max_members INTO allowed_max FROM study_groups WHERE group_id = OLD.group_id; " +
                "    IF member_count < allowed_max THEN " +
                "        UPDATE study_groups SET status = 'OPEN' WHERE group_id = OLD.group_id; " +
                "    END IF; " +
                "END"
            );
            System.out.println("Trigger 'trg_after_member_delete' created.");
            stmt.execute("DROP PROCEDURE IF EXISTS GetGroupsBySubject");
            stmt.execute(
                "CREATE PROCEDURE GetGroupsBySubject(IN p_subject VARCHAR(100)) " +
                "BEGIN " +
                "    IF p_subject IS NULL OR p_subject = '' OR p_subject = 'ALL' THEN " +
                "        SELECT sg.group_id, sg.title, sg.subject, sg.max_members, sg.status, sg.owner_id, " +
                "               u.full_name AS owner_name, s.meeting_day, s.meeting_time, " +
                "               COUNT(gm.user_id) AS current_members " +
                "        FROM study_groups sg " +
                "        JOIN users u ON sg.owner_id = u.user_id " +
                "        LEFT JOIN schedules s ON sg.group_id = s.group_id " +
                "        LEFT JOIN group_members gm ON sg.group_id = gm.group_id " +
                "        GROUP BY sg.group_id, s.meeting_day, s.meeting_time " +
                "        ORDER BY sg.created_at DESC; " +
                "    ELSE " +
                "        SELECT sg.group_id, sg.title, sg.subject, sg.max_members, sg.status, sg.owner_id, " +
                "               u.full_name AS owner_name, s.meeting_day, s.meeting_time, " +
                "               COUNT(gm.user_id) AS current_members " +
                "        FROM study_groups sg " +
                "        JOIN users u ON sg.owner_id = u.user_id " +
                "        LEFT JOIN schedules s ON sg.group_id = s.group_id " +
                "        LEFT JOIN group_members gm ON sg.group_id = gm.group_id " +
                "        WHERE sg.subject = p_subject " +
                "        GROUP BY sg.group_id, s.meeting_day, s.meeting_time " +
                "        ORDER BY sg.created_at DESC; " +
                "    END IF; " +
                "END"
            );
            System.out.println("Stored Procedure 'GetGroupsBySubject' created.");
            stmt.execute("UPDATE users SET password = 'password123' WHERE email LIKE '%@university.edu'");
            System.out.println("Demo account passwords updated for Java login.");

            System.out.println("Database initialization complete!");

        } catch (SQLException e) {
            System.err.println("Warning: Could not initialize triggers/procedure: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

