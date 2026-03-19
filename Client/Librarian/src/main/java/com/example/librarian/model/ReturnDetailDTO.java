package com.example.librarian.model;


import java.time.LocalDate;

//Model cho trả sách
public class ReturnDetailDTO
{
    private int borrowId;
    private int readerId;
    private String readerName;
    private int copyId;
    private String copyCode;
    private String bookName;
    private double depositAmount;
    private double fineAmount;
    private String status;
    private LocalDate dueDate;

    public ReturnDetailDTO(int borrowId, int readerId, String readerName, int copyId, String copyCode,
                           String bookName, double depositAmount, double fineAmount, String status, LocalDate dueDate) {
        this.borrowId = borrowId;
        this.readerId = readerId;
        this.readerName = readerName;
        this.copyId = copyId;
        this.copyCode = copyCode;
        this.bookName = bookName;
        this.depositAmount = depositAmount;
        this.fineAmount = fineAmount;
        this.status = status;
        this.dueDate = dueDate;
    }

    // Các Getters bắt buộc cho TableView
    public int getBorrowId() { return borrowId; }
    public int getReaderId() { return readerId; }
    public String getReaderName() { return readerName; }
    public int getCopyId() { return copyId; }
    public String getCopyCode() { return copyCode; }
    public String getBookName() { return bookName; }
    public double getDepositAmount() { return depositAmount; }
    public double getFineAmount() { return fineAmount; }
    public String getStatus() { return status; }
    public LocalDate getDueDate() { return dueDate; }

    public String getDisplayCopy() { return " (ID:" + copyId + ")"; }

}
