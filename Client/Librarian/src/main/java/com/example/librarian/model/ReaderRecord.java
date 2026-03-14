package com.example.librarian.model;

public class ReaderRecord {

    private String readerCode;
    private String username;
    private String fullName;
    private String phone;
    private String email;
    private String status;

    public ReaderRecord(String readerCode, String username, String fullName,
                        String phone, String email, String status) {
        this.readerCode = readerCode;
        this.username = username;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.status = status;
    }

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