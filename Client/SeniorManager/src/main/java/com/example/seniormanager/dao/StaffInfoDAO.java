package com.example.seniormanager.dao;

import com.example.seniormanager.model.StaffInfo;
import com.example.seniormanager.model.StaffOption;
import com.example.seniormanager.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StaffInfoDAO
{
    public static List<StaffInfo> getAllStaff(){
        List<StaffInfo> list = new ArrayList<>();
        String sql = "select s.StaffId, s.StaffCode, s.Role, s.FullName, s.DoB, s.Email, s.Phone\n" +
                "from Staff s where role = \"librarian\" ";

        try(
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ){
            while (rs.next()){
                StaffInfo sf = new StaffInfo();
                sf.setStaffId(rs.getInt("StaffId"));
                sf.setStaffCode(rs.getString("StaffCode"));
                sf.setFullName(rs.getString("FullName"));
                sf.setEmail(rs.getString("Email"));
                sf.setPhoneNumber(rs.getString("Phone"));
                sf.setDoB(rs.getDate("DoB").toLocalDate());
                sf.setRole(rs.getString("Role"));

                list.add(sf);

            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean insertStaff(String fullName, String email, LocalDate DoB, String phone, String role){
        String sql = "insert into Staff(StaffCode, FullName, Email, DoB, Phone, Role) values (?, ?, ? ,? ,? ,?)";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ){
            String randomStaffCode = "NV" + UUID.randomUUID().toString().substring(0, 2).toUpperCase();
            ps.setString(1, randomStaffCode);
            ps.setString(2, fullName);
            ps.setString(3, email);
            ps.setDate(4, Date.valueOf(DoB));
            ps.setString(5, phone);
            ps.setString(6, role);

            int rowAffected = ps.executeUpdate();
            if(rowAffected >  0) return true;

        }catch (SQLException e){
            e.printStackTrace();
        }
        return  false;
    }

    public static boolean updateStaff(int staffId, String fullName, String email, LocalDate dob, String phone){
        String sql = "update Staff set FullName = ?, Email = ?, DoB = ?, Phone = ? where StaffId = ?";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ){
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setDate(3, Date.valueOf(dob));
            ps.setString(4, phone);
            ps.setInt(5, staffId);

            int rowAffected = ps.executeUpdate();
            return rowAffected > 0;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public static List<StaffOption> getLibrarianOptions() {
        List<StaffOption> list = new ArrayList<>();
        // Chỉ lấy những nhân viên có Role là Librarian
        String sql = "SELECT StaffId, StaffCode, FullName FROM Staff WHERE Role = 'Librarian'";

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
}
