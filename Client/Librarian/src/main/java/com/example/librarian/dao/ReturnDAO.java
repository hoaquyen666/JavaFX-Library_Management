package com.example.librarian.dao;

import com.example.librarian.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReturnDAO {

    // Lấy danh sách CopyId của một phiếu mượn
    public List<Integer> getCopyIdsByBorrowId(int borrowId) {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT CopyId FROM BorrowDetail WHERE BorrowId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, borrowId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) ids.add(rs.getInt("CopyId"));
        } catch (Exception e) { e.printStackTrace(); }
        return ids;
    }

    // Thực hiện trả sách: Cập nhật Borrow và BookCopy
    public boolean processReturn(int borrowId, List<Integer> copyIds, String finalStatus) {
        String updateBorrow = "UPDATE Borrow SET Status = ? WHERE BorrowId = ?";
        String updateCopy = "UPDATE BookCopy SET Status = 'Available' WHERE CopyId = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(updateBorrow)) {
                ps1.setString(1, finalStatus);
                ps1.setInt(2, borrowId);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = conn.prepareStatement(updateCopy)) {
                for (Integer id : copyIds) {
                    ps2.setInt(1, id);
                    ps2.addBatch();
                }
                ps2.executeBatch();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (Exception ex) {}
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (Exception e) {}
        }
    }
}

