package com.example.reader.model;

public class Book {
    private String bookCode;
    private String title;
    private String isbn;

    public Book() {}

    public Book(String bookCode, String title, String isbn) {
        this.bookCode = bookCode;
        this.title = title;
        this.isbn = isbn;
    }

    // Getter và Setter
    public String getBookCode() { return bookCode; }
    public void setBookCode(String bookCode) { this.bookCode = bookCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    @Override
    public String toString() {
        return "Mã: " + bookCode + " | Tên: " + title + " | ISBN: " + isbn;
    }
}