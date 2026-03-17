package com.example.librarian.model;

public class ReaderStat {

    private String name;
    private int total;

    public ReaderStat(String name, int total) {
        this.name = name;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public int getTotal() {
        return total;
    }
}