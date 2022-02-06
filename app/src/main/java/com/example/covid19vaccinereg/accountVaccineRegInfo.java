package com.example.covid19vaccinereg;

public class accountVaccineRegInfo {
    int UserId, age;
    String name, icPassportNo, phoneNo, state, emailAdd, PhysAddress;

    public accountVaccineRegInfo(int userId, int age, String name, String icPassportNo, String phoneNo, String state, String emailAdd, String physAddress) {
        UserId = userId;
        this.age = age;
        this.name = name;
        this.icPassportNo = icPassportNo;
        this.phoneNo = phoneNo;
        this.state = state;
        this.emailAdd = emailAdd;
        PhysAddress = physAddress;
    }

    public int getUserId() {
        return UserId;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public String getIcPassportNo() {
        return icPassportNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getState() {
        return state;
    }

    public String getEmailAdd() {
        return emailAdd;
    }

    public String getPhysAddress() {
        return PhysAddress;
    }
}
