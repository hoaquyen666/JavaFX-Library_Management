package com.example.librarian.dao;

import com.example.librarian.model.Borrow;
import com.example.librarian.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {

    public List<Borrow> getAllBorrows() {
        List<Borrow> borrowList = new ArrayList<>();

        String query = "SELECT " +
                "    b.BorrowId, " +
                "    b.BorrowCode, " +
                "    r.ReaderCode, " +
                "    s.StaffCode, " +
                "    b.BorrowDate, " +
                "    b.DueDate, " +
                "    b.Status, " +
                "    COUNT(bd.BorrowDetailId) AS Quantity " + // Đếm số dòng trong chi tiết
                "FROM Borrow b " +
                "JOIN Reader r ON b.ReaderId = r.ReaderId " +
                "JOIN Staff s ON b.StaffId = s.StaffId " +
                "LEFT JOIN BorrowDetail bd ON b.BorrowId = bd.BorrowId " +
                "GROUP BY " +
                "    b.BorrowId, b.BorrowCode, r.ReaderCode, s.StaffCode, b.BorrowDate, b.DueDate, b.Status";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            while (rs.next()) {
                // Chuyển đổi Timestamp từ DB sang String
                String borrowDateStr = rs.getTimestamp("BorrowDate") != null ?
                        sdf.format(rs.getTimestamp("BorrowDate")) : "";
                String dueDateStr = rs.getTimestamp("DueDate") != null ?
                        sdf.format(rs.getTimestamp("DueDate")) : "";

                Borrow borrow = new Borrow(
                        rs.getInt("BorrowId"),
                        rs.getString("BorrowCode"),
                        rs.getString("ReaderCode"),
                        rs.getString("StaffCode"),
                        borrowDateStr,
                        dueDateStr,
                        rs.getInt("Quantity"), // Lấy con số đã đếm được
                        rs.getString("Status")
                );
                borrowList.add(borrow);
            }
        } catch (Exception e) {
            System.err.println("Lỗi load danh sách mượn: " + e.getMessage());
            e.printStackTrace();
        }
        return borrowList;
    }
    // Hàm Thêm phiếu mượn VÀ Chi tiết phiếu mượn
    public boolean insertBorrowWithDetails(Borrow borrow, List<String> barcodes) {
        // #1: Thêm Phiếu mượn (Tự động tra cứu ID từ Mã chữ)
        String insertBorrowQuery = "INSERT INTO Borrow (BorrowCode, ReaderId, StaffId, DueDate, Status) " +
                "VALUES (?, (SELECT ReaderId FROM Reader WHERE ReaderCode = ?), " +
                "(SELECT StaffId FROM Staff WHERE StaffCode = ?), ?, 'Borrowing')";

        // #2: Thêm Chi tiết mượn sách
        String insertDetailQuery = "INSERT INTO BorrowDetail (BorrowId, CopyId, Status) " +
                "VALUES (?, (SELECT CopyId FROM BookCopy WHERE CopyCode = ?), 'Borrowing')";

        // #3: Cập nhật lại kho sách
        String updateCopyQuery = "UPDATE BookCopy SET Status = 'Borrowed' WHERE CopyCode = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int newBorrowId = -1;

            //Chạy lệnh tạo Phiếu mượn trước
            try (PreparedStatement pstmtBorrow = conn.prepareStatement(insertBorrowQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmtBorrow.setString(1, borrow.getBorrowCode());
                pstmtBorrow.setString(2, borrow.getReaderCode());
                pstmtBorrow.setString(3, borrow.getStaffCode());
                pstmtBorrow.setString(4, borrow.getDueDate()); // Giờ hẹn trả

                if (pstmtBorrow.executeUpdate() == 0) throw new Exception("Không tạo được phiếu mượn!");

                // Lấy cái BorrowId vừa được Database tự động sinh ra
                try (ResultSet generatedKeys = pstmtBorrow.getGeneratedKeys()) {
                    if (generatedKeys.next()) newBorrowId = generatedKeys.getInt(1);
                    else throw new Exception("Không lấy được ID của phiếu mượn!");
                }
            }

            // Chạy vòng lặp thêm từng cuốn sách vào Chi tiết và cập nhật kho
            try (PreparedStatement pstmtDetail = conn.prepareStatement(insertDetailQuery);
                 PreparedStatement pstmtUpdateCopy = conn.prepareStatement(updateCopyQuery)) {

                for (String barcode : barcodes) {
                    String cleanBarcode = barcode.trim(); // Xóa khoảng trắng thừa

                    // Add batch cho Detail
                    pstmtDetail.setInt(1, newBorrowId);
                    pstmtDetail.setString(2, cleanBarcode);
                    pstmtDetail.addBatch();

                    // Add batch cho Kho
                    pstmtUpdateCopy.setString(1, cleanBarcode);
                    pstmtUpdateCopy.addBatch();
                }
                pstmtDetail.executeBatch();
                pstmtUpdateCopy.executeBatch();
            }

            conn.commit(); // CHECKING VÀ LƯU VÀO DATABASE
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Đã Rollback an toàn do lỗi: " + e.getMessage());
                } catch (Exception ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); }
                catch (Exception e) { e.printStackTrace(); }
            }
        }
    }
    // Hàm Cập nhật thông tin phiếu mượn (Chỉ cập nhật thông tin chung, không đụng tới sách)
    public boolean updateBorrow(Borrow borrow) {
        String query = "UPDATE Borrow SET " +
                "ReaderId = (SELECT ReaderId FROM Reader WHERE ReaderCode = ?), " +
                "StaffId = (SELECT StaffId FROM Staff WHERE StaffCode = ?), " +
                "DueDate = ? " +
                "WHERE BorrowId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, borrow.getReaderCode());
            pstmt.setString(2, borrow.getStaffCode());
            pstmt.setString(3, borrow.getDueDate());
            pstmt.setInt(4, borrow.getBorrowId());

            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật phiếu mượn: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
