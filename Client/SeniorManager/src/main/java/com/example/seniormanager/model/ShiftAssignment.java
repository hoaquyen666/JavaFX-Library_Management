package com.example.seniormanager.model;

import java.sql.Time;
import java.time.LocalDate;

public class ShiftAssignment {
    private int assignmentId;
    private int librarianId;
    private int shiftId;
    private String shiftName; // Tên ca làm (VD: Ca Sáng) để hiện lên Lịch
    private LocalDate workDate;
    private boolean attendanceStatus;

    private Time startTime;
    private Time endTime;

    public ShiftAssignment(int assignmentId, int librarianId, int shiftId, String shiftName, LocalDate workDate, boolean attendanceStatus, Time startTime, Time endTime) {
        this.assignmentId = assignmentId;
        this.librarianId = librarianId;
        this.shiftId = shiftId;
        this.shiftName = shiftName;
        this.workDate = workDate;
        this.attendanceStatus = attendanceStatus;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getAssignmentId() { return assignmentId; }
    public int getLibrarianId() { return librarianId; }
    public int getShiftId() { return shiftId; }
    public String getShiftName() { return shiftName; }
    public LocalDate getWorkDate() { return workDate; }
    public boolean isAttendanceStatus() { return attendanceStatus; }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }
}