package com.example.seniormanager.dao;

import com.example.seniormanager.model.ShiftAssignment;
import com.example.seniormanager.model.ShiftOption;
import com.example.seniormanager.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShiftDAO {
    //Lấy tất cả ca làm
    public static List<ShiftOption> getAllShifts(){
        List<ShiftOption> list = new ArrayList<>();
        String sql = "select * from shift";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ){
            //duyệt
            while (rs.next()){
                list.add(new ShiftOption(
                        rs.getInt("ShiftId"),
                        rs.getString("ShiftName"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time")
                ));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    //Thêm ca làm
    public static boolean assignShift(int librarianId, int shiftId, LocalDate workDate, int assignedBy) {
        String sql = "INSERT INTO librarian_shift (Librarian_id, ShiftId, Work_date, Assigned_by) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, librarianId);
            ps.setInt(2, shiftId);
            ps.setDate(3, Date.valueOf(workDate));
            ps.setInt(4, assignedBy); // ID của Senior đang đăng nhập
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    //Cập nhật ca làm và điểm danh
    public static boolean updateShift(int assignmentId, int newShiftId, boolean isAttended) {
        String sql = "UPDATE librarian_shift SET ShiftId = ?, Attendance_Status = ? WHERE Assignment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newShiftId);
            ps.setBoolean(2, isAttended);
            ps.setInt(3, assignmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    //Lấy danh sách ca làm theo tháng
    public static List<ShiftAssignment> getShiftsByMonth(int librarianId, int month, int year) {
        List<ShiftAssignment> list = new ArrayList<>();
        // Kết nối bảng librarian_shift với shift để lấy thêm Tên Ca Làm
        String sql = "SELECT ls.Assignment_id, ls.Librarian_id, ls.ShiftId, s.ShiftName, s.start_time, s.end_time, ls.Work_date, ls.Attendance_Status " +
                "FROM librarian_shift ls " +
                "JOIN shift s ON ls.ShiftId = s.ShiftId " +
                "WHERE ls.Librarian_id = ? AND MONTH(ls.Work_date) = ? AND YEAR(ls.Work_date) = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, librarianId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new ShiftAssignment(
                        rs.getInt("Assignment_id"),
                        rs.getInt("Librarian_id"),
                        rs.getInt("ShiftId"),
                        rs.getString("ShiftName"),
                        rs.getDate("Work_date").toLocalDate(),
                        rs.getBoolean("Attendance_Status"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
