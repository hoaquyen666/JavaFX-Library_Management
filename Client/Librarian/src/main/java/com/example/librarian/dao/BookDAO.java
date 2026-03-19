package com.example.librarian.dao;

import com.example.librarian.model.Book;
import com.example.librarian.util.DatabaseConnection;
import com.example.librarian.model.CategoryOption;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class BookDAO {

    // Hàm lấy toàn bộ danh sách Book liên quan
    public List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        String query = """
            SELECT b.BookId, b.BookCode, b.Title, b.ISBN, b.Publisher, b.PublishYear, b.Price,
                   GROUP_CONCAT(DISTINCT a.AuthorName SEPARATOR ', ') AS AuthorNames,
                   GROUP_CONCAT(DISTINCT c.CategoryName SEPARATOR ', ') AS CategoryNames
            FROM Book b
            LEFT JOIN BookAuthor ba ON b.BookId = ba.BookId
            LEFT JOIN Author a ON ba.AuthorId = a.AuthorId
            LEFT JOIN BookCategory bc ON b.BookId = bc.BookId
            LEFT JOIN Category c ON bc.CategoryId = c.CategoryId
            GROUP BY b.BookId
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                bookList.add(new Book(
                        rs.getInt("BookId"),
                        rs.getString("BookCode"),
                        rs.getString("Title"),
                        rs.getString("AuthorNames"),
                        rs.getString("CategoryNames"),
                        rs.getString("ISBN"),
                        rs.getString("Publisher"),
                        rs.getInt("PublishYear"),
                        rs.getInt("Price")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return bookList;
    }

    //Hàm xoá sách
    public boolean deleteBook(int bookId) {
        String query = "DELETE FROM Book WHERE BookId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, bookId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Lỗi xóa sách (Có thể do dính khóa ngoại): " + e.getMessage());
            return false;
        }
    }

    //Hàm thêm sách mới vào DB
    public boolean insertBook(Book book, int categoryId) {
        String query = "INSERT INTO Book (BookCode, Title, ISBN, Publisher, PublishYear, Price) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             // Chú ý: Cần RETURN_GENERATED_KEYS để lấy ID sách vừa tạo
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, book.getBookCode());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getISBN());
            pstmt.setString(4, book.getPublisher());
            pstmt.setInt(5, book.getPublishYear());
            pstmt.setInt(6, book.getPrice());

            if (pstmt.executeUpdate() > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int newBookId = rs.getInt(1);
                    handleBookAuthors(conn, newBookId, book.getAuthor()); // Giữ nguyên hàm xử lý Tác giả

                    // LƯU TRỰC TIẾP ID THỂ LOẠI VÀO BẢNG TRUNG GIAN
                    try (PreparedStatement psCat = conn.prepareStatement("INSERT INTO BookCategory (BookId, CategoryId) VALUES (?, ?)")) {
                        psCat.setInt(1, newBookId);
                        psCat.setInt(2, categoryId);
                        psCat.executeUpdate();
                    }
                }
                return true;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }


    //Hàm sửa sách
    public boolean updateBook(Book book, int categoryId) {
        String query = "UPDATE Book SET BookCode = ?, Title = ?, ISBN = ?, Publisher = ?, PublishYear = ?, Price = ? WHERE BookId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, book.getBookCode());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getISBN());
            pstmt.setString(4, book.getPublisher());
            pstmt.setInt(5, book.getPublishYear());
            pstmt.setInt(6, book.getPrice());
            pstmt.setInt(7, book.getBookID());

            if (pstmt.executeUpdate() > 0) {
                // Xóa các liên kết cũ
                try(PreparedStatement ps1 = conn.prepareStatement("DELETE FROM BookAuthor WHERE BookId = ?");
                    PreparedStatement ps2 = conn.prepareStatement("DELETE FROM BookCategory WHERE BookId = ?")) {
                    ps1.setInt(1, book.getBookID()); ps1.executeUpdate();
                    ps2.setInt(1, book.getBookID()); ps2.executeUpdate();
                }

                handleBookAuthors(conn, book.getBookID(), book.getAuthor()); // Vẫn xử lý tác giả bằng chữ

                // INSERT LẠI THỂ LOẠI MỚI
                try (PreparedStatement psCat = conn.prepareStatement("INSERT INTO BookCategory (BookId, CategoryId) VALUES (?, ?)")) {
                    psCat.setInt(1, book.getBookID());
                    psCat.setInt(2, categoryId);
                    psCat.executeUpdate();
                }
                return true;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // Hàm tạo thêm 1 bản sao vật lý cho sách
    public boolean insertBookCopy(int bookId) {
        // Tự động tạo mã vạch (Barcode) theo công thức: BC-ID-Random (VD: BC-3-9823)
        String copyCode = "BC-" + bookId + "-" + (System.currentTimeMillis() % 10000);

        // Mặc định Status là 'Available' theo đúng cấu trúc Database
        String query = "INSERT INTO BookCopy (CopyCode, BookId, Status) VALUES (?, ?, 'Available')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, copyCode);
            pstmt.setInt(2, bookId);

            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Lỗi khi thêm bản sao sách: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Integer> countBooksByCategory() {

        Map<String, Integer> result = new HashMap<>();

        String sql = """
        SELECT c.CategoryName, COUNT(bc.BookId) AS total
        FROM Category c
        LEFT JOIN BookCategory bc ON c.CategoryId = bc.CategoryId
        GROUP BY c.CategoryName
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.put(
                        rs.getString("CategoryName"),
                        rs.getInt("total")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public int countBooks(){

        String sql = "SELECT COUNT(*) FROM Book";

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


    //Hàm lấy dữ liệu thể loại vào combobox
    public List<CategoryOption> getCategoryOptions() {
        List<CategoryOption> list = new ArrayList<>();
        String sql = "SELECT c.CategoryId, c.CategoryName, cg.CategoryGroupName " +
                "FROM Category c " +
                "LEFT JOIN CategoryGroup cg ON c.CategoryGroupId = cg.CategoryGroupId";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new CategoryOption(
                        rs.getInt("CategoryId"),
                        rs.getString("CategoryName"),
                        rs.getString("CategoryGroupName") != null ? rs.getString("CategoryGroupName") : "Khác"
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // CÁC HÀM HỖ TRỢ XỬ LÝ CHUỖI TÁC GIẢ & THỂ LOẠI (Cắt theo dấu phẩy)
    // =========================================================================
    private void handleBookAuthors(Connection conn, int bookId, String authorsStr) throws SQLException {
        if (authorsStr == null || authorsStr.trim().isEmpty()) return;
        String[] authors = authorsStr.split(",");
        for (String authorName : authors) {
            authorName = authorName.trim();
            if (authorName.isEmpty()) continue;

            // Kiểm tra tác giả có chưa, chưa thì tự tạo mới
            int authorId = -1;
            String checkSql = "SELECT AuthorId FROM Author WHERE AuthorName = ?";
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setString(1, authorName);
                ResultSet rsCheck = psCheck.executeQuery();
                if (rsCheck.next()) authorId = rsCheck.getInt("AuthorId");
            }
            if (authorId == -1) {
                String insSql = "INSERT INTO Author (AuthorCode, AuthorName) VALUES (?, ?)";
                try (PreparedStatement psIns = conn.prepareStatement(insSql, Statement.RETURN_GENERATED_KEYS)) {
                    psIns.setString(1, "A-" + (System.currentTimeMillis() % 100000));
                    psIns.setString(2, authorName);
                    psIns.executeUpdate();
                    ResultSet rsIns = psIns.getGeneratedKeys();
                    if (rsIns.next()) authorId = rsIns.getInt(1);
                }
            }
            // Liên kết vào BookAuthor
            if (authorId != -1) {
                try (PreparedStatement psLink = conn.prepareStatement("INSERT IGNORE INTO BookAuthor (BookId, AuthorId) VALUES (?, ?)")) {
                    psLink.setInt(1, bookId);
                    psLink.setInt(2, authorId);
                    psLink.executeUpdate();
                }
            }
        }
    }



}
