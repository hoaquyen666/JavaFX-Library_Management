package com.example.seniormanager.dao;

import com.example.seniormanager.model.Account;
import com.example.seniormanager.model.StaffOption;
import com.example.seniormanager.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountDAO {

    // truy vấn một lần lấy thông tin nhân viên, hiển thị tên
    public Account login(String username, String password, String role) {
        String sql = "SELECT * FROM Account WHERE Username = ? AND PasswordHash = ? AND Role = ?";

        try
        {
            Connection connection = DatabaseConnection.getConnection(); // Kết nối
            PreparedStatement ps = connection.prepareStatement(sql); // Tạo lệnh chuẩn bị tránh sqlinjection

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role); // truyền tham số

            ResultSet rs = ps.executeQuery(); // bắt đầu truy vấn

            if (rs.next()) // kiểm tra bản ghi
            {
                Account acc = new Account();
                // đọc kết quả từ rs
                acc.setAccountId(rs.getInt("AccountId"));
                acc.setAccountCode(rs.getString("AccountCode"));
                acc.setStaffId(rs.getObject("StaffId") != null ? rs.getInt("StaffId") : null);
                acc.setReaderId(rs.getObject("ReaderId") != null ? rs.getInt("ReaderId") : null);
                acc.setUsername(rs.getString("Username"));
                acc.setPasswordHash(rs.getString("PasswordHash"));
                acc.setRole(rs.getString("Role"));
                return acc;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Account> getAllStaffAccount(){
        List<Account> list = new ArrayList<>();
        String sql = "SELECT a.AccountCode, s.FullName, s.StaffId, a.Username, a.PasswordHash, a.Role\n" +
                "FROM Account a \n" +
                "JOIN Staff s ON a.StaffId = s.StaffId\n" +
                "where a.Role = \"Librarian\"";
        try(
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ){
            while (rs.next()){
                Account acc = new Account();
                acc.setStaffId(rs.getInt("StaffId"));
                acc.setStaffName(rs.getString("FullName"));
                acc.setUsername(rs.getString("Username"));
                acc.setPasswordHash(rs.getString("PasswordHash"));
                acc.setRole(rs.getString("Role"));

                list.add(acc);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    //Hàm thêm tài khoản cho nhân viên
    public static boolean insertStaffAccount(int staffId, String username, String passwordHash, String role){
        String sql = "INSERT INTO Account (AccountCode, StaffId, Username, PasswordHash, Role) VALUES (?, ?, ?, ?, ?)";
        try(
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ){
            String randomAccountCode = "NV" + UUID.randomUUID().toString().substring(0, 2).toUpperCase();
            ps.setString(1, randomAccountCode);
            ps.setInt(2, staffId);
            ps.setString(3, username);
            ps.setString(4, passwordHash);
            ps.setString(5, role);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    //DS nhân viên để thêm account
    public static List<StaffOption> getStaffsWithoutAccount(){
        List<StaffOption> list = new ArrayList<>();
        // Truy vấn những nhân viên CHƯA CÓ tài khoản trong bảng Account
        String sql = "SELECT StaffId, StaffCode, FullName FROM Staff " +
                "WHERE StaffId NOT IN (SELECT StaffId FROM Account WHERE StaffId IS NOT NULL)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new StaffOption(
                        rs.getInt("StaffId"),
                        rs.getString("StaffCode"),
                        rs.getString("FullName")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean updateAccount(int staffId, String newUsername, String newPassword){
        String sql = "update Account set Username = ?, PasswordHash = ? where StaffId = ?";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ){
            ps.setString(1, newUsername);
            ps.setString(2, newPassword);
            ps.setInt(3,staffId);

            int rowUpdated = ps.executeUpdate();
            return rowUpdated > 1;
        }catch (SQLException e ){
            System.out.println("Có lỗi khi kết nối database!");
            e.printStackTrace();
        }
        return false;

    }

}
