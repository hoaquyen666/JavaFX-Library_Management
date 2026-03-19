package com.example.librarian.model;

public class ReaderRecord {

    // Đây là model dữ liệu "mỏng" của độc giả.
    // Nó chủ yếu dùng để:
    // 1. nhận dữ liệu từ DAO sau khi query DB,
    // 2. chuyển dữ liệu từ controller xuống DAO khi insert/update.
    private String readerCode;
    private String username;
    private String fullName;
    private String phone;
    private String email;
    private String status;

    public ReaderRecord(String readerCode, String username, String fullName,
                        String phone, String email, String status) {
        // Constructor gom tất cả field vào một object duy nhất.
        // Vì class này chưa có setter, sau khi tạo object thì dữ liệu được xem như "đọc là chính".
        this.readerCode = readerCode;
        this.username = username;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.status = status;
    }

    // Các getter bên dưới phục vụ hai việc:
    // 1. DAO lấy dữ liệu từ object để ghi xuống DB.
    // 2. Controller lấy dữ liệu từ object để hiển thị lên UI.
    public String getReaderCode() {
        return readerCode;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }
}
