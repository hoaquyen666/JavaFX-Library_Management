package com.example.librarian.model;

public class Borrow {
    private int borrowId;
    private String borrowCode;
    private String readerCode; // Lấy từ bảng Reader nối sang
    private String staffCode;  // Lấy từ bảng Staff nối sang
    private String borrowDate;
    private String dueDate;
    private int quantity;
    private String status;

    public Borrow(int borrowId, String borrowCode, String readerCode, String staffCode,
                  String borrowDate, String dueDate, int quantity, String status) {
        this.borrowId = borrowId;
        this.borrowCode = borrowCode;
        this.readerCode = readerCode;
        this.staffCode = staffCode;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.quantity = quantity;
        this.status = status;
    }

    // getter & setter
    public int getBorrowId() { return borrowId; }
    public void setBorrowId(int borrowId) { this.borrowId = borrowId; }

    public String getBorrowCode() { return borrowCode; }
    public void setBorrowCode(String borrowCode) { this.borrowCode = borrowCode; }

    public String getReaderCode() { return readerCode; }
    public void setReaderCode(String readerCode) { this.readerCode = readerCode; }

    public String getStaffCode() { return staffCode; }
    public void setStaffCode(String staffCode) { this.staffCode = staffCode; }

    public String getBorrowDate() { return borrowDate; }
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
