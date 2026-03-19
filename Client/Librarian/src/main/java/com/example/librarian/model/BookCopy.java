package com.example.librarian.model;
//Model cho quản lý bản sao
public class BookCopy {
    private int copyId;
    private int bookId;
    private String bookTitle;
    private String status;
    private String location;
    private String note;


    public BookCopy(int copyId, int bookId, String bookTitle, String status, String location, String note){
        this.bookId =bookId;
        this.copyId = copyId;
        this.bookTitle = bookTitle;
        this.status = status;
        this.location = location;
        this.note = note;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getCopyId() {
        return copyId;
    }

    public void setCopyId(int copyId) {
        this.copyId = copyId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
