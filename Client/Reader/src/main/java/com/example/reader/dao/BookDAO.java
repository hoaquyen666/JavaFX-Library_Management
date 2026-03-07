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

        if ("Mã sách".equals(type)) column = "BookCode";
        else if ("ISBN".equals(type)) column = "ISBN";

        String sql = "SELECT BookCode, Title, ISBN FROM Book WHERE " + column + " LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                        rs.getString("BookCode"),
                        rs.getString("Title"),
                        rs.getString("ISBN")
                );
                list.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}