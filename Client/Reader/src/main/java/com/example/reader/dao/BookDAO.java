package com.example.reader.dao;

import com.example.reader.model.Book;
import com.example.reader.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public List<Book> searchBooks(String type, String keyword) {
        List<Book> list = new ArrayList<>();
        String column = "Title"; // Mặc định

        if ("Tác Giả".equals(type)) column = "AuthorName";
        else if ("Thể loại".equals(type)) column = "CategoryName";

        String sql = "SELECT \n" +
                "\tb.BookId,\n" +
                "\tb.Title,\n" +
                "    b.ISBN,\n" +
                "    b.Publisher,\n" +
                "    b.PublishYear,\n" +
                "    a.AuthorName,\n" +
                "    c.CategoryName,\n" +
                "    cg.CategoryGroupName\n" +
                "FROM Book b\n" +
                "LEFT JOIN BookAuthor ba ON b.BookId = ba.BookId\n" +
                "LEFT JOIN Author a ON ba.AuthorId = a.AuthorId\n" +
                "-- Kết nối với bảng Thể loại\n" +
                "LEFT JOIN BookCategory bc ON b.BookId = bc.BookId\n" +
                "LEFT JOIN Category c ON bc.CategoryId = c.CategoryId\n" +
                "LEFT JOIN CategoryGroup cg ON cg.CategoryGroupId = c.CategoryGroupId WHERE " + column + " LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("BookId"),
                        rs.getString("Title"),
                        rs.getString("ISBN"),
                        rs.getString("Publisher"),
                        rs.getInt("PublishYear"),
                        rs.getString("AuthorName"),
                        rs.getString("CategoryName"),
                        rs.getString("CategoryGroupName")
                );
                list.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}