package com.example.librarian.dao;

import com.example.librarian.model.Borrow;
import com.example.librarian.util.DatabaseConnection;
import com.example.librarian.model.ReaderStat;
import com.example.librarian.model.BookBorrowStat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
public class BorrowDAO {

    public List<Borrow> getAllBorrows() {
        List<Borrow> borrowList = new ArrayList<>();


        String query = "SELECT " +
                "    b.BorrowId, " +
                "    b.BorrowCode, " +
                "    r.FullName AS ReaderCode, " +
                "    s.FullName AS StaffCode, " +
                "    b.BorrowDate, " +
                "    b.DueDate, " +
                "    b.Status, " +
                "    COUNT(bd.BorrowDetailId) AS Quantity " +
                "FROM Borrow b " +
                "JOIN Reader r ON b.ReaderId = r.ReaderId " +
                "JOIN Staff s ON b.StaffId = s.StaffId " +
                "LEFT JOIN BorrowDetail bd ON b.BorrowId = bd.BorrowId " +
                "GROUP BY " +
                "    b.BorrowId, b.BorrowCode, r.FullName, s.FullName, b.BorrowDate, b.DueDate, b.Status";

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

    // Hàm lấy danh sách tên các cuốn sách trong 1 phiếu mượn
    public List<String> getBorrowedBooks(int borrowId) {
        List<String> books = new ArrayList<>();
        String query = "SELECT bc.CopyId, bk.Title, bd.Status " +
                "FROM BorrowDetail bd " +
                "JOIN BookCopy bc ON bd.CopyId = bc.CopyId " +
                "JOIN Book bk ON bc.BookId = bk.BookId " +
                "WHERE bd.BorrowId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, borrowId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("Status").equals("Returned") ? "Đã trả" : "Đang mượn";
                    books.add("CopyID: " + rs.getInt("CopyId") + " | Sách: " + rs.getString("Title") + " | " + status);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return books;
    }

    // Lấy danh sách Độc giả để nhét vào ComboBox
    public List<String> getReaderListForCombo() {
        List<String> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT ReaderId, FullName FROM Reader");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getInt("ReaderId") + " - " + rs.getString("FullName"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Lấy danh sách Nhân viên để nhét vào ComboBox
    public List<String> getStaffListForCombo() {
        List<String> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT StaffId, FullName FROM Staff");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getInt("StaffId") + " - " + rs.getString("FullName"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Hàm Insert (Dùng trực tiếp ID số nguyên thay vì Sub-query tìm Mã chữ)
    public boolean insertBorrowWithDetailsById(Borrow borrow, int readerId, int staffId, List<Integer> copyIds) {
        String insertBorrowQuery = "INSERT INTO Borrow (BorrowCode, ReaderId, StaffId, DueDate, Status) VALUES (?, ?, ?, ?, 'Borrowing')";
        String insertDetailQuery = "INSERT INTO BorrowDetail (BorrowId, CopyId, Status) VALUES (?, ?, 'Borrowing')";
        String updateCopyQuery = "UPDATE BookCopy SET Status = 'Borrowed' WHERE CopyId = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            int newBorrowId = -1;

            try (PreparedStatement pstmtBorrow = conn.prepareStatement(insertBorrowQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmtBorrow.setString(1, borrow.getBorrowCode());
                pstmtBorrow.setInt(2, readerId);
                pstmtBorrow.setInt(3, staffId);
                pstmtBorrow.setString(4, borrow.getDueDate());
                if (pstmtBorrow.executeUpdate() == 0) throw new Exception("Lỗi tạo phiếu!");

                try (ResultSet generatedKeys = pstmtBorrow.getGeneratedKeys()) {
                    if (generatedKeys.next()) newBorrowId = generatedKeys.getInt(1);
                }
            }
            try (PreparedStatement pstmtDetail = conn.prepareStatement(insertDetailQuery);
                 PreparedStatement pstmtUpdateCopy = conn.prepareStatement(updateCopyQuery)) {
                for (Integer copyId : copyIds) {
                    pstmtDetail.setInt(1, newBorrowId);
                    pstmtDetail.setInt(2, copyId);
                    pstmtDetail.addBatch();

                    pstmtUpdateCopy.setInt(1, copyId);
                    pstmtUpdateCopy.addBatch();
                }
                pstmtDetail.executeBatch();
                pstmtUpdateCopy.executeBatch();
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
    // Hàm kiểm tra xem danh sách sách có đủ điều kiện để mượn không
    public String checkCopiesAvailable(List<Integer> copyIds) {
        if (copyIds == null || copyIds.isEmpty()) return "Chưa nhập ID sách.";

        try (Connection conn = DatabaseConnection.getConnection()) {
            for (Integer id : copyIds) {
                String query = "SELECT Status FROM BookCopy WHERE CopyId = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setInt(1, id);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            String status = rs.getString("Status");
                            // Nếu sách không khả dụng (Ví dụ: Borrowed, Lost...)
                            if (!"Available".equalsIgnoreCase(status)) {
                                return "Bản sao ID " + id + " đang ở trạng thái '" + status + "', không thể mượn!";
                            }
                        } else {
                            return "Bản sao ID " + id + " không tồn tại trong kho!";
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi CSDL khi kiểm tra tình trạng sách.";
        }
        return "OK"; // Trả về OK nếu tất cả sách đều rảnh
    }

    public Map<String, Integer> countBorrowByMonth() {

        Map<String, Integer> result = new LinkedHashMap<>();

        String sql = """
        SELECT MONTH(BorrowDate) AS month, COUNT(*) AS total
        FROM Borrow
        GROUP BY MONTH(BorrowDate)
        ORDER BY month
    """;

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while(rs.next()){
                result.put(
                        "T" + rs.getInt("month"),
                        rs.getInt("total")
                );
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public List<BookBorrowStat> getTopBorrowedBooks(){

        List<BookBorrowStat> list = new ArrayList<>();

        String sql = """
        SELECT bk.Title, COUNT(*) AS total
        FROM BorrowDetail bd
        JOIN BookCopy bc ON bd.CopyId = bc.CopyId
        JOIN Book bk ON bc.BookId = bk.BookId
        GROUP BY bk.Title
        ORDER BY total DESC
        LIMIT 5
    """;

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while(rs.next()){
                list.add(new BookBorrowStat(
                        rs.getString("Title"),
                        rs.getInt("total")
                ));
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

    public List<ReaderStat> getTopReaders(){

        List<ReaderStat> list = new ArrayList<>();

        String sql = """
        SELECT r.FullName, COUNT(b.BorrowId) AS total
        FROM Reader r
        JOIN Borrow b ON r.ReaderId = b.ReaderId
        GROUP BY r.ReaderId
        ORDER BY total DESC
        LIMIT 5
    """;

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while(rs.next()){
                list.add(new ReaderStat(
                        rs.getString("FullName"),
                        rs.getInt("total")
                ));
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

    public int countReaders(){

        String sql = "SELECT COUNT(*) FROM Reader";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            if(rs.next()){
                return rs.getInt(1);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return 0;
    }

    public int countBorrowing(){

        String sql = "SELECT COUNT(*) FROM BorrowDetail WHERE Status='Borrowing'";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            if(rs.next()){
                return rs.getInt(1);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return 0;
    }

    public int countOverdue(){

        String sql = """
        SELECT COUNT(*)
        FROM Borrow
        WHERE Status='Borrowing' AND DueDate < NOW()
    """;

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            if(rs.next()){
                return rs.getInt(1);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return 0;
    }
}
