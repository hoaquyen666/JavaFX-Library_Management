package com.example.seniormanager.dao;

import com.example.seniormanager.model.Account;
import com.example.seniormanager.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
