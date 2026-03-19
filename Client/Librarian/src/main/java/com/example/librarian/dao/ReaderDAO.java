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
        // Danh sách kết quả sau khi đọc từ DB.
        List<ReaderRecord> readers = new ArrayList<>();

        // Query này đọc dữ liệu ở bảng Reader và nối thêm bảng Account để lấy Username.
        // Dùng LEFT JOIN vì có thể có độc giả tồn tại nhưng chưa có tài khoản đăng nhập.
        // COALESCE(a.Username, '') giúp tránh null ở cột Username.
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

        // try-with-resources giúp tự đóng Connection / PreparedStatement / ResultSet.
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Mỗi dòng DB được map sang một ReaderRecord.
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
            // Hiện tại lỗi chỉ được in ra console.
            // Chưa có log framework hoặc exception handling tập trung.
            e.printStackTrace();
        }

        return readers;
    }

    public void addReader(ReaderRecord reader) {
        // Hàm này chỉ thêm vào bảng Reader.
        // Nó chưa tạo tài khoản tương ứng ở bảng Account.
        String sql = """
                INSERT INTO Reader (ReaderCode, FullName, Phone, Email, Status)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            // Gán dữ liệu vào từng dấu ? theo đúng thứ tự trong SQL.
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

    public void updateReader(ReaderRecord reader) {
        // Cập nhật theo ReaderCode.
        // Nghĩa là ReaderCode đang được xem là mã định danh ổn định ở tầng ứng dụng.
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

    public void resetPassword(String username) {
        // Reset mật khẩu trực tiếp ở bảng Account theo Username.
        // Lưu ý:
        // - đang set cứng chuỗi '123456',
        // - cột tên là PasswordHash nhưng giá trị đưa vào lại là plain text.
        // Điều này ổn để demo nội bộ nhưng không an toàn nếu triển khai thật.
        String sql = """
            UPDATE Account
            SET PasswordHash = '123456'
            WHERE Username = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
