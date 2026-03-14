package com.example.librarian.model;


public class Book {
    private int bookID;
    private String bookCode;
    private String title;
    private String isbn;
    private String publisher;
    private int publishYear;

    // Constructor
    public Book(int bookID, String bookCode, String title, String isbn, String publisher, int publishYear) {
        this.bookID = bookID;
        this.bookCode = bookCode;
        this.title = title;
        this.isbn = isbn;
        this.publisher = publisher;
        this.publishYear = publishYear;
    }

    // --- GETTERS & SETTERS
    public int getBookID() {
        return bookID;
    }
    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getBookCode() {
        return bookCode;
    }
    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getISBN() {
        return isbn;
    }
    public void setISBN(String isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublishYear() {
        return publishYear;
    }
    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }
}
