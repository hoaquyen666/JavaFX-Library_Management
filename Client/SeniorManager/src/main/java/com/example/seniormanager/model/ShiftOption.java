package com.example.seniormanager.model;

import java.sql.Time;

public class ShiftOption {
    private  int shiftId;
    private String shiftName;
    private Time startTime;
    private Time endTime;

    public ShiftOption(int shiftId, String shiftName, Time startTime, Time endTime) {
        this.shiftId = shiftId;
        this.shiftName = shiftName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getShiftId() {
        return shiftId;
    }

    @Override
    public String toString() {
        // Sẽ hiển thị trên ComboBox dạng: "Ca Sáng (08:00:00 - 12:00:00)"
        return shiftName + " (" + startTime + " - " + endTime + ")";
    }
}
