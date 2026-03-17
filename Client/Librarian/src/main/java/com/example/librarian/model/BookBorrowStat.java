package com.example.librarian.model;

public class BookBorrowStat {

    private String title;
    private int total;

    public BookBorrowStat(String title, int total) {
        this.title = title;
        this.total = total;
    }

    public String getTitle() {
        return title;
    }

    public int getTotal() {
        return total;
    }
}
