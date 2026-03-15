package com.example.librarian.dao;

import com.example.librarian.model.Book;
import com.example.librarian.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // Hàm lấy toàn bộ danh sách Book
    public List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        String query = "SELECT * FROM Book";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("BookId"),
                        rs.getString("BookCode"),
                        rs.getString("Title"),
                        rs.getString("ISBN"),
                        rs.getString("Publisher"),
                        rs.getInt("PublishYear")
                );
                bookList.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public boolean insertBook(Book book) {
        String query = "INSERT INTO Book (BookCode, Title, ISBN, Publisher, PublishYear) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Truyền dữ liệu từ object Book vào câu lệnh SQL
            pstmt.setString(1, book.getBookCode());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getISBN());
            pstmt.setString(4, book.getPublisher());
            pstmt.setInt(5, book.getPublishYear());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Lỗi khi thêm sách mới: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    //Hàm sửa sách
    public boolean updateBook(Book book) {
        String query = "UPDATE Book SET BookCode = ?, Title = ?, ISBN = ?, Publisher = ?, PublishYear = ? WHERE BookId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, book.getBookCode());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getISBN());
            pstmt.setString(4, book.getPublisher());
            pstmt.setInt(5, book.getPublishYear());
            // Điều kiện WHERE
            pstmt.setInt(6, book.getBookID());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật sách: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
}
