package com.example.librarian.dao;

import com.example.librarian.model.ReaderRecord;
import com.example.librarian.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReaderDAO {

    public List<ReaderRecord> findAllReaders() {
        List<ReaderRecord> readers = new ArrayList<>();
        String sql = """
                SELECT r.ReaderCode,
                       COALESCE(a.Username, '') AS Username,
                       r.FullName,
                       r.Phone,
                       r.Email,
                       r.Status
                FROM Reader r
                LEFT JOIN Account a ON a.ReaderId = r.ReaderId AND a.Role = 'Reader'
                ORDER BY r.ReaderId
                """;
        //lấy dữ liệu từ bảng reader
        try (Connection connection = DatabaseConnection.getConnection(); //tạo lệnh
             PreparedStatement ps = connection.prepareStatement(sql); // tránh sqlinjection
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                readers.add(new ReaderRecord(
                        rs.getString("ReaderCode"),
                        rs.getString("Username"),
                        rs.getString("FullName"),
                        rs.getString("Phone"),
                        rs.getString("Email"),
                        rs.getString("Status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return readers;
    }
}
