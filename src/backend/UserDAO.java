package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class UserDAO {
    public String[] login(String email, String password) {
        String sql = "SELECT user_id, full_name, email, password FROM users WHERE email = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (storedPassword.equals(password)) {
                    return new String[]{
                        String.valueOf(rs.getInt("user_id")),
                        rs.getString("full_name"),
                        rs.getString("email")
                    };
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String[] register(String fullName, String email, String password) {

        String checkSql = "SELECT user_id FROM users WHERE email = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                rs.close();
                return null;
            }
            rs.close();


            String insertSql = "INSERT INTO users (full_name, email, password) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setString(1, fullName);
            insertStmt.setString(2, email);
            insertStmt.setString(3, password);
            insertStmt.executeUpdate();
            ResultSet keys = insertStmt.getGeneratedKeys();
            if (keys.next()) {
                String newId = String.valueOf(keys.getInt(1));
                keys.close();
                insertStmt.close();
                return new String[]{newId, fullName, email};
            }
            insertStmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<String[]> getAllUsers() {
        List<String[]> users = new ArrayList<>();
        String sql = "SELECT user_id, full_name, email, created_at FROM users ORDER BY user_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new String[]{
                    String.valueOf(rs.getInt("user_id")),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("created_at")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}

