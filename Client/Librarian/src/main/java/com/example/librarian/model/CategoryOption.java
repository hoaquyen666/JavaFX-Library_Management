package com.example.librarian.model;

public class CategoryOption {
    private int categoryId;
    private String categoryName;
    private String groupName;

    public CategoryOption(int categoryId, String categoryName, String groupName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.groupName = groupName;
    }

    public int getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }

    @Override
    public String toString() {
        // Trả về định dạng: "Thể loại - Nhóm thể loại" hiển thị trên ComboBox
        return categoryName + " - " + groupName;
    }
}
