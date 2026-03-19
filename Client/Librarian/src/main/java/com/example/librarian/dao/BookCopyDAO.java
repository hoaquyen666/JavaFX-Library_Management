package com.example.librarian.dao;

import com.example.librarian.model.BookCopy;
import com.example.librarian.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookCopyDAO {

    public List<BookCopy> getAllBookCopies(){
        List<BookCopy> list = new ArrayList<>();

        String sql = "select bc.CopyId, b.bookId, b.Title, bc.Status, bc.Location, bc.Note\n" +
                "from BookCopy bc\n" +
                "join Book b ON bc.BookId = b.BookId \n" +
                "order by bc.CopyId";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ){
            while (rs.next()){
                list.add(new BookCopy(
                        rs.getInt("CopyId"),
                        rs.getInt("BookId"),
                        rs.getString("Title"),
                        rs.getString("Status"),
                        rs.getString("Location"),
                        rs.getString("Note")
                ));
            }

        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("có lỗi khi kết nối database");
        }
        return  list;
    }

    public boolean updateBookCopy(BookCopy copy) {
        String sql = "UPDATE BookCopy SET Status = ?, Location = ?, Note = ? WHERE CopyId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, copy.getStatus());
            ps.setString(2, copy.getLocation());
            ps.setString(3, copy.getNote());
            ps.setInt(4, copy.getCopyId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
