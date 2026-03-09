package com.example.reader.model;

public class Book {
    private int bookId;
    private String title;
    private String isbn;
    private String publisher;
    private int publishYear;
    private String author;
    private String category;
    private String categoryGroup;

    public Book() {}

    public Book(int bookId, String title, String isbn, String publisher, int publishYear, String author, String category, String categoryGroup) {
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.author = author;
        this.category = category;
        this.categoryGroup = categoryGroup;
    }

    // Getter và Setter


    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryGroup() {
        return categoryGroup;
    }

    public void setCategoryGroup(String categoryGroup) {
        this.categoryGroup = categoryGroup;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", isbn='" + isbn + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publishYear=" + publishYear +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", categoryGroup='" + categoryGroup + '\'' +
                '}';
    }
}