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

    // =========================
    // THÊM READER
    // =========================
    public void addReader(ReaderRecord reader) {

        String sql = """
                INSERT INTO Reader (ReaderCode, FullName, Phone, Email, Status)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, reader.getReaderCode());
            ps.setString(2, reader.getFullName());
            ps.setString(3, reader.getPhone());
            ps.setString(4, reader.getEmail());
            ps.setString(5, reader.getStatus());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // =========================
    // UPDATE READER
    // =========================
    public void updateReader(ReaderRecord reader) {

        String sql = """
                UPDATE Reader
                SET FullName = ?, Phone = ?, Email = ?, Status = ?
                WHERE ReaderCode = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, reader.getFullName());
            ps.setString(2, reader.getPhone());
            ps.setString(3, reader.getEmail());
            ps.setString(4, reader.getStatus());
            ps.setString(5, reader.getReaderCode());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetPassword(String username){

        String sql = """
            UPDATE Account
            SET PasswordHash = '123456'
            WHERE Username = ?
            """;

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setString(1, username);
            ps.executeUpdate();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
