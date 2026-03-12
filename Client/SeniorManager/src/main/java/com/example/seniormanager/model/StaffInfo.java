package com.example.seniormanager.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;

public class StaffInfo
{
    private int staffId;
    private String staffCode;
    private String fullName;
    private LocalDate DoB;
    private String email;
    private String phoneNumber;
    private String Role;


    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public StaffInfo() {
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDoB() {
        return DoB;
    }

    public void setDoB(LocalDate doB) {
        DoB = doB;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
