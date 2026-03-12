package com.example.seniormanager.model;

public class StaffOption {
    private int staffId;
    private String displayString;

    public StaffOption(int staffId, String staffCode, String fullName) {
        this.staffId = staffId;
        // Hiển thị dạng: "NV01 - Nguyễn Văn A"
        this.displayString = staffCode + " - " + fullName;
    }

    public int getStaffId() {
        return staffId;
    }

    // JavaFX ComboBox sẽ tự động gọi hàm toString() này để hiển thị chữ lên giao diện
    @Override
    public String toString() {
        return displayString;
    }
}
