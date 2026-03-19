package com.example.librarian.dao;

import com.example.librarian.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.example.librarian.model.ReturnDetailDTO;

public class ReturnDAO {

    // Lấy danh sách CopyId của một phiếu mượn
//    public List<Integer> getCopyIdsByBorrowId(int borrowId) {
//        List<Integer> ids = new ArrayList<>();
//        String query = "SELECT CopyId FROM BorrowDetail WHERE BorrowId = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setInt(1, borrowId);
//            ResultSet rs = pstmt.executeQuery();
//            while (rs.next()) ids.add(rs.getInt("CopyId"));
//        } catch (Exception e) { e.printStackTrace(); }
//        return ids;
//    }
//
//    // Thực hiện trả sách: Cập nhật Borrow và BookCopy
//    public boolean processReturn(int borrowId, List<Integer> copyIds, String finalStatus) {
//        String updateBorrow = "UPDATE Borrow SET Status = ? WHERE BorrowId = ?";
//        String updateCopy = "UPDATE BookCopy SET Status = 'Available' WHERE CopyId = ?";
//
//        Connection conn = null;
//        try {
//            conn = DatabaseConnection.getConnection();
//            conn.setAutoCommit(false);
//
//            try (PreparedStatement ps1 = conn.prepareStatement(updateBorrow)) {
//                ps1.setString(1, finalStatus);
//                ps1.setInt(2, borrowId);
//                ps1.executeUpdate();
//            }
//
//            try (PreparedStatement ps2 = conn.prepareStatement(updateCopy)) {
//                for (Integer id : copyIds) {
//                    ps2.setInt(1, id);
//                    ps2.addBatch();
//                }
//                ps2.executeBatch();
//            }
//
//            conn.commit();
//            return true;
//        } catch (Exception e) {
//            if (conn != null) try { conn.rollback(); } catch (Exception ex) {}
//            return false;
//        } finally {
//            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (Exception e) {}
//        }
//    }

    //Lấy ra danh sách mượn chi tiết
    public List<ReturnDetailDTO> getPendingReturns() {
        List<ReturnDetailDTO> list = new ArrayList<>();

        String sql = """
            SELECT 
                b.BorrowId, r.ReaderId, r.FullName, 
                bc.CopyId, bc.CopyCode, bk.Title, 
                bd.DepositAmount, bd.FineAmount, bd.Status, b.DueDate
            FROM BorrowDetail bd
            JOIN Borrow b ON bd.BorrowId = b.BorrowId
            JOIN Reader r ON b.ReaderId = r.ReaderId
            JOIN BookCopy bc ON bd.CopyId = bc.CopyId
            JOIN Book bk ON bc.BookId = bk.BookId
            WHERE bd.Status IN ('Borrowing', 'Overdue')
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LocalDate dueDate = null;
                if (rs.getTimestamp("DueDate") != null) {
                    dueDate = rs.getTimestamp("DueDate").toLocalDateTime().toLocalDate();
                }

                list.add(new ReturnDetailDTO(
                        rs.getInt("BorrowId"), rs.getInt("ReaderId"), rs.getString("FullName"),
                        rs.getInt("CopyId"), rs.getString("CopyCode"), rs.getString("Title"),
                        rs.getDouble("DepositAmount"), rs.getDouble("FineAmount"),
                        rs.getString("Status"), dueDate
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Trả từng cuốn vật lý một
    public boolean processSingleReturn(int borrowId, int copyId, double fineAmount, String detailStatus) {
        // 1. Cập nhật chi tiết mượn (BorrowDetail)
        String updateDetail = "UPDATE BorrowDetail SET ReturnDate = NOW(), FineAmount = ?, Status = ? WHERE BorrowId = ? AND CopyId = ?";
        // 2. Cập nhật sách vật lý về rảnh (Available)
        String updateCopy = "UPDATE BookCopy SET Status = 'Available' WHERE CopyId = ?";
        // 3. (Tùy chọn) Có thể viết thêm SQL check xem nếu TẤT CẢ BorrowDetail đã trả, thì Update bảng Borrow thành 'Returned'

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(updateDetail)) {
                ps1.setDouble(1, fineAmount);
                ps1.setString(2, detailStatus);
                ps1.setInt(3, borrowId);
                ps1.setInt(4, copyId);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = conn.prepareStatement(updateCopy)) {
                ps2.setInt(1, copyId);
                ps2.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (Exception e) {}
        }
    }
}

